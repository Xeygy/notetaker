package org.intellij.sdk.toolWindow;

import com.intellij.openapi.components.ServiceManager;

import javax.swing.text.Document;

public class NoteStorageManager {
    private final NoteStorageState storageState;

    public NoteStorageManager() {
        storageState = ServiceManager.getService(NoteStorageState.class);
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
