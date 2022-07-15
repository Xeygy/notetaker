package org.intellij.sdk.toolWindow;

import com.intellij.lang.jvm.JvmParameter;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.util.Processor;

import java.util.HashSet;

public class FindMethodProcessor implements Processor {

    private String methodName;
    private HashSet<PsiMethod> foundMethods;

    /** a processor for PsiFiles that finds all instances
     * of a method with a given method name
     * @param methodName the method name to look for
     */
    public FindMethodProcessor(String methodName) {
        this.methodName = methodName;
        this.foundMethods = new HashSet<>();
    }

    @Override
    public boolean process(Object o) {
        FindMethodVisitor visitor = new FindMethodVisitor(methodName, foundMethods);
        if (o instanceof PsiFile) {
            ((PsiFile)o).accept(visitor);
        }
        return true;
    }

    public void printFoundMethods() {
        for (PsiMethod method : foundMethods) {
            System.out.println(method.getContainingClass());
            JvmParameter[] params = method.getParameters();
            for (JvmParameter p : params) {
                System.out.println(p.getType());
            }
        }
    }
}
