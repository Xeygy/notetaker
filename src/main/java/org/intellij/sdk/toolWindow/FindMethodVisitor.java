package org.intellij.sdk.toolWindow;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiMethod;

import java.util.HashSet;

public class FindMethodVisitor extends JavaRecursiveElementVisitor {
    private String methodName;
    private HashSet foundMethods;

    public FindMethodVisitor(String methodName, HashSet<PsiMethod> foundMethods) {
        this.methodName = methodName;
        this.foundMethods = foundMethods;
    }

    @Override
    public void visitMethod(PsiMethod method) {
        super.visitMethod(method);
        if (method.getName().equals(methodName)) {
            foundMethods.add(method);
        }
    }
}
