package org.intellij.sdk.notetaker;

import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;
import org.intellij.sdk.notetaker.visitors.FindMethodProcessor;
import org.intellij.sdk.notetaker.window.NoteToolWindow;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * run:
 *  autoLink
 *  saveNote
 * every time the doc is updated
 */
public class NoteDocumentListener implements DocumentListener {
    private final NoteToolWindow noteToolWindow;
    private JMenuItem selectedItem;
    private JPopupMenu pm;

    public NoteDocumentListener(NoteToolWindow noteToolWindow) {
        this.noteToolWindow = noteToolWindow;
    }

    public void insertUpdate(DocumentEvent e) {
        System.out.println(e);
        hidePopup();
        autoLink(e);
        noteToolWindow.saveNote();
    }

    public void removeUpdate(DocumentEvent e) {
        System.out.println(e);
    }

    public void changedUpdate(DocumentEvent e) {
        //Plain text components do not fire these events
    }

    private void popUp(HashSet<PsiMethod> foundMethods, int start, HTMLDocument doc) {
        pm = new JPopupMenu("Autocomplete");
        JTextPane textPane = noteToolWindow.getNotePanel();
        for (PsiMethod method : foundMethods) {
            ArrayList params = new ArrayList<String>();
            for (PsiParameter param : method.getParameterList().getParameters()) {
                params.add(param.getType().getCanonicalText());
            }

            String locId = method.getContainingClass().getQualifiedName() + "." + method.getName() + "#" + String.join(",", params);
            JMenuItem menuItem = new JMenuItem(locId);
            menuItem.addChangeListener(new ChangeListener() {
                @Override
                public void stateChanged(ChangeEvent e) {
                    selectedItem = getSelected(pm);
                }
            });
            menuItem.addActionListener(e -> createLink(start, method, method.getName(), doc));
            pm.add(menuItem);
        }
        if (foundMethods.isEmpty()) {
            JMenuItem menuItem = new JMenuItem("No methods found.");
            pm.add(menuItem);
        }

        // add the popup to the frame
        int offset = textPane.getCaretPosition();
        // for displacing the popUp window
        int fontSize = textPane.getFont().getSize();
        try {
            Rectangle2D r = textPane.modelToView2D(offset);
            pm.show(noteToolWindow.getNotePanel(), (int)r.getX(), (int)r.getY() + 3*fontSize/2);
            noteToolWindow.getNotePanel().requestFocusInWindow();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void select() {
        if (selectedItem != null) {
            selectedItem.doClick();
        }
    }
    public static JMenuItem getSelected(JPopupMenu menu) {
        int itemCount = menu.getComponentCount();
        for (int i = 0; i < itemCount; i++) {
            Component component = menu.getComponent(i);
            if (menu.getComponent(i) instanceof JMenuItem) {
                JMenuItem currItem = (JMenuItem) component;
                if(currItem.isArmed()) {
                    return currItem;
                }
            }
        }
        return null;
    }

    public void hidePopup() {
        if (pm != null) {
            pm.setVisible(false);
        }
    }
    /**
     * checks that the character entered by DocumentEvent e
     * is a whitespace character, and the word preceding that
     * is in the format \{content}.
     *
     * if so, reformats the content into a link.
     *
     * code taken from here: https://stackoverflow.com/questions/12035925/java-jeditorpane-hyperlink-wont-exit-tag */
    private void autoLink(DocumentEvent e) {
        Runnable autoLink = new Runnable() {
            public void run() {
                // checks that the insert is a single char (not copy-pasted) and autolink is not running
                // checks for instance of HTMLDocument
                if (e.getDocument() instanceof HTMLDocument
                        && e.getOffset() > 0
                        && e.getLength() == 1
                        && !noteToolWindow.isInProgress()) {
                    try {
                        // casts doc to HTMLDocument
                        HTMLDocument doc = (HTMLDocument) e.getDocument();
                        String text = doc.getText(e.getOffset(), e.getLength());
                        // if whitespace just entered, check word before the whitespace to update
                        if (true || text.charAt(0) == ' ' || text.charAt(0) == '\n' || text.charAt(0) == '\t') {
                            // get text of word before whitespace
                            //int start = Utilities.getWordStart(NotePanel, e.getOffset() - 1);
                            int start = getBetterWordStart(noteToolWindow.getNotePanel(), e.getOffset());
                            text = doc.getText(start, e.getOffset() - start + 1);
                            if (text.startsWith("\\{") && text.length() >= 2 + 2) {
                                String innerText = text.substring(2);
                                FindMethodProcessor processor = new FindMethodProcessor(innerText, noteToolWindow.getProject());
                                processor.runProcessor();
                                processor.printFoundMethods();
                                popUp(processor.getFoundMethods(), start, doc);
                                //createLink(start, text, doc);
                            }
                        }
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
        SwingUtilities.invokeLater(autoLink);
    }

    /** creates a link with loc-id text */
    public void createLink(int start, PsiMethod method, String displayName, HTMLDocument doc) {
        noteToolWindow.setInProgress(true);
        HTMLEditorKit kit = (HTMLEditorKit) noteToolWindow.getNotePanel().getEditorKit();

        // add link to toolWindow.links
        MethodWrapper currMethod = new MethodWrapper(method);
        String locId = currMethod.getLocId();
        noteToolWindow.getLinks().put(locId, currMethod);

        //the next 3 lines are necessary to create separate text elem
        //to be replaced with link
        SimpleAttributeSet a = new SimpleAttributeSet();
        a.addAttribute("DUMMY_ATTRIBUTE_NAME", "DUMMY_ATTRIBUTE_VALUE");
        doc.setCharacterAttributes(start, locId.length() + 2, a, false);

        Element elem = doc.getCharacterElement(start);
        if (locId.length() > 0) {
            String html = "<a loc-id='" + locId + "' href='#'>" + displayName + "</a>";
            try {
                doc.setOuterHTML(elem, html);
                doc.insertString(start+displayName.length(), " ", noteToolWindow.defaultStyle);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        noteToolWindow.setInProgress(false);
    }

    /**
     * custom definition of a word to be any sequence of
     * characters that doesn't contain whitespace.
     */
    public static int getBetterWordStart(JTextPane pane, int offset) {
        Document doc = pane.getDocument();
        for (; offset >= 0; offset--) {
            try {
                String currSpot = doc.getText(offset, 1);
                if (currSpot.matches("\\s")) {
                    return offset + 1;
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}
