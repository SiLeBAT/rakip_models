package de.bund.bfr.rakip.generic

import java.awt.Frame
import javax.swing.JDialog
import javax.swing.JOptionPane
import javax.swing.WindowConstants


class ValidatableDialog(panel: ValidatablePanel) : JDialog(null as Frame?, true) {

    private val optionPane = JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_OPTION)

    init {

        // Handle window closing correctly
        defaultCloseOperation = WindowConstants.DISPOSE_ON_CLOSE

        optionPane.addPropertyChangeListener { e ->

            if (isVisible && e.source == optionPane &&
                    e.propertyName == JOptionPane.VALUE_PROPERTY &&
                    optionPane.value != JOptionPane.UNINITIALIZED_VALUE) {

                val value = optionPane.value as? Int

                if (value == JOptionPane.YES_OPTION) {
                    val errors = panel.validatePanel()
                    if (errors.isEmpty()) {
                        dispose()
                    } else {
                        val msg = errors.joinToString(separator = "\n")
                        JOptionPane.showMessageDialog(this, msg, "Missing fields",
                                JOptionPane.ERROR_MESSAGE)

                        // Reset the JOptionPane's value. If you don't this, the if the user presses
                        // the same button next time, no property change will be fired.
                        optionPane.value = JOptionPane.UNINITIALIZED_VALUE  // Reset value
                    }
                } else if (value == JOptionPane.NO_OPTION) {
                    dispose()
                }
            }
        }

        contentPane = optionPane
        pack()
    }

    fun getValue(): Any = optionPane.value
}