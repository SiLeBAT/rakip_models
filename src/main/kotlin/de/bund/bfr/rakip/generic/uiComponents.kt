package de.bund.bfr.rakip.generic

import de.bund.bfr.knime.ui.AutoSuggestField
import javax.swing.JTextArea
import javax.swing.JTextField

/**
 * Created by miguelalba on 23.06.17.
 */

fun JTextField.hasValidValue() = text.isNotBlank()

fun JTextArea.hasValidValue() = text.isNotBlank()

fun AutoSuggestField.hasValidValue() : Boolean {
    val field = editor.editorComponent as JTextField
    return field.text.isNotBlank()
}