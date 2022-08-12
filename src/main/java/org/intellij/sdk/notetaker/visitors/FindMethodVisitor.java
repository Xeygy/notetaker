package org.intellij.sdk.notetaker.visitors;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiMethod;

import java.util.HashSet;

/**
 * visitor that finds methods starting with
 * the given prefix and puts them in to a
 * given HashSet foundMethods.
 */
public class FindMethodVisitor extends JavaRecursiveElementVisitor {
    private String methodName;
    private HashSet<PsiMethod> foundMethods;

    /**
     * visitor that finds methods starting with
     * the given prefix and puts them in to a
     * given HashSet foundMethods.
     * @param prefix the prefix that methods should start with
     * @param foundMethods the HashSet you want updated with the found methods
     */
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
