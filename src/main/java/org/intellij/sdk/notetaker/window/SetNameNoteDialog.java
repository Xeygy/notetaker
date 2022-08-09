package org.intellij.sdk.notetaker.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentValidator;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.DocumentAdapter;
import org.intellij.sdk.notetaker.storage.NoteModel;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/** Dialog popup for naming notes */
public class SetNameNoteDialog extends DialogWrapper {
    private List<NoteModel> existingNotes;

    private JTextField nameField;

    public SetNameNoteDialog(List<NoteModel> existingNotes) {
        this(existingNotes, "Set Name");
    }

    public SetNameNoteDialog(List<NoteModel> existingNotes, String dialogTitle) {
        super(true); // use current window as parent
        this.existingNotes = existingNotes;
        setTitle(dialogTitle);
        init();
    }

    /** for getting entered value */
    public String getFieldValue() {
        return nameField.getText();
    }

    @Override
    protected @org.jetbrains.annotations.Nullable ValidationInfo doValidate() {
        String name = nameField.getText();
        if (name.equals("")) {
            return new ValidationInfo("Note name must not be empty", nameField);
        }
        if (name.matches("^\\s+\\S*")) {
            return new ValidationInfo("Note name must not start with whitespace", nameField);
        }
        if (!existingNotes.stream()
                .map(NoteModel::getName)
                .filter(existingName->existingName.equals(name))
                .collect(Collectors.toSet())
                .isEmpty()) {
            return new ValidationInfo("Name already exists", nameField);
        }
        //if valid,
        return null;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        nameField = new JTextField();

        dialogPanel.add(nameField, BorderLayout.CENTER);
        return dialogPanel;
    }
}