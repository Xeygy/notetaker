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
        processor.runProcessor(project);
        processor.printFoundMethods();
        processor.goToFoundMethods();
    }




    public JPanel getContent() {
        return goToContent;
    }
}
