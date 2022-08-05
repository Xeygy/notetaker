package org.intellij.sdk.notetaker.storage;

import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

public class NoteStorageManager {
    private final NoteStorageState storageState;

    public NoteStorageManager(Project project) {
        storageState = project.getService(NoteStorageState.class);
    }

    public List<NoteModel> getNoteList() {
        if (storageState.getNoteModelStorage() == null) {
            return null;
        }
        return storageState.getNoteModelStorage().getNoteList();
    }

    public List<NoteModel> setNoteList(List<NoteModel> noteList) {
        if (storageState.getNoteModelStorage() == null) {
            storageState.setNoteModelStorage(new NoteModelStorage(noteList));
        } else {
            storageState.getNoteModelStorage().setNoteList(noteList);
        }
        storageState.loadState(storageState);
        return getNoteList();
    }

}
