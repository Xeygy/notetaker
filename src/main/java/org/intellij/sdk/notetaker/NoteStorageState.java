package org.intellij.sdk.notetaker;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;

import java.util.HashMap;


@State(name = "Notetaker")
class NoteStorageState implements PersistentStateComponent<NoteStorageState> {
    private String text;

    @OptionTag(converter = LinksConverter.class)
    private HashMap<String, MethodWrapper> links;

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

    public HashMap<String, MethodWrapper> getLinks() {
        return links;
    }
    public void setLinks(HashMap links) {
        this.links = links;
    }
}
