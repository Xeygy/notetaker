package org.intellij.sdk.notetaker.storage;

import com.google.gson.Gson;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** converter for serializing NoteModelStorage, should eventually
 * be changed to NoteModelListConverter & remove NoteModelStorage, as GSON can convert Lists
 * just fine, we don't need a wrapper around the List.
 */

public class NoteModelStorageConverter extends Converter<NoteModelStorage> {
    private final Gson jsonParser;

    public NoteModelStorageConverter() {
        this.jsonParser = new Gson();
    }

    @Nullable
    @Override
    public NoteModelStorage fromString(@NotNull String value) {
        return jsonParser.fromJson(value, NoteModelStorage.class);
    }

    @Override
    public @Nullable String toString(@NotNull NoteModelStorage value) {
        return jsonParser.toJson(value);
    }
}