package org.intellij.sdk.notetaker.window;

import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

public class AddRemoveWindow {
    private CollectionListModel<String> noteNames;
    private JBList list;

    public AddRemoveWindow() {
        noteNames = new CollectionListModel<>();
        list = new JBList(noteNames);
        noteNames.add("note1");
        noteNames.add("note2");
    }

    public JComponent getComponent() {
        list.addMouseListener(onClick());

        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(list);
        decorator.setAddAction(button -> {addNoteAction();});
        decorator.setRemoveAction(button -> {removeNoteAction();});
        decorator.disableDownAction();
        decorator.disableUpAction();

        return decorator.createPanel();
    }

    public MouseListener onClick() {
        MouseListener l = new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    // Double-click detected
                    int index = list.locationToIndex(evt.getPoint());
                    String selectedFile = noteNames.getElementAt(index);
                }
            }
        };
        return l;
    }

    public void addNoteAction() {
        noteNames.add(getRandomString());
    }
    public void removeNoteAction() {
        if (list.getSelectedIndex() != -1) {
            noteNames.remove(list.getSelectedIndex());
        }
    }

    protected String getRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 6) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }
}
