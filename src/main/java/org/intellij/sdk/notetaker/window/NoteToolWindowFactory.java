package org.intellij.sdk.notetaker.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.*;
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
        List<NoteModel> openTabs = manager.getOpenTabs();
        if (openTabs != null) {
            //openTabs.add(new NoteModel("test", ""));
            for (NoteModel note : openTabs) {
                NoteWindow noteWindow = new NoteWindow(toolWindow, project, note);
                Content noteTab = contentFactory.createContent(noteWindow.getContent(), note.getName(), false);
                cm.addContent(noteTab);
            }
        } else {
            manager.setNoteList(new ArrayList<>());
        }
        cm.addContentManagerListener(tabStorageManager(manager));
    }

    /** responsible for detecting when open tabs are changed in the content manager */
    public ContentManagerListener tabStorageManager(NoteStorageManager manager) {
        return new ContentManagerListener() {
            @Override
            public void contentRemoved(@NotNull ContentManagerEvent event) {
                ContentManagerListener.super.contentRemoved(event);
                String removedName = event.getContent().getDisplayName();
                manager.removeFromOpenTabs(removedName);
            }

            @Override
            public void contentAdded(@NotNull ContentManagerEvent event) {
                ContentManagerListener.super.contentAdded(event);
                String addedName = event.getContent().getDisplayName();
                manager.addToOpenTabs(addedName);
            }
        };
    }

}