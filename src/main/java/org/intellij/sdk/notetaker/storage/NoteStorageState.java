package org.intellij.sdk.notetaker.storage;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import org.jetbrains.annotations.NotNull;

import java.util.List;


@State(name = "Notetaker")
class NoteStorageState implements PersistentStateComponent<NoteStorageState> {

    @OptionTag(converter = NoteModelStorageConverter.class)
    private NoteModelStorage noteModelStorage;
    private List<String> openTabNames;

    public NoteStorageState getState() {
        return this;
    }

    public void loadState(@NotNull NoteStorageState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public NoteModelStorage getNoteModelStorage() {
        return noteModelStorage;
    }

    public void setNoteModelStorage(NoteModelStorage storage) {
        noteModelStorage = storage;
    }

    public List<String> getOpenTabNames() {
        return openTabNames;
    }

    public void setOpenTabNames(List<String> openTabNames) {
        this.openTabNames = openTabNames;
    }
}
