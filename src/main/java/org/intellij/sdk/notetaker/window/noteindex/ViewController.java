package org.intellij.sdk.notetaker.window.noteindex;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.components.JBList;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.intellij.sdk.notetaker.storage.NoteModel;
import org.intellij.sdk.notetaker.window.texteditor.NoteWindow;

import java.util.List;

/** Class for updating the window in NoteIndexWindow and NoteWindow */
public class ViewController {
    //Model & View for NoteIndexWindow
    private JBList<NoteModel> listView;
    private CollectionListModel<NoteModel> listModel;

    //Actual list that gets saved in storage
    private List<NoteModel> storedList;
    private Project project;

    /**
     * Class for updating the window in NoteIndexWindow and NoteWindow.
     * @param noteList all the notes in the project
     * @param project the project that this ViewController works on.
     */
    public ViewController(List<NoteModel> noteList, Project project) {
        this.storedList = noteList;
        this.project = project;
        listModel = new CollectionListModel<>(noteList);
        listView = new JBList<>(listModel);
    }

    public JBList<NoteModel> getListView() {
        return listView;
    }

    public void addNote() {
        SetNoteNameDialog dialog = new SetNoteNameDialog(storedList, "New Note Name");
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
            SetNoteNameDialog dialog = new SetNoteNameDialog(storedList, "Rename");
            if (dialog.showAndGet()) {
                String newName = dialog.getFieldValue();

                renameInNoteWindow(storedList.get(selectedIndex), newName);
                listModel.getElementAt(selectedIndex).setName(newName);
            }
        }
    }

    public void openSelectedNote() {
        NoteModel selected = listView.getSelectedValue();
        if (selected != null) {
            addToNoteWindow(selected);
        }
    }

    /* methods for updating the NoteWindow ToolWindow */

    /**
     * Adds given NoteModel to NoteWindow and changes focus to that NoteModel in
     * NoteWindow
     * @param newNote the note to add
     */
    public void addToNoteWindow(NoteModel newNote) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Notetaker");
        if (toolWindow != null) {
            ContentManager cm = toolWindow.getContentManager();
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

            Content existingNote = cm.findContent(newNote.getName());
            // note not already open in NoteWindow
            if (existingNote == null) {
                NoteWindow noteWindow = new NoteWindow(project, newNote);
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

    /**
     * changes focus only if a tab with the same name as the note is in the noteWindow
     *  @param note the note to change focus to
     *  */
    public void changeFocusToNoteWindow(NoteModel note) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Notetaker");
        if (toolWindow != null) {
            ContentManager cm = toolWindow.getContentManager();
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

            Content selectedNote = cm.findContent(note.getName());
            if (selectedNote != null) {
                cm.setSelectedContent(selectedNote);
                toolWindow.show();
            }
        }

        }

    /**
     * assumes display names are unique, returns first matching content it finds,
     * null otherwise
     * @param cm ContentManager for NoteWIndow
     * @param name Name of the tab you want to find
     * @return first matching content it finds,
     * null otherwise
     */
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
