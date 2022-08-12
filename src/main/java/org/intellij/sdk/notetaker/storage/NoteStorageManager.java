package org.intellij.sdk.notetaker.storage;

import com.intellij.openapi.project.Project;

import java.util.ArrayList;
import java.util.List;

/**
 * A class to get access to the stored state of notetaker
 * for a given project. Allows you to save and load stored data.
 */
public class NoteStorageManager {
    private final NoteStorageState storageState;

    /**
     * @param project the IntelliJ project you want the notes to come from
     */
    public NoteStorageManager(Project project) {
        storageState = project.getService(NoteStorageState.class);
    }

    /**
     * @return a list of all the NoteModels being stored
     */
    public List<NoteModel> getNoteList() {
        if (storageState.getNoteModelStorage() == null) {
            return null;
        }
        return storageState.getNoteModelStorage().getNoteList();
    }

    /**
     * @param noteList the new list of NoteModels to be saved
     * @return a list of all the NoteModels being stored
     */
    public List<NoteModel> setNoteList(List<NoteModel> noteList) {
        if (storageState.getNoteModelStorage() == null) {
            storageState.setNoteModelStorage(new NoteModelStorage(noteList));
        } else {
            storageState.getNoteModelStorage().setNoteList(noteList);
        }
        storageState.loadState(storageState);
        return getNoteList();
    }

    /**
     * Assumes unique note names.
     * @return the saved list of all open NoteModels in the text editor
     */
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

    /**
     * Assumes unique note names.
     * @param tabName the name of the NoteModel you want
     *                to remove from openTabNames
     */
    public void removeFromOpenTabs(String tabName) {
        storageState.getOpenTabNames().remove(tabName);
    }

    /**
     * Assumes unique note names.
     * @param tabName the name of the NoteModel you want
     *                to add to openTabNames
     */
    public void addToOpenTabs(String tabName) {
        storageState.getOpenTabNames().add(tabName);
    }

}
