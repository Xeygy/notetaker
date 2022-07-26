package org.intellij.sdk.toolWindow;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

import javax.swing.text.Document;

public class NoteStorageManager {
    private final NoteStorageState storageState;

    public NoteStorageManager(Project project) {
        storageState = project.getService(NoteStorageState.class);
    }

    public String getNoteText() {
        return this.storageState.getText();
    }

    public String setNoteText(String text) {
        this.storageState.setText(text);
        this.storageState.loadState(storageState);
        return this.storageState.getText();
    }
}
