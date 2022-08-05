package org.intellij.sdk.notetaker.storage;

import com.google.gson.Gson;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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