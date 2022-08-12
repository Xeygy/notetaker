package org.intellij.sdk.notetaker.visitors;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiMethod;

import java.util.HashSet;

public class FindMethodVisitor extends JavaRecursiveElementVisitor {
    private String methodName;
    private HashSet<PsiMethod> foundMethods;

    public FindMethodVisitor(String prefix, HashSet<PsiMethod> foundMethods) {
        this.methodName = prefix;
        this.foundMethods = foundMethods;
    }

    @Override
    public void visitMethod(PsiMethod method) {
        super.visitMethod(method);
        if (method.getName().startsWith(methodName)) {
            foundMethods.add(method);
        }
    }
}
