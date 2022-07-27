package org.intellij.sdk.toolWindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import static java.awt.event.KeyEvent.VK_RIGHT;

public class NoteToolWindow {
    private JTextPane NotePanel;
    private JPanel NotePanelContent;
    private JScrollPane ScrollPane;

    private Project project;
    private final StyledDocument doc;

    private DocumentParser docParser;
    private Style defaultStyle;
    private NoteStorageManager manager;
    private boolean isInProgress;

    public final int NAME_MIN_LEN = 1;
    public final int NAME_MAX_LEN = 20;

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
        manager = new NoteStorageManager(project);
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
        doc.addDocumentListener(new NoteDocumentListener(this));
    }

    public void saveNote() {
        manager.setNoteText(NotePanel.getText());
    }

    /** called in toolWindowFactory to diplay tool window */
    public JPanel getContent() {
        return NotePanelContent;
    }

    public boolean isInProgress() {
        return isInProgress;
    }
    public void setInProgress(boolean isInProgress) {
        this.isInProgress = isInProgress;
    }

    public DocumentParser getDocParser() {
        return docParser;
    }

    public JTextPane getNotePanel() {
        return NotePanel;
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
