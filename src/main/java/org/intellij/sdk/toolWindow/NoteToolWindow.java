package org.intellij.sdk.toolWindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiMethod;
import com.thoughtworks.qdox.model.expression.Not;

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
import java.io.IOException;
import java.util.List;

public class NoteToolWindow {
    private JTextPane NotePanel;
    private JPanel NotePanelContent;
    private JCheckBox IsEditable;
    private JButton EscLinkButton;
    private JScrollPane ScrollPane;
    private Project project;
    private final StyledDocument doc;
    private DocumentParser docParser;
    private Style defaultStyle;
    private boolean isInProgress;

    private final int NAME_MIN_LEN = 1;
    private final int NAME_MAX_LEN = 20;

    /** @see DocumentParser#getStartOfStrings(String)  getStart
     * {@link FindMethodVisitor#visitMethod(PsiMethod)} */
    public NoteToolWindow(ToolWindow toolWindow, Project project) {
        this.project = project;
        IsEditable.addActionListener(e -> toggleEditability(e));
        EscLinkButton.addActionListener(e -> escapeLink(e));
        EditorKit k = new CustomHTMLEditorKit();
        NotePanel.setEditorKit(k);

        //set notepanel text styling to intellij defaults
        NotePanel.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        defaultStyle = NotePanel.getLogicalStyle();

        //NotePanel.setText("<a loc-id=\"DocumentParser#getStartOfStrings(String)\" href=\"http://www.google.com/finance?q=NYSE:C\">Click this link</a> aa");

        NotePanel.addHyperlinkListener(getLinkListener());
        doc = NotePanel.getStyledDocument();
        docParser = new DocumentParser(doc);
        doc.addDocumentListener(new NoteDocumentListener());
    }

    public void toggleEditability(ActionEvent e) {
        NotePanel.setEditable(!NotePanel.isEditable());
        if (NotePanel.isEditable()) {
            System.out.println("Text now editable");
        } else {
            System.out.println("Text now not editable");
        }
    }

    public JPanel getContent() {
        return NotePanelContent;
    }

    /** run the autoLink process every time the doc is updated */
    class NoteDocumentListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            System.out.println(e);
            System.out.println("ew");
            autoLink(e);
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

    /** Styling must be queued, else we get an exception & styling doesn't work.
     * will detect all hyperlinks in the format and style them according to linkStyle
     * code from: https://stackoverflow.com/questions/15206586/getting-attempt-to-mutate-notification-exception
     */
    private void styleLinks() {
        Runnable doHighlight = new Runnable() {
            @Override
            public void run() {
                List<OffsetRange> linkRanges = docParser.getBracedContentRanges(NAME_MIN_LEN, NAME_MAX_LEN);
                for (OffsetRange range : linkRanges) {
                    String linkText = docParser.getContentInRange(range);
                    try {
                        doc.setCharacterAttributes(range.getStart(), range.size(), defaultStyle, false);
                    } catch (Exception exception) {
                        System.out.println(exception);
                    }
                }
            }
        };
        SwingUtilities.invokeLater(doHighlight);
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

    /** currently just adds a space at the end of the link https://stackoverflow.com/a/12046827 */
    public void escapeLink(ActionEvent e) {
        int caretPos = NotePanel.getCaretPosition();

        Element elem = doc.getParagraphElement(caretPos);

        int pos = elem.getEndOffset() - 1;
        int max = doc.getLength();
        if (pos >= max) {
            try {
                doc.insertString(pos, " ", defaultStyle);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        NotePanel.setCaretPosition(pos);
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
