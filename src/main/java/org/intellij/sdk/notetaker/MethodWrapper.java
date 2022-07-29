package org.intellij.sdk.notetaker;

import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.ArrayList;

public class MethodWrapper {
    private final PsiMethod method;

    public MethodWrapper(PsiMethod method) {
        this.method = method;
    }

    public String getLocId() {
        ArrayList params = new ArrayList<String>();
        for (PsiParameter param : method.getParameterList().getParameters()) {
            params.add(param.getType().getCanonicalText());
        }
        return method.getContainingClass().getQualifiedName() + "." + method.getName() + "#" + String.join(",", params);
    }

    public boolean equalsLocId(String locId) {
        return this.getLocId().equals(locId);
    }

    public void goToMethod() {
        PsiElement navigationElement = method.getNavigationElement();
        if (navigationElement instanceof Navigatable && ((Navigatable) navigationElement).canNavigate())
        {
            ((Navigatable) navigationElement).navigate(true);
        }
    }
}
