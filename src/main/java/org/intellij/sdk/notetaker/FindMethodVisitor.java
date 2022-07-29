package org.intellij.sdk.notetaker;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class FindMethodVisitor extends JavaRecursiveElementVisitor {
    private String methodName;
    private String enclosingClass;
    private HashSet<PsiMethod> foundMethods;

    public FindMethodVisitor(String methodName, HashSet<PsiMethod> foundMethods) {
        this.methodName = methodName;
        this.foundMethods = foundMethods;
    }
    public FindMethodVisitor(String containingClass, String methodName, HashSet<PsiMethod> foundMethods) {
        this(methodName, foundMethods);
        this.enclosingClass = containingClass;
    }

    @Override
    public void visitMethod(PsiMethod method) {
        super.visitMethod(method);
        if (method.getName().startsWith(methodName)) {
            if (enclosingClass != null) { //TODO: CLEAN
                if (enclosingClass.equals(method.getContainingClass().getName())) {
                    foundMethods.add(method);
                }
            } else {
                String s = method.getContainingClass().getQualifiedName();
                foundMethods.add(method);
            }
        }
    }
}
