package org.intellij.sdk.notetaker;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.util.xmlb.XmlSerializerUtil;


@State(name = "Notetaker")
class NoteStorageState implements PersistentStateComponent<NoteStorageState> {
    private String text;

    public NoteStorageState getState() {
        return this;
    }

    public void loadState(NoteStorageState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
