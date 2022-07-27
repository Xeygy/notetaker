package org.intellij.sdk.notetaker.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;

import org.jetbrains.annotations.NotNull;


public class MyToolWindowFactory implements ToolWindowFactory {

    /**
     * Create the tool window content.
     *
     * @param project    current project
     * @param toolWindow current tool window
     */
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentManager cm = toolWindow.getContentManager();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        for (int i = 1; i<= 2; i++) {
            NoteToolWindow noteToolWindow = new NoteToolWindow(toolWindow, project);
            Content noteTab = contentFactory.createContent(noteToolWindow.getContent(), "Notes"+i, false);
            cm.addContent(noteTab);
        }
    }

}