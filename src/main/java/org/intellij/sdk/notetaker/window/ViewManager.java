package org.intellij.sdk.notetaker.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.intellij.sdk.notetaker.storage.NoteModel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/** Class for updating the window in AddRemoveToolWindow and NoteWindow */
public class ViewManager {
    //Model & View for AddRemoveToolWindow
    private JBList<NoteModel> listView;
    private CollectionListModel<NoteModel> listModel;

    //Actual list that gets saved in storage
    private List<NoteModel> storedList;

    private Project project;

    // noteList is not null
    public ViewManager(List<NoteModel> noteList, Project project) {
        this.storedList = noteList;
        this.project = project;
        listModel = new CollectionListModel<>(noteList);
        listView = new JBList<>(listModel);
        listView.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    SetNameNoteDialog dialog = new SetNameNoteDialog(storedList);
                    if (dialog.showAndGet()) {
                        // user pressed OK
                        String newNoteName = dialog.getFieldValue();
                    }
                }
            }
        });
    }

    public JBList<NoteModel> getListView() {
        return listView;
    }

    public void addNote() {
        SetNameNoteDialog dialog = new SetNameNoteDialog(storedList, "New Note Name");
        if (dialog.showAndGet()) {
            String newNoteName = dialog.getFieldValue();
            NoteModel newNote = new NoteModel(newNoteName, "");
            listModel.add(newNote);
            storedList.add(newNote);
            addToNoteWindow(newNote);
        }
    }

    public void removeSelectedNote() {
        int selectedIndex = listView.getSelectedIndex();
        if (selectedIndex > -1) {
            listModel.remove(selectedIndex);
            removeFromNoteWindow(storedList.get(selectedIndex));
            storedList.remove(selectedIndex);
        }
    }

    public void renameSelectedNote() {
        int selectedIndex = listView.getSelectedIndex();
        if (selectedIndex > -1) {
            SetNameNoteDialog dialog = new SetNameNoteDialog(storedList, "Rename");
            if (dialog.showAndGet()) {
                String newName = dialog.getFieldValue();
                renameInNoteWindow(storedList.get(selectedIndex), newName);
                listModel.getElementAt(selectedIndex).setName(newName);
                //storedList.remove(selectedIndex); not needed, noteModel is same instance
            }
        }
    }

    public void openSelectedNote() {
        NoteModel selected = listView.getSelectedValue();
        if (selected != null) {
            addToNoteWindow(selected);
        }
    }

    /** methods for updating the NoteWindow ToolWindow */
    public void addToNoteWindow(NoteModel newNote) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Notetaker");
        if (toolWindow != null) {
            ContentManager cm = toolWindow.getContentManager();
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

            Content existingNote = cm.findContent(newNote.getName());
            // note not already open in NoteWindow
            if (existingNote == null) {
                NoteWindow noteWindow = new NoteWindow(toolWindow, project, newNote);
                Content noteTab = contentFactory.createContent(noteWindow.getContent(), newNote.getName(), false);
                cm.addContent(noteTab);
            }
            changeFocusToNoteWindow(newNote);
        }
    }

    public void removeFromNoteWindow(NoteModel note) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Notetaker");
        if (toolWindow != null) {
            ContentManager cm = toolWindow.getContentManager();
            Content noteWindow = findContentWithDisplayName(cm, note.getName());
            if (noteWindow != null) {
                cm.removeContent(noteWindow, true);
            }
        }
    }

    public void renameInNoteWindow(NoteModel note, String newName) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Notetaker");
        if (toolWindow != null) {
            ContentManager cm = toolWindow.getContentManager();
            Content noteWindow = findContentWithDisplayName(cm, note.getName());
            if (noteWindow != null) {
                noteWindow.setDisplayName(newName);
            }
        }
    }

    /** TODO: RequestFocus DOES NOT WORK
     * changes focus only if a tab with the same name as the note is in the noteWindow */
    public void changeFocusToNoteWindow(NoteModel note) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Notetaker");
        if (toolWindow != null) {
            ContentManager cm = toolWindow.getContentManager();
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

            Content selectedNote = cm.findContent(note.getName());
            if (selectedNote != null) {
                cm.requestFocus(selectedNote, true);
            }
        }

        }
    /** assumes display names are unique, returns first matching content it finds,
     * null otherwise */
    public Content findContentWithDisplayName(ContentManager cm, String name) {
        Content[] contents = cm.getContents();
        for (Content content : contents) {
            if (name.equals(content.getDisplayName())) {
                return content;
            }
        }
        return null;
    }
}
