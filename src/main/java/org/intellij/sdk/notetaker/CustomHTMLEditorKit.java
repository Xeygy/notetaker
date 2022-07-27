package org.intellij.sdk.notetaker;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
/** Adds hyperlink clicking functionality from an editable
 * JTextPane.
 * Solution from:
 * https://stackoverflow.com/questions/31906569/add-hyperlinklistener-on-editable-editorpane */
public class CustomHTMLEditorKit extends HTMLEditorKit {
//    Maybe for hover cursor changing?
//    public boolean isNeedCursorChange=true;
//    JTextPane edit=new JTextPane() {
//        public void setCursor(Cursor cursor) {
//            if (isNeedCursorChange) {
//                super.setCursor(cursor);
//            }
//        }
//    };
    MyLinkController handler=new MyLinkController();
    public void install(JEditorPane c) {
        /* get mouse/motion listeners we want to preserve */
        MouseListener[] oldMouseListeners=c.getMouseListeners();
        MouseMotionListener[] oldMouseMotionListeners=c.getMouseMotionListeners();
        super.install(c);
        //the following code removes link handler added by original
        //HTMLEditorKit
        /* remove all listeners */
        for (MouseListener l: c.getMouseListeners()) {
            c.removeMouseListener(l);
        }
        /* add back the ones we wanted to preserve */
        for (MouseListener l: oldMouseListeners) {
            c.addMouseListener(l);
        }

        for (MouseMotionListener l: c.getMouseMotionListeners()) {
            c.removeMouseMotionListener(l);
        }
        for (MouseMotionListener l: oldMouseMotionListeners) {
            c.addMouseMotionListener(l);
        }

        //add our link handler instead of removed one
        c.addMouseListener(handler);
        c.addMouseMotionListener(handler);
    }


    public class MyLinkController extends LinkController {
        public void mouseClicked(MouseEvent e) {
            JEditorPane editor = (JEditorPane) e.getSource();
            //Original checks for !isEditable, isEnabled, and isLeftMouse
            if (editor.isEditable() && SwingUtilities.isLeftMouseButton(e)) {
                if (e.getClickCount()==2) {
                    editor.setEditable(false); //quick swap lol
                    super.mouseClicked(e);
                    editor.setEditable(true);
                }
            }
        }
//          some sort of checking
//        public void mouseMoved(MouseEvent e) {
//            JEditorPane editor = (JEditorPane) e.getSource();
//
//            if (editor.isEditable()) {
//                isNeedCursorChange=false;
//                editor.setEditable(false);
//                isNeedCursorChange=true;
//                super.mouseMoved(e);
//                isNeedCursorChange=false;
//                editor.setEditable(true);
//                isNeedCursorChange=true;
//            }
//        }

    }
}

