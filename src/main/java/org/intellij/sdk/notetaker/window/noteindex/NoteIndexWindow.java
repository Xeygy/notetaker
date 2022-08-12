package org.intellij.sdk.notetaker.window.noteindex;

import com.intellij.openapi.project.Project;
import com.intellij.ui.ToolbarDecorator;
import org.intellij.sdk.notetaker.storage.NoteModel;
import org.intellij.sdk.notetaker.storage.NoteStorageManager;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class NoteIndexWindow {
    private ViewController viewController;

    NoteStorageManager storageManager;

    public NoteIndexWindow(Project project) {
        storageManager = new NoteStorageManager(project);

        //null check if first time using app
        if (storageManager.getNoteList() == null) {
            ArrayList<NoteModel> newList = new ArrayList<>();
            storageManager.setNoteList(newList);
        }
        viewController = new ViewController(storageManager.getNoteList(), project);
    }

    public JComponent getComponent() {
        viewController.getListView().addMouseListener(clickListener());

        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(viewController.getListView());
        decorator.setAddAction(button -> addNoteAction());
        decorator.setRemoveAction(button -> removeNoteAction());
        decorator.setEditAction(button -> renameNoteAction());
        decorator.disableDownAction();
        decorator.disableUpAction();

        return decorator.createPanel();
    }

    public MouseListener clickListener() {
        MouseListener l = new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    // Double-click detected
                    viewController.openSelectedNote();
                }
            }
        };
        return l;
    }

    public void addNoteAction() {
        viewController.addNote();
    }
    public void removeNoteAction() {
        viewController.removeSelectedNote();
    }
    public void renameNoteAction() {
        viewController.renameSelectedNote();
    }

}
