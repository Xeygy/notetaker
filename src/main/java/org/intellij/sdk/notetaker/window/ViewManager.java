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

import java.util.ArrayList;
import java.util.List;

/** Class for updating the window in AddRemoveToolWindow */
public class ViewManager {
    //Model & View for the window
    private JBList<NoteModel> listView;
    private CollectionListModel<NoteModel> listModel;

    //Actual list that gets saved in the background
    private List<NoteModel> storedList;

    private Project project;

    // noteList is not null
    public ViewManager(List<NoteModel> noteList, Project project) {
        this.storedList = noteList;
        this.project = project;
        listModel = new CollectionListModel<>(noteList);
        listView = new JBList<>(listModel);
    }

    public JBList<NoteModel> getListView() {
        return listView;
    }

    public void addNote(NoteModel newNote) {
        listModel.add(newNote);
        storedList.add(newNote);
        updateNoteWindow();
    }

    public void removeSelectedNote() {
        int selectedIndex = listView.getSelectedIndex();
        if (selectedIndex > -1) {
            listModel.remove(selectedIndex);
            storedList.remove(selectedIndex);
            updateNoteWindow();
        }
    }

    /** Updates NoteWindow to match AddRemoveToolWindow */
    public void updateNoteWindow() {
        //Duplicate code from MyToolWindowFactory
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Notetaker");
        ContentManager cm = toolWindow.getContentManager();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        cm.removeAllContents(true);
        if (toolWindow != null) {
            for (NoteModel note : storedList) {
                NoteWindow noteWindow = new NoteWindow(toolWindow, project, note);
                Content noteTab = contentFactory.createContent(noteWindow.getContent(), note.getName(), false);
                cm.addContent(noteTab);
            }
        }
    }
}