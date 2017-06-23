package de.bund.bfr.rakip.generic

import java.awt.Color
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

internal fun createLabel(text: String, tooltip: String): JLabel {
    val label = JLabel(text)
    label.toolTipText = tooltip

    return label
}

internal fun createAdvancedPanel(checkbox: JCheckBox): JPanel {
    val panel = JPanel()
    panel.background = Color.lightGray
    panel.add(checkbox)

    return panel
}

/** Creates a JSpinner with 5 columns. */
internal fun createSpinner(spinnerModel: AbstractSpinnerModel): JSpinner {
    val spinner = JSpinner(spinnerModel)
    (spinner.editor as JSpinner.DefaultEditor).textField.columns = 5

    return spinner
}

/** Creates a SpinnerNumberModel for integers with no limits and initial value 0. */
internal fun createSpinnerIntegerModel() = SpinnerNumberModel(0, null, null, 1)

/** Creates a SpinnerNumberModel for real numbers with no limits and initial value 0.0. */
internal fun createSpinnerDoubleModel() = SpinnerNumberModel(0.0, null, null, .01)

/**
 * Creates a SpinnerNumberModel for percentages (doubles) and initial value 0.0.
 *
 * Has limits 0.0 and 1.0.
 * */
internal fun createSpinnerPercentageModel() = SpinnerNumberModel(0.0, 0.0, 1.0, .01)

internal class NonEditableTableModel : DefaultTableModel(arrayOf(), arrayOf("header")) {
    override fun isCellEditable(row: Int, column: Int) = false
}

internal class HeadlessTable(model: NonEditableTableModel, val renderer: DefaultTableCellRenderer) : JTable(model) {

    init {
        tableHeader = null  // Hide header
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    }

    override fun getCellRenderer(row: Int, column: Int) = renderer
}

internal class ButtonsPanel : JPanel() {

    val addButton = JButton("Add")
    val modifyButton = JButton("Modify")
    val removeButton = JButton("Remove")

    init {
        add(addButton)
        add(modifyButton)
        add(removeButton)
    }
}

/**
 * Shows Swing ok/cancel dialog.
 *
 * @return the selected option: JOptionPane.OK_OPTION or JOptionPane.CANCEL_OPTION
 */
internal fun showConfirmDialog(panel: JPanel, title: String): Int {
    return JOptionPane.showConfirmDialog(null, panel, title, JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE)
}
