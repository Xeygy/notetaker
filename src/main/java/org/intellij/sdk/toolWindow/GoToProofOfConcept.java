package org.intellij.sdk.toolWindow;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.pom.Navigatable;
import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class GoToProofOfConcept {
    private JTextField textField1;
    private JButton goToDefinitionButton;
    private JPanel goToContent;
    private final Project project;

    public GoToProofOfConcept(ToolWindow toolWindow, @NotNull Project project) {
        goToDefinitionButton.addActionListener(e -> goToClicked());
        this.project = project;
    }

    public void goToClicked() {
        String def = textField1.getText(); //the function definition we're looking for

        FindMethodProcessor processor = new FindMethodProcessor(def);
        PsiSearchHelper searchHelper = PsiSearchHelper.getInstance(project);
        Module @NotNull [] modules = ModuleManager.getInstance(project).getModules();
        GlobalSearchScope scope = null;
        for (Module m : modules) {
            /* moduleScope is necessary (as opposed to projectScope or allScope)
                because it excludes libraries and dependencies from the search */
             GlobalSearchScope tempScope = GlobalSearchScope.moduleScope(m);
             scope = (scope == null) ? tempScope : tempScope.uniteWith(scope);
        }
        if (scope != null) {
            searchHelper.processAllFilesWithWord(def, scope, processor, true);
        }
        processor.printFoundMethods();
    }

    /** sets the UI of the user to the location of the
     * PsiElement
     * @param element element to navigate to
     */
    public void navigateToElement(PsiElement element) {
        // code sourced from: https://intellij-support.jetbrains.com/hc/en-us/community/posts/206137479-Navigating-to-a-PsiElement
        PsiElement navigationElement = element.getNavigationElement();
        if (navigationElement instanceof Navigatable && ((Navigatable) navigationElement).canNavigate())
        {
            ((Navigatable) navigationElement).navigate(true);
        }
    }

    public JPanel getContent() {
        return goToContent;
    }
}
