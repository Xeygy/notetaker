package org.intellij.sdk.notetaker.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;

import org.intellij.sdk.notetaker.storage.NoteModel;
import org.intellij.sdk.notetaker.storage.NoteStorageManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class NoteToolWindowFactory implements ToolWindowFactory {

    /**
     * Create the tool window content.
     *
     * @param project    current project
     * @param toolWindow current tool window
     */
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentManager cm = toolWindow.getContentManager();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        NoteStorageManager manager = new NoteStorageManager(project);
        List<NoteModel> noteList = manager.getNoteList();
        if (noteList != null) {
            //noteList.add(new NoteModel("test", ""));
            for (NoteModel note : noteList) {
                NoteWindow noteWindow = new NoteWindow(toolWindow, project, note);
                Content noteTab = contentFactory.createContent(noteWindow.getContent(), note.getName(), false);
                cm.addContent(noteTab);
            }
        } else {
            manager.setNoteList(new ArrayList<>());

        }
    }

}