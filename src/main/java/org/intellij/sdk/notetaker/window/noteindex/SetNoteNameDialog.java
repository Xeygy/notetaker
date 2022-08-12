package org.intellij.sdk.notetaker.window.noteindex;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.intellij.sdk.notetaker.storage.NoteModel;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/** Dialog popup for naming notes, prevents the user from entering
 * any names that already belong to an existing note. */
public class SetNoteNameDialog extends DialogWrapper {
    private List<NoteModel> existingNotes;

    private JTextField nameField;

    /**
     * default dialog constructor, popup title is "Set Name"
     * @param existingNotes existing notes that the dialog will
     *                      check to prevent duplicate names
     */
    public SetNoteNameDialog(List<NoteModel> existingNotes) {
        this(existingNotes, "Set Name");
    }

    public SetNoteNameDialog(List<NoteModel> existingNotes, String dialogTitle) {
        super(true); // use current window as parent
        this.existingNotes = existingNotes;
        setTitle(dialogTitle);
        init();
    }

    /** for getting value entered into dialog */
    public String getFieldValue() {
        return nameField.getText();
    }

    /** validation settings for the popup
     * @return null if valid, ValidationInfo with reason if invalid
     */
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

    /**
     * The UI component of the popup.
     */
    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JPanel dialogPanel = new JPanel(new BorderLayout());
        nameField = new JTextField();

        dialogPanel.add(nameField, BorderLayout.CENTER);
        return dialogPanel;
    }
}