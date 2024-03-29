package org.intellij.sdk.notetaker.visitors;

import com.intellij.psi.JavaRecursiveElementVisitor;
import com.intellij.psi.PsiMethod;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * visitor that finds methods with the given method signature and puts them in to a
 * given HashSet foundMethods.
 */
public class FindIndividualMethodVisitor extends JavaRecursiveElementVisitor {
    private final String methodName;
    private final String params;
    private final String enclosingClass;
    private HashSet<PsiMethod> foundMethods;

    /**
     * Creates visitor that finds methods with the given method signature and puts them in to a
     * given HashSet foundMethods.
     * @param enclosingClass the full path of the containing class (PsiMethod.getContainingClass().getQualifiedName())
     * @param methodName the name of the method
     * @param params the parameter types (param.getType().getCanonicalText()) separated by commas
     * @param foundMethods the HashSet you want updated with the found methods
     */
    public FindIndividualMethodVisitor(String enclosingClass, String methodName, String params, HashSet<PsiMethod> foundMethods) {
        this.enclosingClass = enclosingClass;
        this.methodName = methodName;
        this.params = params;
        this.foundMethods = foundMethods;
    }

    @Override
    public void visitMethod(PsiMethod method) {
        super.visitMethod(method);
        List<String> currParamSet = Arrays.stream(method.getParameterList().getParameters())
                .map(param -> param.getType().getCanonicalText())
                .collect(Collectors.toList());
        // must check that params isn't empty, otherwise set will contain an empty string
        String[] targetParamArr = params.equals("") ? new String[0] : params.split(",");
        List<String> targetParamSet = Arrays.stream(targetParamArr).collect(Collectors.toList());

        boolean targetFound = method.getContainingClass().getQualifiedName().equals(enclosingClass) &&
                method.getName().equals(methodName) &&
                currParamSet.equals(targetParamSet);
        if (targetFound) {
            foundMethods.add(method);
        }
    }
}
