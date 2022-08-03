package org.intellij.sdk.notetaker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.psi.PsiMethod;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;

public class LinksConverter extends Converter<HashMap> {

    private final Gson jsonParser;

    public LinksConverter() {
        this.jsonParser = new Gson();
    }

    @Nullable
    @Override
    public HashMap<String, MethodWrapper> fromString(@NotNull String value) {
        try {
            return (HashMap<String, MethodWrapper>) objectFromString(value);
        } catch (IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public @Nullable String toString(@NotNull HashMap value) {
        try {
            return serializableToString(value);
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private static String serializableToString( Serializable o ) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
    private static Object objectFromString(String s) throws IOException, ClassNotFoundException
    {
        byte [] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        Object o  = ois.readObject();
        ois.close();
        return o;
    }
}
