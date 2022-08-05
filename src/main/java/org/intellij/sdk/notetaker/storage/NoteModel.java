package org.intellij.sdk.notetaker.storage;

/** Model of an individual note */
public class NoteModel {
    private String name;
    private String content;

    public NoteModel(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
