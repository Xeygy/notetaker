package org.intellij.sdk.notetaker.storage;

import java.util.List;

/** Class used for serialization */
public class NoteModelStorage {
    private List<NoteModel> noteList;

    public NoteModelStorage(List<NoteModel> noteList) {
        this.noteList = noteList;
    }

    public List<NoteModel> getNoteList() {
        return noteList;
    }

    public void setNoteList(List<NoteModel> noteList) {
        this.noteList = noteList;
    }
}
