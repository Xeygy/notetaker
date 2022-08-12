package org.intellij.sdk.notetaker.storage;

import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * hello
 */
public class NoteStorageManager {
    private final NoteStorageState storageState;

    public NoteStorageManager(Project project) {
        storageState = project.getService(NoteStorageState.class);
    }

    /**
     *
     * @return g
     */
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

    //assumes unique note names
    public List<NoteModel> getOpenTabs() {
        if (storageState.getOpenTabNames() == null) {
            storageState.setOpenTabNames(new ArrayList<>());
        }
        List<String> openTabNames = storageState.getOpenTabNames();
        List<NoteModel> allNotes = getNoteList();
        List<NoteModel> openTabs = new ArrayList<>();
        for (String tab : openTabNames) {
            for (NoteModel note : allNotes) {
                if (note.getName().equals(tab)) {
                   openTabs.add(note);
                }
            }
        }
        return openTabs;
    }

    public void removeFromOpenTabs(String tabName) {
        storageState.getOpenTabNames().remove(tabName);
    }
    public void addToOpenTabs(String tabName) {
        storageState.getOpenTabNames().add(tabName);
    }

}
