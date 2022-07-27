package org.intellij.sdk.toolWindow;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.io.IOException;
import java.util.List;

/**
 * run:
 *  autoLink
 *  saveNote
 * every time the doc is updated
 */
class NoteDocumentListener implements DocumentListener {
    private final NoteToolWindow noteToolWindow;

    public NoteDocumentListener(NoteToolWindow noteToolWindow) {
        this.noteToolWindow = noteToolWindow;
    }

    public void insertUpdate(DocumentEvent e) {
        System.out.println(e);
        autoLink(e);
        noteToolWindow.saveNote();
        List<Integer> starts = noteToolWindow.getDocParser().getStartOfStrings("\\{");
        for (int start : starts) {
            System.out.println(noteToolWindow.getDocParser().getContentInCurlyBraces(start, noteToolWindow.NAME_MIN_LEN, noteToolWindow.NAME_MAX_LEN));
        }
    }

    public void removeUpdate(DocumentEvent e) {
        System.out.println(e);
    }

    public void changedUpdate(DocumentEvent e) {
        //Plain text components do not fire these events
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
                        if (text.charAt(0) == ' ' || text.charAt(0) == '\n' || text.charAt(0) == '\t') {
                            // get text of word before whitespace
                            //int start = Utilities.getWordStart(NotePanel, e.getOffset() - 1);
                            int start = getBetterWordStart(noteToolWindow.getNotePanel(), e.getOffset() - 1);
                            text = doc.getText(start, e.getOffset() - start);
                            if (text.startsWith("\\{") && text.endsWith("}")) {
                                noteToolWindow.setInProgress(true);
                                HTMLEditorKit kit = (HTMLEditorKit) noteToolWindow.getNotePanel().getEditorKit();

                                //the next 3 lines are necessary to create separate text elem
                                //to be replaced with link
                                SimpleAttributeSet a = new SimpleAttributeSet();
                                a.addAttribute("DUMMY_ATTRIBUTE_NAME", "DUMMY_ATTRIBUTE_VALUE");
                                doc.setCharacterAttributes(start, text.length(), a, false);

                                Element elem = doc.getCharacterElement(start);
                                String innerText = text.substring(2, text.length()-1);
                                if (innerText.length() > 0) {
                                    String html = "<a loc-id='" + innerText + "' href='#'>" + innerText + "</a>";
                                    doc.setOuterHTML(elem, html);
                                }
                                noteToolWindow.setInProgress(false);
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
