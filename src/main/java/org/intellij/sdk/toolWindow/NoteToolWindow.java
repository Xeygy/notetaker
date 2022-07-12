package org.intellij.sdk.toolWindow;

import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class NoteToolWindow {
    private JTextPane NotePanel;
    private JPanel NotePanelContent;
    private JScrollPane ScrollPane;

    public NoteToolWindow(ToolWindow toolWindow) {
        NotePanel.getDocument().addDocumentListener(new NoteDocumentListener());

        System.out.println("hello!!");

    }
    public JPanel getContent() {
        return NotePanelContent;
    }

    class NoteDocumentListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            System.out.println(e);
        }
        public void removeUpdate(DocumentEvent e) {
            System.out.println(e);
        }
        public void changedUpdate(DocumentEvent e) {
            //Plain text components do not fire these events
        }
    }
}
