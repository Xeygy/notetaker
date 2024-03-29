package org.intellij.sdk.notetaker.window.texteditor;

import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.psi.PsiMethod;
import org.intellij.sdk.notetaker.storage.NoteModel;
import org.intellij.sdk.notetaker.visitors.FindIndividualMethodProcessor;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.*;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Iterator;

import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_SPACE;

/**
 * Text Editor window for a single note, represented by a NoteModel.
 * GUI done in NoteWindow.form
 */
public class NoteWindow {
    private JTextPane NotePanel;
    private JPanel NotePanelContent;
    private JScrollPane ScrollPane;

    private Project project;
    private final StyledDocument doc;

    public Style defaultStyle;
    private NoteModel model;
    private NoteDocumentListener docListener;
    private boolean isInProgress;
    private HashMap<String, MethodWrapper> links;

    /**
     * Creates instance of NoteWindow, sets the styling and loads the NoteModel into the window.
     * @param project the project that this NoteWindow works on
     * @param model the NoteModel that this window edits
     */
    public NoteWindow(Project project, NoteModel model) {
        this.project = project;
        this.model = model;
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

        // load note into text editor
        NotePanel.setText(model.getContent());
        links = new HashMap<>();
        doc = NotePanel.getStyledDocument();

        // add Listeners
        docListener = new NoteDocumentListener(this);
        doc.addDocumentListener(docListener);
        NotePanel.addKeyListener(getKeyListener());
        NotePanel.addHyperlinkListener(getLinkListener());
    }

    public void saveNote() {
        model.setContent(NotePanel.getText());
    }

    /** called in toolWindowFactory to diplay tool window */
    public JPanel getContent() {
        return NotePanelContent;
    }

    /* getters & setters */
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

    /** activated on any link click and navigates focus to loc-id, if present*/
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
                        if (currMethod == null) {
                            currMethod = findIndividualMethod(locId);
                        }
                        if (currMethod != null) {
                            links.put(locId, currMethod);
                            currMethod.goToMethod();
                        }
                    }
                }
            }
        };
        return listener;
    }

    public KeyListener getKeyListener() {
        return new KeyAdapter() {

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
        };
    }
    /**
     * currently just adds a space at the end of the link https://stackoverflow.com/a/12046827.
     * only works if you're at the end of the document. helps the user escape link formatting
     * while in the text editor
     * */
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

    /**
     * Searches for a Method in the porject based on a given locId
     * @param locId the method signature to look for
     * @return the MethodWrapper corresponding to the locId, else
     * null if locId doesn't correspond to a method
     */
    public MethodWrapper findIndividualMethod(String locId) {
        String enclosingClass = "";
        String methodName = "";
        String params = "";

        String[] paramSplit = locId.split("#");
        if (paramSplit.length > 1) {
            params = paramSplit[1];
        }
        int dotIndex = paramSplit[0].lastIndexOf('.');
        if (dotIndex >= 0) {
            enclosingClass = paramSplit[0].substring(0, dotIndex);
            methodName = paramSplit[0].substring(dotIndex + 1);
        } else {
            methodName = paramSplit[0];
        }

        FindIndividualMethodProcessor processor = new FindIndividualMethodProcessor(enclosingClass, methodName, params, project);
        processor.runProcessor();
        Iterator<PsiMethod> foundMethods = processor.getFoundMethods().stream().iterator();
        return foundMethods.hasNext() ? new MethodWrapper(foundMethods.next()) : null;
    }

}
