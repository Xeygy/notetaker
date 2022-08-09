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
import java.util.Set;

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

    public void addNote(NoteModel newNote) {
        listModel.add(newNote);
        storedList.add(newNote);
        addToNoteWindow(newNote);
    }

    public void removeSelectedNote() {
        int selectedIndex = listView.getSelectedIndex();
        if (selectedIndex > -1) {
            listModel.remove(selectedIndex);
            removeFromNoteWindow(storedList.get(selectedIndex));
            storedList.remove(selectedIndex);
        }
    }

    /** methods for updating the NoteWindow ToolWindow */
    public void addToNoteWindow(NoteModel newNote) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Notetaker");
        if (toolWindow != null) {
            ContentManager cm = toolWindow.getContentManager();
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            NoteWindow noteWindow = new NoteWindow(toolWindow, project, newNote);
            Content noteTab = contentFactory.createContent(noteWindow.getContent(), newNote.getName(), false);
            cm.addContent(noteTab);
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
