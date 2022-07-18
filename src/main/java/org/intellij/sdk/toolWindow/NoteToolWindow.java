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
import java.util.List;

public class NoteToolWindow {
    private JTextPane NotePanel;
    private JPanel NotePanelContent;
    private JScrollPane ScrollPane;
    private Project project;
    private Document doc;
    private DocumentParser docParser;

    private final int NAME_MIN_LEN = 1;
    private final int NAME_MAX_LEN = 20;

    public NoteToolWindow(ToolWindow toolWindow, Project project) {
        doc = NotePanel.getDocument();
        docParser = new DocumentParser(doc);
        this.project = project;
        doc.addDocumentListener(new NoteDocumentListener());
        NotePanel.addMouseListener(new NoteMouseListener());
    }

    public JPanel getContent() {
        return NotePanelContent;
    }


    class NoteDocumentListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            System.out.println(e);
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

    class NoteMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            Point mousePt = e.getPoint();
            int offset = NotePanel.viewToModel2D(mousePt);
            List<OffsetRange> ranges = docParser.getBracedContentRanges(NAME_MIN_LEN, NAME_MAX_LEN);
            OffsetRange selectedRange = OffsetRange.getRangeWith(ranges, offset);
            if (selectedRange != null) {
                String def = docParser.getContentInRange(selectedRange);
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
