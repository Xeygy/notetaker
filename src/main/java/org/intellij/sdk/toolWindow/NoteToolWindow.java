package org.intellij.sdk.toolWindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.thoughtworks.qdox.model.expression.Not;
import jnr.ffi.annotations.In;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public class NoteToolWindow {
    private JTextPane NotePanel;
    private JPanel NotePanelContent;
    private JScrollPane ScrollPane;
    private Project project;
    private StyledDocument doc;
    private DocumentParser docParser;
    private Style linkStyle;
    private Style defaultStyle;

    private final int NAME_MIN_LEN = 1;
    private final int NAME_MAX_LEN = 20;

    public NoteToolWindow(ToolWindow toolWindow, Project project) {
        this.project = project;
        doc = NotePanel.getStyledDocument();
        NotePanel.getText();

        //Text Styling
        defaultStyle = NotePanel.getLogicalStyle();
        linkStyle = NotePanel.addStyle("Link Style", null);
        StyleConstants.setForeground(linkStyle, new Color(128, 189, 255));
        StyleConstants.setUnderline(linkStyle, true);
        StyleConstants.setItalic(linkStyle, true);

        docParser = new DocumentParser(doc);
        doc.addDocumentListener(new NoteDocumentListener());
        NotePanel.addMouseListener(new NoteMouseListener());
    }

    public JPanel getContent() {
        return NotePanelContent;
    }


    class NoteDocumentListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            styleLinks();
            System.out.println(e);
            List<Integer> starts = docParser.getStartOfStrings("\\{");
            for (int start : starts) {
                System.out.println(docParser.getContentInCurlyBraces(start, NAME_MIN_LEN, NAME_MAX_LEN));
            }
        }
        public void removeUpdate(DocumentEvent e) {
            styleLinks();
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

                //clears styling first
                doc.setCharacterAttributes(0, doc.getLength() + 1, defaultStyle, true);

                for (OffsetRange range : linkRanges) {
                    String linkText = docParser.getContentInRange(range);
                    try {
                        doc.setCharacterAttributes(range.getStart(), range.size(), linkStyle, false);
                    } catch (Exception exception) {
                        System.out.println(exception);
                    }
                }
            }
        };
        SwingUtilities.invokeLater(doHighlight);
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
                String[] signature = def.split("\\.");
                FindMethodProcessor processor;
                if (signature.length == 2) {
                     processor = new FindMethodProcessor(signature[0], signature[1]);
                } else {
                    processor = new FindMethodProcessor(def);
                }
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
