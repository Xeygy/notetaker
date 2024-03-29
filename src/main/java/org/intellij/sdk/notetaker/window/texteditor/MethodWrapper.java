package org.intellij.sdk.notetaker.window.texteditor;

import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiParameter;

import java.util.ArrayList;

/**
 * Wrapper that provides utility methods for the provided PsiMethod
 */
public class MethodWrapper implements java.io.Serializable {
    private final PsiMethod method;

    public MethodWrapper(PsiMethod method) {
        this.method = method;
    }

    /**
     * @return the full method signature of the PsiMethod
     */
    public String getLocId() {
        ArrayList<String> params = new ArrayList<>();
        for (PsiParameter param : method.getParameterList().getParameters()) {
            params.add(param.getType().getCanonicalText());
        }
        return method.getContainingClass().getQualifiedName() + "." + method.getName() + "#" + String.join(",", params);
    }

    public boolean equalsLocId(String locId) {
        return this.getLocId().equals(locId);
    }

    /**
     * changes the user focus to highlight the method declaration.
     */
    public void goToMethod() {
        PsiElement navigationElement = method.getNavigationElement();
        if (navigationElement instanceof Navigatable && ((Navigatable) navigationElement).canNavigate())
        {
            ((Navigatable) navigationElement).navigate(true);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MethodWrapper that = (MethodWrapper) o;

        return method != null ? method.equals(that.method) : that.method == null;
    }

    @Override
    public int hashCode() {
        return method != null ? method.hashCode() : 0;
    }
}
