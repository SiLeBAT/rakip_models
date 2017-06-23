package de.bund.bfr.rakip.generic

import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JTextArea
import javax.swing.JTextField

class EditAssayPanel(assay: Assay? = null, isAdvanced: Boolean) : ValidatablePanel() {

    companion object {
        val assayName = "Name *"
        val assayNameTooltip = "A name given to the assay"

        val assayDescription = "Description"
        val assayDescriptionTooltip = "General description of the assay. Corresponds to the Protocol REF in ISA"
    }

    val nameTextField = JTextField(30)
    val descriptionTextArea = if (isAdvanced) JTextArea(5, 30) else null

    init {

        // Populate UI with passed `assay`
        assay?.let {
            nameTextField.text = it.name
            descriptionTextArea?.text = it.description
        }

        initUI()
    }

    private fun initUI() {
        val nameLabel = createLabel(text = assayName, tooltip = assayNameTooltip)
        val descriptionLabel = createLabel(text = assayDescription, tooltip = assayDescriptionTooltip)

        val pairList = mutableListOf<Pair<JLabel, JComponent>>()
        pairList.add(Pair(first = nameLabel, second = nameTextField))
        descriptionTextArea?.let { Pair(first = descriptionLabel, second = it) }

        addGridComponents(pairs = pairList)
    }

    fun toAssay() = Assay(name = nameTextField.text, description = descriptionTextArea?.text)

    override fun validatePanel(): List<String> {
        val errors = mutableListOf<String>()
        if (!nameTextField.hasValidValue()) errors.add("Missing ${assayName}")
        return errors
    }
}
