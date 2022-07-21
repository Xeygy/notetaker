package org.intellij.sdk.toolWindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiMethod;

import javax.print.attribute.Attribute;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.List;

public class NoteToolWindow {
    private JTextPane NotePanel;
    private JPanel NotePanelContent;
    private JCheckBox IsEditable;
    private JScrollPane ScrollPane;
    private Project project;
    private StyledDocument doc;
    private DocumentParser docParser;
    private Style linkStyle;

    private final int NAME_MIN_LEN = 1;
    private final int NAME_MAX_LEN = 20;

    /** @see DocumentParser#getStartOfStrings(String)  getStart
     * {@link FindMethodVisitor#visitMethod(PsiMethod)} */
    public NoteToolWindow(ToolWindow toolWindow, Project project) {
        this.project = project;
        doc = NotePanel.getStyledDocument();
        IsEditable.addActionListener(e -> toggleEditability(e));
        EditorKit k = new CustomHTMLEditorKit();
        NotePanel.setEditorKit(k);

        NotePanel.setText("<a loc-id=\"DocumentParser#getStartOfStrings(String)\" href=\"http://www.google.com/finance?q=NYSE:C\">Click this link</a>");
        NotePanel.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                System.out.println("hyperlinkUpdate received");
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                   AttributeSet attrs = e.getSourceElement().getAttributes();
                   SimpleAttributeSet simpleAttributeSet = (SimpleAttributeSet) attrs.getAttribute(HTML.Tag.A);
                   String locId = (String) simpleAttributeSet.getAttribute("loc-id");
                   if (locId != null) {
                       System.out.println(locId);
                   }
                }
            }
        });
        NotePanel.getText();

        linkStyle = NotePanel.addStyle("Link Style", null);
        StyleConstants.setForeground(linkStyle, new Color(128, 189, 255));
        StyleConstants.setUnderline(linkStyle, true);
        StyleConstants.setItalic(linkStyle, true);

        docParser = new DocumentParser(doc);
        doc.addDocumentListener(new NoteDocumentListener());
        NotePanel.addMouseListener(new NoteMouseListener());
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
