package org.intellij.sdk.toolWindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiMethod;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.util.List;

import static java.awt.event.KeyEvent.VK_RIGHT;

public class NoteToolWindow {
    private JTextPane NotePanel;
    private JPanel NotePanelContent;
    private JCheckBox IsEditable;
    private JButton saveButton;
    private JScrollPane ScrollPane;
    private Project project;
    private final StyledDocument doc;
    private DocumentParser docParser;
    private Style defaultStyle;
    private NoteStorageManager manager;
    private boolean isInProgress;

    private final int NAME_MIN_LEN = 1;
    private final int NAME_MAX_LEN = 20;

    /** @see DocumentParser#getStartOfStrings(String)  getStart
     * {@link FindMethodVisitor#visitMethod(PsiMethod)} */
    public NoteToolWindow(ToolWindow toolWindow, Project project) {
        this.project = project;
        manager = new NoteStorageManager(project);
        IsEditable.addActionListener(e -> toggleEditability(e));

        saveButton.addActionListener(e -> saveNote());
        EditorKit k = new CustomHTMLEditorKit();
        NotePanel.setEditorKit(k);
        NotePanel.addKeyListener(new KeyListener() {
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
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        if (manager.getNoteText() != null) {
            NotePanel.setText(manager.getNoteText());
            System.out.println("from save");
        }
        //set notepanel text styling to intellij defaults
        NotePanel.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        defaultStyle = NotePanel.getLogicalStyle();

        NotePanel.addHyperlinkListener(getLinkListener());
        doc = NotePanel.getStyledDocument();
        docParser = new DocumentParser(doc);
        doc.addDocumentListener(new NoteDocumentListener());
    }

    public void saveNote() {
        manager.setNoteText(NotePanel.getText());
    }

    public void toggleEditability(ActionEvent e) {
        NotePanel.setEditable(!NotePanel.isEditable());
    }

    public JPanel getContent() {
        return NotePanelContent;
    }

    /** run the autoLink process every time the doc is updated */
    class NoteDocumentListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            System.out.println(e);
            autoLink(e);
            saveNote();
            List<Integer> starts = docParser.getStartOfStrings("\\{");
            for (int start : starts) {
                System.out.println(docParser.getContentInCurlyBraces(start, NAME_MIN_LEN, NAME_MAX_LEN));
            }
        }
        public void removeUpdate(DocumentEvent e) {
            System.out.println(e);
        }
        public void changedUpdate(DocumentEvent e) {
            //Plain text components do not fire these events
        }
    }


    /** code taken from here: https://stackoverflow.com/questions/12035925/java-jeditorpane-hyperlink-wont-exit-tag */
    private void autoLink(DocumentEvent e) {
        Runnable autoLink = new Runnable() {
            public void run() {
                // checks that the insert is a single char (not copy-pasted) and autolink is not running
                // checks for instance of HTMLDocument
                if (e.getDocument() instanceof HTMLDocument
                        && e.getOffset() > 0
                        && e.getLength() == 1
                        && !isInProgress) {
                    try {
                        // casts doc to HTMLDocument
                        HTMLDocument doc = (HTMLDocument) e.getDocument();
                        String text = doc.getText(e.getOffset(), e.getLength());
                        // if whitespace just entered, check word before the whitespace to update
                        if (text.charAt(0) == ' ' || text.charAt(0) == '\n' || text.charAt(0) == '\t') {
                            // get text of word before whitespace
                            //int start = Utilities.getWordStart(NotePanel, e.getOffset() - 1);
                            int start = getBetterWordStart(NotePanel, e.getOffset() - 1);
                            text = doc.getText(start, e.getOffset() - start);
                            if (text.startsWith("\\{") && text.endsWith("}")) {
                                isInProgress = true;
                                HTMLEditorKit kit = (HTMLEditorKit) NotePanel.getEditorKit();
                                //the next 3 lines are necessary to create separate text elem
                                //to be replaced with link
                                // TODO: figure out why this works
                                SimpleAttributeSet a = new SimpleAttributeSet();
                                a.addAttribute("DUMMY_ATTRIBUTE_NAME", "DUMMY_ATTRIBUTE_VALUE");
                                doc.setCharacterAttributes(start, text.length(), a, false);

                                Element elem = doc.getCharacterElement(start);
                                String innerText = text.substring(2, text.length()-1);
                                if (innerText.length() > 0) {
                                    String html = "<a loc-id='" + innerText + "' href='#'>" + innerText + "</a>";
                                    doc.setOuterHTML(elem, html);
                                }
                                isInProgress = false;
                            }
                        }
                    } catch (BadLocationException e1) {
                        e1.printStackTrace();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
        SwingUtilities.invokeLater(autoLink);
    }

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

    /** on link click, go to loc-id */
    public HyperlinkListener getLinkListener() {
        HyperlinkListener listener = new HyperlinkListener() {
            public void hyperlinkUpdate (HyperlinkEvent e){
                System.out.println("hyperlinkUpdate received");
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    AttributeSet attrs = e.getSourceElement().getAttributes();
                    SimpleAttributeSet simpleAttributeSet = (SimpleAttributeSet) attrs.getAttribute(HTML.Tag.A);
                    String locId = (String) simpleAttributeSet.getAttribute("loc-id");
                    if (locId != null) {
                        FindMethodProcessor processor = new FindMethodProcessor(locId);
                        processor.runProcessor(project);
                        processor.goToFoundMethods();
                    }
                }
            }
        };
        return listener;
    }
}
