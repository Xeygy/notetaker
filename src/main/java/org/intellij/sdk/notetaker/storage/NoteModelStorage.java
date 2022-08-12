package org.intellij.sdk.notetaker.storage;

import java.util.List;

/** Wrapper class used for serializing a List of NoteModels.
 * Should be replaced as GSON can handle a list of
 * Objects just fine -- NoteModelStorageConverter
 * should be changed into NoteModelListConverter
 * @see NoteModelStorageConverter*/
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
