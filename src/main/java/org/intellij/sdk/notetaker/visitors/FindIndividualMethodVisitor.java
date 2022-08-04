package org.intellij.sdk.notetaker.visitors;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiMethod;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class FindIndividualMethodVisitor extends JavaRecursiveElementVisitor {
    private final String methodName;
    private final String params;
    private final String enclosingClass;
    private HashSet<PsiMethod> foundMethods;

    public FindIndividualMethodVisitor(String enclosingClass, String methodName, String params, HashSet<PsiMethod> foundMethods) {
        this.enclosingClass = enclosingClass;
        this.methodName = methodName;
        this.params = params;
        this.foundMethods = foundMethods;
    }

    @Override
    public void visitMethod(PsiMethod method) {
        super.visitMethod(method);
        Set<String> currParamSet = Arrays.stream(method.getParameterList().getParameters())
                .map(param -> param.getType().getCanonicalText())
                .collect(Collectors.toSet());
        // must check that params isn't empty, otherwise set will contain an empty string
        String[] targetParamArr = params.equals("") ? new String[0] : params.split(",");
        Set<String> targetParamSet = Arrays.stream(targetParamArr).collect(Collectors.toSet());

        boolean targetFound = method.getContainingClass().getQualifiedName().equals(enclosingClass) &&
                method.getName().equals(methodName) &&
                currParamSet.equals(targetParamSet);
        if (targetFound) {
            foundMethods.add(method);
        }
    }
}
