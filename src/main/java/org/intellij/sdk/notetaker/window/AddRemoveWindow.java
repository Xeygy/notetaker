package org.intellij.sdk.notetaker.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.intellij.sdk.notetaker.storage.NoteModel;
import org.intellij.sdk.notetaker.storage.NoteStorageManager;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Random;

public class AddRemoveWindow {
    private ViewManager viewManager;

    NoteStorageManager storageManager;
    private Project project;

    public AddRemoveWindow(Project project) {
        this.project = project;
        storageManager = new NoteStorageManager(project);

        //null check if first time using app
        if (storageManager.getNoteList() == null) {
            ArrayList<NoteModel> newList = new ArrayList<>();
            storageManager.setNoteList(newList);
        }
        viewManager = new ViewManager(storageManager.getNoteList(), project);
    }

    public JComponent getComponent() {
        viewManager.getListView().addMouseListener(clickListener());

        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(viewManager.getListView());
        decorator.setAddAction(button -> {addNoteAction();});
        decorator.setRemoveAction(button -> {removeNoteAction();});
        decorator.disableDownAction();
        decorator.disableUpAction();

        return decorator.createPanel();
    }

    public MouseListener clickListener() {
        MouseListener l = new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    // Double-click detected
                    int index = list.locationToIndex(evt.getPoint());
                    //String selectedFile = listForView.getElementAt(index).getName();
                }
            }
        };
        return l;
    }

    public void addNoteAction() {
        NoteModel newNote = new NoteModel(getRandomString(), "");
        viewManager.addNote(newNote);
    }
    public void removeNoteAction() {
        viewManager.removeSelectedNote();
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
