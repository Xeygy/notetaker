package org.intellij.sdk.notetaker.storage;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import com.thoughtworks.qdox.model.expression.Not;

import java.util.List;


@State(name = "Notetaker")
class NoteStorageState implements PersistentStateComponent<NoteStorageState> {

    @OptionTag(converter = NoteModelStorageConverter.class)
    private NoteModelStorage noteModelStorage;

    public NoteStorageState getState() {
        return this;
    }

    public void loadState(NoteStorageState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public NoteModelStorage getNoteModelStorage() {
        return noteModelStorage;
    }

    public void setNoteModelStorage(NoteModelStorage storage) {
        noteModelStorage = storage;
    }

}
