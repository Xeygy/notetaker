package org.intellij.sdk.notetaker.visitors;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.lang.jvm.JvmParameter;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

/**
 * A processor for PsiFiles that finds all instances
 * of a method with a given method signature.
 * Call runProcessor() to search the given project.
 */
public class FindIndividualMethodProcessor implements Processor {

    private final String methodName;
    private final String params;
    private final String enclosingClass;
    private final HashSet<PsiMethod> foundMethods;
    private final Project project;

    /**
     * A processor for PsiFiles that finds all instances
     * of a method with a given method signature.
     * Call runProcessor() to search the given project.
     * @param enclosingClass the full path of the containing class (PsiMethod.getContainingClass().getQualifiedName())
     * @param methodName the name of the method
     * @param params the parameter types (param.getType().getCanonicalText()) separated by commas
     * @param project the project you want to search through
     */
    public FindIndividualMethodProcessor(String enclosingClass, String methodName, String params, Project project) {
        this.enclosingClass = enclosingClass;
        this.methodName = methodName;
        this.params = params;
        this.foundMethods = new HashSet<>();
        this.project = project;
    }

    /**
     * Must call runProcessor() first
     * @return the found methods
     */
    public HashSet<PsiMethod> getFoundMethods() {
        return foundMethods;
    }

    @Override
    public boolean process(Object o) {
        FindIndividualMethodVisitor visitor = new FindIndividualMethodVisitor(enclosingClass, methodName, params, foundMethods);
        if (o instanceof VirtualFile) {
            VirtualFile vf = ((VirtualFile)o);
            PsiFile pf = PsiManager.getInstance(project).findFile(vf);
            pf.accept(visitor);
        }
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

    /**
     * searches all modules in the project for the provided method signature.
     */
    public void runProcessor() {
        Module @NotNull [] modules = ModuleManager.getInstance(project).getModules();
        GlobalSearchScope scope = null;
        for (Module m : modules) {
            /* moduleScope is necessary (as opposed to projectScope or allScope)
                because it excludes libraries and dependencies from the search */
            GlobalSearchScope tempScope = GlobalSearchScope.moduleScope(m);
            scope = (scope == null) ? tempScope : tempScope.uniteWith(scope);
        }
        if (scope != null) {
            FileTypeIndex.processFiles(JavaFileType.INSTANCE, this, scope);
        }
    }
}
