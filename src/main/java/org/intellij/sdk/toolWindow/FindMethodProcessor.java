package org.intellij.sdk.toolWindow;

import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class FindMethodProcessor implements Processor {

    private final String methodName;
    private String containingClass;
    private final HashSet<PsiMethod> foundMethods;

    /** a processor for PsiFiles that finds all instances
     * of a method with a given method name
     * @param methodName the method name to look for
     */
    public FindMethodProcessor(String methodName) {
        this.methodName = methodName;
        this.foundMethods = new HashSet<>();
    }

    public FindMethodProcessor(String containingClass, String methodName) {
        this(methodName);
        this.containingClass = containingClass;
    }

    @Override
    public boolean process(Object o) {
        FindMethodVisitor visitor = new FindMethodVisitor(containingClass, methodName, foundMethods);
        if (o instanceof PsiFile) {
            ((PsiFile)o).accept(visitor);
        }
        return true;
    }

    public void printFoundMethods() {
        //TODO: JvmParameters are marked as experimental API
        for (PsiMethod method : foundMethods) {
            System.out.println(method.getContainingClass());
            JvmParameter[] params = method.getParameters();
            for (JvmParameter p : params) {
                System.out.println(p.getType());
            }
        }
    }

    public void goToFoundMethods() {
        for(PsiElement e : foundMethods) {
            navigateToElement(e);
        }
    }

    /** sets the UI of the user to the location of the
     * PsiElement
     * @param element element to navigate to
     */
    public static void navigateToElement(PsiElement element) {
        // code sourced from: https://intellij-support.jetbrains.com/hc/en-us/community/posts/206137479-Navigating-to-a-PsiElement
        PsiElement navigationElement = element.getNavigationElement();
        if (navigationElement instanceof Navigatable && ((Navigatable) navigationElement).canNavigate())
        {
            ((Navigatable) navigationElement).navigate(true);
        }
    }

    public void runProcessor(Project project) {
        PsiSearchHelper searchHelper = PsiSearchHelper.getInstance(project);
        Module @NotNull [] modules = ModuleManager.getInstance(project).getModules();
        GlobalSearchScope scope = null;
        for (Module m : modules) {
            /* moduleScope is necessary (as opposed to projectScope or allScope)
                because it excludes libraries and dependencies from the search */
            GlobalSearchScope tempScope = GlobalSearchScope.moduleScope(m);
            scope = (scope == null) ? tempScope : tempScope.uniteWith(scope);
        }
        if (scope != null) {
            searchHelper.processAllFilesWithWord(methodName, scope, this, true);
        }
    }
}
