package org.intellij.sdk.toolWindow;

import com.intellij.lang.Language;
import com.intellij.model.search.SearchContext;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.*;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.search.*;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

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