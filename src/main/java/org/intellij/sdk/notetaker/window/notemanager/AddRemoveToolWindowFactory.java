package org.intellij.sdk.notetaker.window.notemanager;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;


public class AddRemoveToolWindowFactory implements ToolWindowFactory {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentManager cm = toolWindow.getContentManager();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        AddRemoveWindow window = new AddRemoveWindow(project);
        Content noteTab = contentFactory.createContent(window.getComponent(), "", false);
        cm.addContent(noteTab);
    }

}
