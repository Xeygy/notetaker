package org.intellij.sdk.notetaker.window;

import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiMethod;
import org.intellij.sdk.notetaker.*;
import org.intellij.sdk.notetaker.visitors.FindMethodProcessor;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static java.awt.event.KeyEvent.*;

public class NoteToolWindow {
    private JTextPane NotePanel;
    private JPanel NotePanelContent;
    private JScrollPane ScrollPane;

    private Project project;
    private final StyledDocument doc;

    public Style defaultStyle;
    private NoteStorageManager manager;
    private NoteDocumentListener docListener;
    private boolean isInProgress;
    private HashMap<String, MethodWrapper> links;

    /**
     * Sets up instance vars
     * project
     * manager (for saves)
     *      - loading saves
     * editorKit
     * styling
     * defaultstyle
     * doc
     *
     * KeyListener (for escaping hyperlinks)
     * HyperLinkListener
     * DocParser (for finding links)
     * */
    public NoteToolWindow(ToolWindow toolWindow, Project project) {
        this.project = project;

        HTMLEditorKit kit = new CustomHTMLEditorKit();
        NotePanel.setEditorKit(kit);

        // set styling
        NotePanel.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        defaultStyle = NotePanel.getLogicalStyle();
        EditorColorsScheme colorsScheme = EditorColorsManager.getInstance().getGlobalScheme();
        // colors from https://stackoverflow.com/q/66894675 (check comments also)
        Color linkColor = colorsScheme.getAttributes(CodeInsightColors.HYPERLINK_ATTRIBUTES).getForegroundColor();
        Color textPaneBg = colorsScheme.getDefaultBackground();
        NotePanel.setBackground(textPaneBg);
        String rgb = "rgb("
                + linkColor.getRed() + ", "
                + linkColor.getGreen() + ", "
                + linkColor.getBlue() +
                ")";
        StyleSheet css = kit.getStyleSheet();
        css.addRule("a { color: " + rgb + ";}");

        // load existing note if it exists
        manager = new NoteStorageManager(project);
        if (manager.getNoteText() != null) {
            NotePanel.setText(manager.getNoteText());
            String s = manager.getNoteText();
            System.out.println("from save");
        }
        //findLinks();
        links = new HashMap<>();


        doc = NotePanel.getStyledDocument();

        // add Listeners
        docListener = new NoteDocumentListener(this);
        doc.addDocumentListener(docListener);
        NotePanel.addKeyListener(getKeyListener());
        NotePanel.addHyperlinkListener(getLinkListener());
    }

    public void saveNote() {
        manager.setNoteText(NotePanel.getText());
    }

    /** called in toolWindowFactory to diplay tool window */
    public JPanel getContent() {
        return NotePanelContent;
    }

    /** getters & setters */
    public boolean isInProgress() {
        return isInProgress;
    }
    public void setInProgress(boolean isInProgress) {
        this.isInProgress = isInProgress;
    }
    public JTextPane getNotePanel() {
        return NotePanel;
    }
    public Project getProject() {
        return project;
    }
    public HashMap<String, MethodWrapper> getLinks() {
        return links;
    }

    /** on link click, go to loc-id */
    public HyperlinkListener getLinkListener() {
        HyperlinkListener listener = new HyperlinkListener() {
            public void hyperlinkUpdate (HyperlinkEvent e){
                System.out.println("hyperlinkUpdate received");
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    AttributeSet attrs = e.getSourceElement().getAttributes();
                    SimpleAttributeSet simpleAttributeSet = (SimpleAttributeSet) attrs.getAttribute(HTML.Tag.A);
                    String locId = (String) simpleAttributeSet.getAttribute("loc-id");
                    System.out.println(locId);
                    if (locId != null) {
                        MethodWrapper currMethod = links.get(locId);
                        if (currMethod != null) {
                            currMethod.goToMethod();
                        }
                    }
                }
            }
        };
        return listener;
    }

    public KeyListener getKeyListener() {
        return new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println(e);
                if (e.getKeyCode() == VK_RIGHT) {
                    escapeLink();
                    System.out.println("escape!");
                }
                if (e.getKeyCode() == VK_SPACE) {
                    docListener.select();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };
    }
    /** currently just adds a space at the end of the link https://stackoverflow.com/a/12046827
     * only works if you're at the end of the document.*/
    public void escapeLink() {
        int caretPos = NotePanel.getCaretPosition();
        Element elem = doc.getParagraphElement(caretPos);

        int pos = elem.getEndOffset() - 1;
        int max = doc.getLength();
        if (caretPos >= max) {
            try {
                doc.insertString(pos, " ", defaultStyle);
                NotePanel.setCaretPosition(pos);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

//
//
//    public HashMap<String, MethodWrapper> findLinks() {
//        NotePanel.getText();
//        Document d = NotePanel.getDocument();
//        HashMap<String, MethodWrapper> result = new HashMap<>();
//        if (d instanceof HTMLDocument) {
//            HTMLDocument htmlDocument = (HTMLDocument) d;
//            HTMLDocument.Iterator linkIterator = htmlDocument.getIterator(HTML.Tag.A);
//            Set<String> locIds = new HashSet<>();
//            // get all loc ids in the document
//            while (linkIterator.isValid()) {
//                String locId = (String) linkIterator.getAttributes().getAttribute("loc-id");
//                if (locId != null) {
//                    locIds.add(locId);
//                }
//                linkIterator.next();
//            }
//            for (String id : locIds) {
//                MethodWrapper wrapper = findIndividualMethod(id);
//                if (wrapper != null) {
//                    result.put(id, wrapper);
//                }
//            }
//        }
//        return result;
//    }
//
//    public MethodWrapper findIndividualMethod(String locId) {
//        String enclosingClass = "";
//        String methodName = null;
//        String params = null;
//
//        String[] paramSplit = locId.split("#");
//        if (paramSplit.length > 1) {
//            params = paramSplit[1];
//        }
//        int dotIndex = paramSplit[0].lastIndexOf('.');
//        if (dotIndex >= 0) {
//            enclosingClass = paramSplit[0].substring(0, dotIndex);
//            methodName = paramSplit[0].substring(dotIndex + 1);
//        } else {
//            methodName = paramSplit[0];
//        }
//
//        FindMethodProcessor processor = new FindMethodProcessor(enclosingClass, methodName, params, project);
//        processor.runProcessor();
//        Iterator<PsiMethod> foundMethods = processor.getFoundMethods().stream().iterator();
//        return foundMethods.hasNext() ? new MethodWrapper(foundMethods.next()) : null;
//    }
//
}
