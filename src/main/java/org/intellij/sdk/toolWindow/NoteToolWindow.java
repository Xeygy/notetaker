package org.intellij.sdk.toolWindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import jnr.ffi.annotations.In;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class NoteToolWindow {
    private JTextPane NotePanel;
    private JPanel NotePanelContent;
    private JScrollPane ScrollPane;
    private Project project;
    private Document doc;

    public NoteToolWindow(ToolWindow toolWindow, Project project) {
        doc = NotePanel.getDocument();
        this.project = project;
        doc.addDocumentListener(new NoteDocumentListener());
        NotePanel.addMouseListener(new NoteMouseListener());
    }

    public JPanel getContent() {
        return NotePanelContent;
    }

    /** returns a list of the starting indices of a given
     * String in the note tool window Document
     * @param s a string to look for
     * @return all starting indices
     */
    public List<Integer> getStartOfStrings(String s) {
        ArrayList<Integer> starts = new ArrayList<>();
        for (int i = 0; i <= doc.getLength() - s.length(); i++) {
            try {
                if (doc.getText(i, s.length()).equals(s)) {
                    starts.add(i);
                }
            } catch (Exception e) {
                System.out.println("getStartOfStringsError");
            }
        }
        return starts;
    }

    /** gets content inside braces (i.e. \{content})
     * @param start starting index of the `\`
     * @param minContentLen the minimum valid length
     *                      for content (must be positive)
     * @param maxContentLen the maximum valid length for
     *                      content
     * @return null if can't find content, else returns content
     */
    public String getContentInCurlyBraces(int start, int minContentLen, int maxContentLen) {
        int wordStart = start + 2;
        for (int currLen = minContentLen; currLen <= maxContentLen; currLen++) {
            try {
                int closingBraceLoc = wordStart + currLen;
                String test = doc.getText(closingBraceLoc, 1);
                if (test.equals("}")) {
                    return doc.getText(wordStart, currLen);
                }
            } catch (Exception e) {
                break;
            }
        }
        return null;
    }

    class NoteDocumentListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            System.out.println(e);
            List<Integer> starts = getStartOfStrings("\\{");
            for (int start : starts) {
                System.out.println(getContentInCurlyBraces(start, 1, 20));
            }
        }
        public void removeUpdate(DocumentEvent e) {
            System.out.println(e);
        }
        public void changedUpdate(DocumentEvent e) {
            //Plain text components do not fire these events
        }
    }

    class NoteMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            Point mousePt = e.getPoint();
            int offset = NotePanel.viewToModel2D(mousePt);
            List<Integer> starts = getStartOfStrings("\\{");
            if (starts.contains(offset)) {
                String def = getContentInCurlyBraces(offset, 1, 20);
                FindMethodProcessor processor = new FindMethodProcessor(def);
                processor.runProcessor(project);
                processor.goToFoundMethods();
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}
