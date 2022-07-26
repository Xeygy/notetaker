package org.intellij.sdk.toolWindow;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;

import javax.swing.text.Document;


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
