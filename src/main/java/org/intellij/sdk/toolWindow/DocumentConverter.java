package org.intellij.sdk.toolWindow;

import com.google.gson.Gson;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.Document;

public class DocumentConverter extends Converter<Document> {
    private final Gson jsonParser;

    public DocumentConverter() {
        this.jsonParser = new Gson();
    }

    @Nullable
    @Override
    public Document fromString(@NotNull String value) {
        return jsonParser.fromJson(value, Document.class);
    }

    @Override
    public @Nullable String toString(@NotNull Document value) {
        return jsonParser.toJson(value);
    }
}
