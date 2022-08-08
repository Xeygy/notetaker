package org.intellij.sdk.notetaker.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
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
import java.util.List;
import java.util.Random;

public class AddRemoveWindow {
    private CollectionListModel<String> noteNames;
    private JBList<String> list;

    private List<NoteModel> noteList;
    NoteStorageManager storageManager;
    private Project project;

    public AddRemoveWindow(Project project) {
        this.project = project;
        noteNames = new CollectionListModel<>();
        storageManager = new NoteStorageManager(project);
        noteList = storageManager.getNoteList();


        if (noteList != null) {
            noteList.stream().forEach(model -> {noteNames.add(model.getName());});
        }
        list = new JBList<>(noteNames);
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
        NoteModel newNote = new NoteModel(getRandomString(), "");
        if (noteList == null) {
            ArrayList<NoteModel> newList = new ArrayList<>();
            storageManager.setNoteList(newList);
        }
        noteList.add(newNote);
        noteNames.add(newNote.getName());
        updateNotesWindow(newNote);
    }
    public void removeNoteAction() {
        if (list.getSelectedIndex() != -1) {
            noteNames.remove(list.getSelectedIndex());
        }
    }

    public void updateNotesWindow(NoteModel note) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Notetaker");
        if (toolWindow != null) {
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            ContentManager cm = toolWindow.getContentManager();
            NoteWindow noteWindow = new NoteWindow(toolWindow, project, note);
            Content noteTab = contentFactory.createContent(noteWindow.getContent(), note.getName(), false);
            cm.addContent(noteTab);
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
