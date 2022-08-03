package org.intellij.sdk.notetaker;

import com.intellij.openapi.project.Project;

import java.util.HashMap;

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

    public HashMap<String, MethodWrapper> getLinks() {
        return this.storageState.getLinks();
    }
    public HashMap<String, MethodWrapper> setLinks(HashMap hm) {
        this.storageState.setLinks(hm);
        this.storageState.loadState(storageState);
        return this.storageState.getLinks();
    }
}
