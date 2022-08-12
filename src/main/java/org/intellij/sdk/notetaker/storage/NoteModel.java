package org.intellij.sdk.notetaker.storage;

/** The NoteModel class represents a note instance.
 * Stores the note name and the contents of the notes.
 * */
public class NoteModel {
    private String name;
    private String content;

    /**
     * @param name the name/title of the note
     * @param content the contents of the note, represented in HTML
     */
    public NoteModel(String name, String content) {
        this.name = name;
        this.content = content;
    }

    /** Because NoteIndexWindow relies on NoteModel.toString to display the
     * user's notes, toString only returns the name of the note.
     * Note names are expected to be unique.
     * @see org.intellij.sdk.notetaker.window.noteindex.NoteIndexWindow
     * NoteIndexWindow
     */
    @Override
    public String toString() {
        return name;
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
