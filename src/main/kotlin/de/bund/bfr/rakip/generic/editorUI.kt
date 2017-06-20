import com.gmail.gcolaianni5.jris.bean.Record
import com.toedter.calendar.JDateChooser
import de.bund.bfr.knime.ui.AutoSuggestField
import de.bund.bfr.rakip.generic.*
import ezvcard.VCard
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.awt.*
import java.awt.event.WindowEvent
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Logger
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel

val logger: Logger = Logger.getAnonymousLogger()

val workbook = XSSFWorkbook("resources/FSKLab_Config_Controlled Vocabularies.xlsx")

val vocabs = mapOf(
        // General information controlled vocabularies
        "Rights" to readVocabFromSheet(workbook.getSheet("Rights")),
        "Format" to readVocabFromSheet(workbook.getSheet("Format")),
        "Software" to readVocabFromSheet(workbook.getSheet("Software")),
        "Language written in" to readVocabFromSheet(workbook.getSheet("Language written in")),
        "Status" to readVocabFromSheet(workbook.getSheet("Status")), // TODO: Status sheet is empty -> Ask Carolina

        // Product controlled vocabularies
        "Environment name" to readVocabFromSheet(workbook.getSheet("Product-matrix name")),
        "Environment unit" to readVocabFromSheet(workbook.getSheet("Product-matrix unit")),
        "Method of production" to readVocabFromSheet(workbook.getSheet("Method of production")), // TODO: Empty sheet
        "Packaging" to readVocabFromSheet(workbook.getSheet("Packaging")), // TODO: Empty sheet
        "Product treatment" to readVocabFromSheet(workbook.getSheet("Product treatment")), // TODO: Empty sheet
        "Country of origin" to readVocabFromSheet(workbook.getSheet("Country of origin")), // TODO: Empty sheet
        "Area of origin" to readVocabFromSheet(workbook.getSheet("Area of origin")), // TODO: Empty sheet
        "Fisheries area" to readVocabFromSheet(workbook.getSheet("Fisheries area"))  // TODO: Empty sheet
)

fun readVocabFromSheet(sheet: XSSFSheet): Set<String> {
    val vocab = mutableSetOf<String>()
    for (row in sheet) {
        if (row.rowNum == 0) continue  // Skip header
        val cell = row.getCell(0) ?: continue  // Skip empty cells

        // Replace faulty cells with "" that are later discarded
        val cellValue = try {
            cell.stringCellValue
        } catch (e: Exception) {
            logger.warning("Controlled vocabulary ${sheet.sheetName}: wrong value ${cell}")
            ""
        }

        if (cellValue.isNotBlank()) vocab.add(cellValue)
    }

    return vocab
}

fun main(args: Array<String>) {

    fun createExampleGeneralInformation(): GeneralInformation {

        // Example data
        val gi = GeneralInformation(name = "name",
                identifier = "007",
                creationDate = Date(),
                rights = "to remain silent",
                isAvailable = true,
                url = URL("https://google.de"),
                format = "fskx",
                language = "spanish",
                software = "KNIME",
                languageWrittenIn = "Matlab",
                status = "super curated",
                objective = "world domination",
                description = "by taking over M&Ms"
        )

        // example references
        Record().let {
            it.addAuthor("Florent Baty")
            it.pubblicationYear = "2012"
            it.title = "Package `nlstools`"
            gi.reference.add(it)
        }

        Record().let {
            it.addAuthor("Jagannath")
            it.pubblicationYear = "2005"
            it.title = "Comparison of the thermal inactivation"
            gi.reference.add(it)
        }

        // Add example creator
        VCard().let {
            it.setNickname("Gump")
            it.setFormattedName("Forrest Gump")
            it.addEmail("forrestgump@example.com")
            gi.creators.add(it)
        }

        return gi
    }

    val gi = createExampleGeneralInformation()

    val frame = JFrame()
    val generalInformationPanel = GeneralInformationPanel(gi)
    generalInformationPanel.studyNameTextField.text = gi.name
    generalInformationPanel.identifierTextField.text = gi.identifier
    generalInformationPanel.creationDateChooser.date = gi.creationDate
    generalInformationPanel.rightsField.setPossibleValues(vocabs.get("Rights"))
    generalInformationPanel.rightsField.selectedItem = gi.rights
    generalInformationPanel.availabilityCheckBox.isSelected = gi.isAvailable
    generalInformationPanel.urlTextField.text = gi.url.toString()
    generalInformationPanel.formatField.setPossibleValues(vocabs.get("Format"))
    generalInformationPanel.formatField.selectedItem = gi.format
    generalInformationPanel.languageTextField.text = gi.language
    generalInformationPanel.softwareField.setPossibleValues(vocabs.get("Software"))
    generalInformationPanel.softwareField.selectedItem = gi.software
    generalInformationPanel.languageWrittenInField.setPossibleValues(vocabs.get("Language writte in"))
    generalInformationPanel.languageWrittenInField.selectedItem = gi.languageWrittenIn
    generalInformationPanel.statusField.setPossibleValues(vocabs.get("Status"))
    generalInformationPanel.statusField.selectedItem = gi.status
    generalInformationPanel.objectiveTextField.text = gi.objective
    generalInformationPanel.descriptionTextField.text = gi.description

    val scopePanel = ScopePanel(Scope())
    val dataBackgroundPanel = DataBackgroundPanel()
    val modelMathPanel = ModelMathPanel()

    // Tabbed pane
    val tabbedPane = JTabbedPane()
    tabbedPane.addTab("General information", JScrollPane(generalInformationPanel))
    tabbedPane.addTab("Scope", JScrollPane(scopePanel))
    tabbedPane.addTab("Data background", JScrollPane(dataBackgroundPanel))
    tabbedPane.addTab("Model math", JScrollPane(modelMathPanel))

    frame.add(tabbedPane)
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
    frame.title = "JTree Example"
    frame.setSize(500, 300)
    frame.minimumSize = Dimension(800, 500)
    frame.isVisible = true
    frame.addWindowListener(object : java.awt.event.WindowAdapter() {
        // Save changes on close
        override fun windowClosing(windowEvent: WindowEvent?) {
            gi.name = generalInformationPanel.studyNameTextField.text
            gi.identifier = generalInformationPanel.identifierTextField.text
            if (generalInformationPanel.creationDateChooser.date != null) {
                gi.creationDate = generalInformationPanel.creationDateChooser.date
            }
            gi.rights = generalInformationPanel.rightsField.selectedItem as String
            gi.isAvailable = generalInformationPanel.availabilityCheckBox.isSelected
            gi.url = URL(generalInformationPanel.urlTextField.text)
            gi.format = generalInformationPanel.formatField.selectedItem as String
            gi.software = generalInformationPanel.softwareField.selectedItem as String?
            gi.languageWrittenIn = generalInformationPanel.languageWrittenInField.selectedItem as String?
            gi.status = generalInformationPanel.statusField.selectedItem as String?
            gi.objective = generalInformationPanel.objectiveTextField.text
            gi.description = generalInformationPanel.descriptionTextField.text

            System.exit(0)
        }
    })
}

fun JPanel.add(comp: JComponent, gridx: Int, gridy: Int, gridwidth: Int = 1, gridheight: Int = 1): Unit {
    val constraints = GridBagConstraints()
    constraints.gridx = gridx
    constraints.gridy = gridy
    constraints.gridwidth = gridwidth
    constraints.gridheight = gridheight
    constraints.ipadx = 10
    constraints.ipady = 10
    constraints.anchor = GridBagConstraints.LINE_START

    add(comp, constraints)
}

fun JPanel.addGridComponents(pairs: List<Pair<JLabel, JComponent>>) {

    val labelConstraints = GridBagConstraints()
    labelConstraints.gridx = 0
    labelConstraints.ipadx = 10
    labelConstraints.ipady = 10
    labelConstraints.anchor = GridBagConstraints.LINE_START

    val fieldConstraints = GridBagConstraints()
    fieldConstraints.gridx = 1
    fieldConstraints.ipadx = 10
    fieldConstraints.ipady = 10
    fieldConstraints.anchor = GridBagConstraints.LINE_START

    for ((index, entry) in pairs.withIndex()) {
        val label = entry.first
        val field = entry.second
        label.labelFor = field

        labelConstraints.gridy = index
        add(label, labelConstraints)

        fieldConstraints.gridy = index
        add(field, fieldConstraints)
    }
}

class GeneralInformationPanel(generalInformation: GeneralInformation) : Box(BoxLayout.PAGE_AXIS) {

    companion object {
        val studyName = "Study name"
        val studyNameTooltip = "Name given to the model or data"

        val identifier = "Identifier"
        val identifierTooltip = "Unambiguous ID given to the model or data"

        val creationDate = "Creation date"
        val creationDateTooltip = "Model creation date"

        val rights = "Rights"
        val rightsTooltip = "Rights held in over the resource"

        val availability = "Is available"
        val availabilityTooltip = "Availability of data or model"

        val url = "URL"
        val urlTooltip = "Web address referencing the resource location"

        val format = "Format"
        val formatTooltip = "Form of data (file extension)"

        val language = "Language"
        val languageTooltip = "Language used to write the model"

        val software = "Software"
        val softwareTooltip = "Program in which the model has been implemented"

        val languageWrittenIn = "Language written in"
        val languageWrittenInTooltip = "Language used to write the model"

        val status = "Status"
        val statusTooltip = "The curation status of the model"

        val objective = "Objective"
        val objectiveTooltip = "Objective of model or data"

        val description = "Description"
        val descriptionTooltip = "General assayDescription of the study, data or model"
    }

    val studyNameTextField = JTextField(30)
    val identifierTextField = JTextField(30)
    val creationDateChooser = FixedJDateChooser()
    val rightsField = AutoSuggestField(10)
    val availabilityCheckBox = JCheckBox()
    val urlTextField = JTextField(30)
    val formatField = AutoSuggestField(10)
    val languageTextField = JTextField(30)
    val softwareField = AutoSuggestField(10)
    val languageWrittenInField = AutoSuggestField(10)
    val statusField = AutoSuggestField(10)
    val objectiveTextField = JTextField(30)
    val descriptionTextField = JTextField(30)

    init {
        initUI(generalInformation)
    }

    private fun initUI(gi: GeneralInformation) {

        val advancedCheckBox = JCheckBox("Advanced")

        val studyNameLabel = createLabel(text = studyName, tooltip = studyNameTooltip)
        val identifierLabel = createLabel(text = identifier, tooltip = identifierTooltip)
        val creationDateLabel = createLabel(text = creationDate, tooltip = creationDateTooltip)
        val rightsLabel = createLabel(text = rights, tooltip = rightsTooltip)
        val urlLabel = createLabel(text = url, tooltip = urlTooltip)
        val formatLabel = createLabel(text = format, tooltip = formatTooltip)
        val languageLabel = createLabel(text = language, tooltip = languageTooltip)
        val softwareLabel = createLabel(text = software, tooltip = softwareTooltip)
        val languageWrittenInLabel = createLabel(text = languageWrittenIn, tooltip = languageWrittenInTooltip)
        val statusLabel = createLabel(text = status, tooltip = statusTooltip)
        val objectiveLabel = createLabel(text = objective, tooltip = objectiveTooltip)
        val descriptionLabel = createLabel(text = description, tooltip = descriptionTooltip)

        // hide initially advanced comps
        urlLabel.isVisible = false
        urlTextField.isVisible = false
        formatLabel.isVisible = false
        formatField.isVisible = false
        languageLabel.isVisible = false
        languageTextField.isVisible = false
        softwareLabel.isVisible = false
        softwareField.isVisible = false
        languageWrittenInLabel.isVisible = false
        languageWrittenInField.isVisible = false
        statusLabel.isVisible = false
        statusField.isVisible = false
        objectiveLabel.isVisible = false
        objectiveTextField.isVisible = false
        descriptionLabel.isVisible = false
        descriptionTextField.isVisible = false

        val propertiesPanel = JPanel(GridBagLayout())

        propertiesPanel.add(comp = studyNameLabel, gridy = 1, gridx = 0)
        propertiesPanel.add(comp = studyNameTextField, gridy = 1, gridx = 1, gridwidth = 2)

        propertiesPanel.add(comp = identifierLabel, gridy = 2, gridx = 0)
        propertiesPanel.add(comp = identifierTextField, gridy = 2, gridx = 1, gridwidth = 2)

        val creatorPanel = CreatorPanel(gi.creators)
        propertiesPanel.add(comp = creatorPanel, gridy = 3, gridx = 0, gridwidth = 3)

        propertiesPanel.add(comp = creationDateLabel, gridy = 4, gridx = 0)
        propertiesPanel.add(comp = creationDateChooser, gridy = 4, gridx = 1)

        propertiesPanel.add(comp = rightsLabel, gridy = 5, gridx = 0)
        propertiesPanel.add(comp = rightsField, gridy = 5, gridx = 1, gridwidth = 2)

        availabilityCheckBox.text = availability
        availabilityCheckBox.toolTipText = availabilityTooltip
        propertiesPanel.add(comp = availabilityCheckBox, gridy = 6, gridx = 0)

        propertiesPanel.add(comp = urlLabel, gridy = 7, gridx = 0)
        propertiesPanel.add(comp = urlTextField, gridy = 7, gridx = 1, gridwidth = 2)

        propertiesPanel.add(comp = formatLabel, gridy = 8, gridx = 0)
        propertiesPanel.add(comp = formatField, gridy = 8, gridx = 1, gridwidth = 2)

        val referencePanel = ReferencePanel(refs = gi.reference, isAdvanced = advancedCheckBox.isSelected)
        propertiesPanel.add(comp = referencePanel, gridy = 9, gridx = 0, gridwidth = 3)

        propertiesPanel.add(comp = languageLabel, gridy = 10, gridx = 0)
        propertiesPanel.add(comp = languageTextField, gridy = 10, gridx = 1, gridwidth = 2)

        propertiesPanel.add(comp = softwareLabel, gridy = 11, gridx = 0)
        propertiesPanel.add(comp = softwareField, gridy = 11, gridx = 1)

        propertiesPanel.add(comp = languageWrittenInLabel, gridy = 12, gridx = 0)
        propertiesPanel.add(comp = languageWrittenInField, gridy = 12, gridx = 1)

        propertiesPanel.add(comp = statusLabel, gridy = 13, gridx = 0)
        propertiesPanel.add(comp = statusField, gridy = 13, gridx = 1, gridwidth = 2)

        propertiesPanel.add(comp = objectiveLabel, gridy = 14, gridx = 0)
        propertiesPanel.add(comp = objectiveTextField, gridy = 14, gridx = 1, gridwidth = 2)

        propertiesPanel.add(comp = descriptionLabel, gridy = 15, gridx = 0)
        propertiesPanel.add(comp = descriptionTextField, gridy = 15, gridx = 1, gridwidth = 2)

        advancedCheckBox.addItemListener { _ ->
            val showAdvanced = advancedCheckBox.isSelected

            urlLabel.isVisible = showAdvanced
            urlTextField.isVisible = showAdvanced

            formatLabel.isVisible = showAdvanced
            formatField.isVisible = showAdvanced

            languageLabel.isVisible = showAdvanced
            languageTextField.isVisible = showAdvanced

            softwareLabel.isVisible = showAdvanced
            softwareField.isVisible = showAdvanced

            languageWrittenInLabel.isVisible = showAdvanced
            languageWrittenInField.isVisible = showAdvanced

            statusLabel.isVisible = showAdvanced
            statusField.isVisible = showAdvanced

            objectiveLabel.isVisible = showAdvanced
            objectiveTextField.isVisible = showAdvanced

            descriptionLabel.isVisible = showAdvanced
            descriptionTextField.isVisible = showAdvanced

            referencePanel.isAdvanced = showAdvanced
        }

        add(createAdvancedPanel(checkbox = advancedCheckBox))
        add(Box.createGlue())
        add(propertiesPanel)
    }
}

class ReferencePanel(val refs: MutableList<Record>, var isAdvanced: Boolean) : JPanel(BorderLayout()) {

    init {
        border = BorderFactory.createTitledBorder("References")

        val dtm = NonEditableTableModel()
        refs.forEach { dtm.addRow(arrayOf(it)) }

        val renderer = object : DefaultTableCellRenderer() {
            override fun setValue(value: Any?) {
                if (value == null) text = ""
                else {
                    val record = value as Record

                    val firstAuthor = record.authors?.get(0) ?: ""
                    val publicationYear = record.pubblicationYear.orEmpty()
                    val title = record.title.orEmpty()
                    text = "${firstAuthor}_${publicationYear}_$title"
                }
            }
        }
        val myTable = HeadlessTable(model = dtm, renderer = renderer)

        // buttons
        val buttonsPanel = ButtonsPanel()
        buttonsPanel.addButton.addActionListener { _ ->
            val editPanel = EditReferencePanel(isAdvanced = isAdvanced)
            val result = showConfirmDialog(panel = editPanel, title = "Create reference")
            if (result == JOptionPane.OK_OPTION) {
                dtm.addRow(arrayOf(editPanel.toRecord()))
            }
        }

        buttonsPanel.modifyButton.addActionListener { _ ->
            val rowToEdit = myTable.selectedRow
            if (rowToEdit != -1) {
                val ref = dtm.getValueAt(rowToEdit, 0) as Record

                val editPanel = EditReferencePanel(ref, isAdvanced = isAdvanced)
                val result = showConfirmDialog(panel = editPanel, title = "Modify reference")
                if (result == JOptionPane.OK_OPTION) {
                    dtm.setValueAt(editPanel.toRecord(), rowToEdit, 0)
                }
            }
        }

        buttonsPanel.removeButton.addActionListener { _ ->
            val rowToDelete = myTable.selectedRow
            if (rowToDelete != -1) dtm.removeRow(rowToDelete)
        }

        add(myTable, BorderLayout.NORTH)
        add(buttonsPanel, BorderLayout.SOUTH)
    }
}

/** Fixes JDateChooser and disables the text field */
class FixedJDateChooser : JDateChooser() {

    init {
        // Fixes bug AP-5865
        popup.isFocusable = false

        // Text field is disabled so that the dates are only chooseable through the calendar widget. Then there is no
        // need to validate the dates
        dateEditor.setEnabled(true)
    }
}


class EditReferencePanel(ref: Record? = null, isAdvanced: Boolean) : JPanel(GridBagLayout()) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd")

    // fields. null if advanced
    private val isReferenceDescriptionCheckBox = JCheckBox("Is reference assayDescription")
    private val typeComboBox = if (isAdvanced) JComboBox<com.gmail.gcolaianni5.jris.bean.Type>() else null
    private val dateChooser = if (isAdvanced) FixedJDateChooser() else null
    private val pmidTextField = if (isAdvanced) JTextField(30) else null
    private val doiTextField = JTextField(30)
    private val authorListTextField = if (isAdvanced) JTextField(30) else null
    private val titleTextField = JTextField(30)
    private val abstractTextArea = if (isAdvanced) JTextArea(5, 30) else null
    private val journalTextField = if (isAdvanced) JTextField(30) else null
    private val volumeSpinnerModel = if (isAdvanced) createSpinnerIntegerModel() else null
    private val issueSpinnerModel = if (isAdvanced) createSpinnerIntegerModel() else null
    private val pageTextField = if (isAdvanced) JTextField(30) else null
    private val statusTextField = if (isAdvanced) JTextField(30) else null
    private val websiteTextField = if (isAdvanced) JTextField(30) else null
    private val commentTextField = if (isAdvanced) JTextArea(5, 30) else null

    companion object {
        val publicationType = "Publication type"
        val publicationDate = "Publication date"
        val pubMedId = "PubMed ID"
        val publicationDoi = "Publication DOI"
        val publicationAuthorList = "Publication author list"
        val publicationTitle = "Publication title"
        val publicationAbstract = "Publication abstract"
        val publicationJournal = "Publication journal"
        val publicationVolume = "Publication volume"
        val publicationIssue = "Publication issue"
        val publicationPage = "Publication page"
        val publicationStatus = "Publication status"
        val publicationWebsite = "Publication website"
        val comment = "Comment"
    }

    init {
        // Add types to typeComboBox and set the selected type
        typeComboBox?.let {
            com.gmail.gcolaianni5.jris.bean.Type.values().forEach { value -> it.addItem(value) }
            it.selectedItem = ref?.type
        }

        initUI()

        // Populate interface if ref is provided
        ref?.let {
            it.date?.let { dateChooser?.date = dateFormat.parse(it) }
            doiTextField.text = it.doi
            authorListTextField?.text = it.authors?.joinToString(";")
            titleTextField.text = it.title
            abstractTextArea?.text = it.abstr
            journalTextField?.text = it.secondaryTitle
            if (it.volumeNumber != null) volumeSpinnerModel?.value = it.volumeNumber
            if (it.issueNumber != null) issueSpinnerModel?.value = it.issueNumber
            websiteTextField?.text = it.websiteLink
        }
    }

    private fun initUI() {

        val pairList = mutableListOf<Pair<JLabel, JComponent>>()
        typeComboBox?.let { pairList.add(Pair(first = JLabel(publicationType), second = it)) }
        dateChooser?.let { pairList.add(Pair(first = JLabel(publicationDate), second = it)) }
        pmidTextField?.let { pairList.add(Pair(first = JLabel(pubMedId), second = it)) }
        pairList.add(Pair(first = JLabel(publicationDoi), second = doiTextField))
        authorListTextField?.let { pairList.add(Pair(first = JLabel(publicationAuthorList), second = it)) }
        pairList.add(Pair(first = JLabel(publicationTitle), second = titleTextField))
        abstractTextArea?.let { pairList.add(Pair(first = JLabel(publicationAbstract), second = it)) }
        journalTextField?.let { pairList.add(Pair(first = JLabel(publicationJournal), second = it)) }
        volumeSpinnerModel?.let {
            val spinner = createSpinner(it)
            pairList.add(Pair(first = JLabel(publicationVolume), second = spinner))
        }
        issueSpinnerModel?.let {
            val spinner = createSpinner(it)
            pairList.add(Pair(first = JLabel(publicationIssue), second = spinner))
        }
        pageTextField?.let { pairList.add(Pair(first = JLabel(publicationPage), second = it)) }
        statusTextField?.let { pairList.add(Pair(first = JLabel(publicationStatus), second = it)) }
        websiteTextField?.let { pairList.add(Pair(first = JLabel(publicationWebsite), second = it)) }
        commentTextField?.let { pairList.add(Pair(first = JLabel(comment), second = it)) }

        add(comp = isReferenceDescriptionCheckBox, gridy = 0, gridx = 0)
        for ((index, pair) in pairList.withIndex()) {
            val label = pair.first
            val field = pair.second
            label.labelFor = field
            add(comp = label, gridy = index + 1, gridx = 0)
            add(comp = field, gridy = index + 1, gridx = 1)
        }
    }

    fun toRecord(): Record {
        val risRecord = Record()
        // TODO: can't do anything with ReferencePanel.isReferenceDescriptionCheckBox yet
        risRecord.type = typeComboBox?.selectedItem as com.gmail.gcolaianni5.jris.bean.Type
        risRecord.date = dateChooser?.date?.toString()
        // TODO: can't do anything with PubMedId yet
        risRecord.doi = doiTextField.text
        if (!authorListTextField?.text.isNullOrEmpty()) {
            authorListTextField?.text?.split(";")?.forEach { risRecord.addAuthor(it) }
        }
        risRecord.title = titleTextField.text
        risRecord.abstr = abstractTextArea?.text
        risRecord.secondaryTitle = journalTextField?.text
        risRecord.volumeNumber = volumeSpinnerModel?.number?.toString()
        risRecord.issueNumber = issueSpinnerModel?.number?.toInt()
        // TODO: can't do anything with status yet
        risRecord.websiteLink = websiteTextField?.text
        // TODO: can't do anything with comment yet

        return risRecord
    }
}

class CreatorPanel(val creators: MutableList<VCard>) : JPanel(BorderLayout()) {

    init {
        border = BorderFactory.createTitledBorder("Creators")

        val dtm = NonEditableTableModel()
        creators.forEach { dtm.addRow(arrayOf(it)) }

        val renderer = object : DefaultTableCellRenderer() {
            override fun setValue(value: Any?) {
                value?.let {
                    val creator = value as VCard

                    val givenName = creator.nickname?.values?.get(0)
                    val familyName = creator.formattedName.value
                    val contact = creator.emails?.get(0)?.value

                    text = "${givenName}_${familyName}_$contact"
                }
            }
        }
        val myTable = HeadlessTable(model = dtm, renderer = renderer)

        // buttons
        val buttonsPanel = ButtonsPanel()
        buttonsPanel.addButton.addActionListener { _ ->
            val editPanel = EditCreatorPanel()
            val result = showConfirmDialog(panel = editPanel, title = "Create creator")
            if (result == JOptionPane.OK_OPTION) {
                dtm.addRow(arrayOf(editPanel.toVCard()))
            }
        }

        buttonsPanel.modifyButton.addActionListener { _ ->
            val rowToEdit = myTable.selectedRow
            if (rowToEdit != -1) {
                val creator = dtm.getValueAt(rowToEdit, 0) as VCard

                val editPanel = EditCreatorPanel(creator)
                val result = showConfirmDialog(panel = editPanel, title = "Modify creator")
                if (result == JOptionPane.OK_OPTION) {
                    dtm.setValueAt(editPanel.toVCard(), rowToEdit, 0)
                }
            }
        }

        buttonsPanel.removeButton.addActionListener { _ ->
            val rowToDelete = myTable.selectedRow
            if (rowToDelete != -1) dtm.removeRow(rowToDelete)
        }

        add(myTable, BorderLayout.NORTH)
        add(buttonsPanel, BorderLayout.SOUTH)
    }
}

class EditCreatorPanel(creator: VCard? = null) : JPanel(GridBagLayout()) {

    private val givenNameField = JTextField(30)
    private val familyNameField = JTextField(30)
    private val contactField = JTextField(30)

    companion object {
        val givenName = "Given name"
        val familyName = "Family name"
        val contact = "Contact"
    }

    init {
        initUI()

        // Populate interface if `creator` is provided
        creator?.let {
            givenNameField.text = it.nickname?.values?.firstOrNull()
            familyNameField.text = it.formattedName?.value
            contactField.text = it.emails?.first()?.value
        }
    }

    private fun initUI() {
        val pairList = listOf<Pair<JLabel, JComponent>>(
                Pair(first = JLabel(givenName), second = givenNameField),
                Pair(first = JLabel(familyName), second = familyNameField),
                Pair(first = JLabel(contact), second = contactField)
        )

        addGridComponents(pairs = pairList)
    }

    fun toVCard(): VCard {
        val vCard = VCard()
        givenNameField.text?.let { vCard.setNickname(it) }
        familyNameField.text?.let { vCard.setFormattedName(it) }
        contactField.text?.let { vCard.addEmail(it) }

        return vCard
    }
}

class ScopePanel(val scope: Scope) : Box(BoxLayout.PAGE_AXIS) {

    val productButton = JButton()
    val hazardButton = JButton()
    val populationButton = JButton()
    val commentField = JTextArea(5, 30)
    val dateChooser = FixedJDateChooser()
    val regionField = JTextField(30)
    val countryField = JTextField(30)

    val advancedCheckBox = JCheckBox("Advanced")

    companion object {
        val product = "Product"

        val hazard = "Hazard"

        val populationGroup = "Population group"

        val comment = "General comment"
        val commentTooltip = "General comments on the scope"

        val temporalInformation = "Temporal information"
        val temporalInformationTooltip = "Temporal information on which the model or data applies"

        val region = "Region"
        val regionTooltip = "Spatial information (area) on which the model or data applies"

        val country = "Country"
        val countryTooltip = "Country on which the model or data applies"
    }

    init {
        initUI()
    }

    private fun initUI() {
        val propertiesPanel = JPanel(GridBagLayout())

        productButton.toolTipText = "Click me to add a product"
        productButton.addActionListener { _ ->
            val editPanel = EditProductPanel(product = scope.product, isAdvanced = advancedCheckBox.isSelected)
            val result = showConfirmDialog(panel = editPanel, title = "Create a Product")
            if (result == JOptionPane.OK_OPTION) {
                val product = editPanel.toProduct()
                productButton.text = "${product.environmentName} [${product.environmentUnit}]"
                scope.product = product
            }
        }

        hazardButton.toolTipText = "Click me to add a hazard"
        hazardButton.addActionListener { _ ->
            val editPanel = EditHazardPanel(hazard = scope.hazard, isAdvanced = advancedCheckBox.isSelected)
            val result = showConfirmDialog(panel = editPanel, title = "Create a Hazard")
            if (result == JOptionPane.OK_OPTION) {
                val hazard = editPanel.toHazard()
                hazardButton.text = "${hazard.hazardName} [${hazard.hazardUnit}]"
                scope.hazard = hazard
            }
        }

        populationButton.toolTipText = "Click me to add a Population group"
        populationButton.addActionListener { _ ->
            val editPanel = EditPopulationGroupPanel(populationGroup = scope.populationGroup, isAdvanced = advancedCheckBox.isSelected)
            val result = showConfirmDialog(panel = editPanel, title = "Create a Population Group")
            if (result == JOptionPane.OK_OPTION) {
                val populationGroup = editPanel.toPopulationGroup()
                populationButton.text = populationGroup.populationName
                scope.populationGroup = populationGroup
            }
        }

        val pairList = listOf<Pair<JLabel, JComponent>>(
                Pair(first = JLabel(product), second = productButton),
                Pair(first = JLabel(hazard), second = hazardButton),
                Pair(first = JLabel(populationGroup), second = populationButton),
                Pair(first = createLabel(text = comment, tooltip = commentTooltip), second = commentField),
                Pair(first = createLabel(text = temporalInformation, tooltip = temporalInformationTooltip), second = dateChooser),
                Pair(first = createLabel(text = region, tooltip = regionTooltip), second = regionField),
                Pair(first = createLabel(text = country, tooltip = countryTooltip), second = countryField)
        )
        propertiesPanel.addGridComponents(pairs = pairList)

        // advancedCheckBox
        advancedCheckBox.addItemListener { _ -> println("dummy listener") }  // TODO: not implemented yet

        add(createAdvancedPanel(checkbox = advancedCheckBox))
        add(Box.createGlue())
        add(propertiesPanel)
    }
}

class EditProductPanel(product: Product? = null, isAdvanced: Boolean) : JPanel(GridBagLayout()) {

    companion object {
        val envName = "Environment name"
        val envNameTooltip = """
            |<html>
            |<p>The environment (animal, food product, matrix, etc.) for which the model
            |<p>or data applies
            |</html>
            """.trimMargin()

        val envDescription = "Environment description"
        val envDescriptionTooltip = """
            |<html>
            |<p>Description of the environment (animal, food product, matrix, etc.) for
            |<p>which the model or data applies
            |</html>
            """.trimMargin()

        val productionMethod = "Method of production"
        val productionMethodTooltip = "Type of production for the product/ matrix"

        val envUnit = "Environment unit"
        val envUnitTooltip = "Units of the environment for which the model or data applies"

        val packaging = "Packaging"
        val packagingTooltip = """
            |<html>
            |<p>Describe container or wrapper that holds the product/matrix. Common type
            |<p>of packaging: paper or plastic bags, boxes, tinplate or aluminium cans,
            |<p>plastic trays, plastic bottles, glass bottles or jars.
            |</html>
            """.trimMargin()

        val productTreatment = "Product treatment"
        val productTreatmentTooltip = """
            |<html>
            |<p>Used to characterise a product/matrix based on the treatment or
            |<p>processes applied to the product or any indexed ingredient.
            |</html>
            """.trimMargin()

        val originCountry = "Country of origin"
        val originCountryTooltip = "Country of origin of the food/product (ISO 3166 1-alpha-2 country code)"

        val originArea = "Area of origin"
        val originAreaTooltip = """
            |<html>
            |<p>Area of origin of the food/product (Nomenclature of territorial units
            |<p>for statistics – NUTS – coding system valid only for EEA and Switzerland).
            |</html>
            """.trimMargin()

        val fisheriesArea = "Fisheries area"
        val fisheriesAreaTooltip = """
            |<html>
            |<p>Fisheries or aquaculture area specifying the origin of the
            |<p>sample (FAO Fisheries areas).
            |</html>
            """.trimMargin()

        val productionDate = "Production date"
        val productionDateTooltip = "date of production of food/product"

        val expirationDate = "Expiration date"
        val expirationDateTooltip = "date of expiry of food/product"

        val moisturePercentage = "Moisture percentage"
        val moisturePercentageTooltip = "Percentage of moisture in the original sample"

        val fatPercentage = "Fat percentage"
        val fatPercentageTooltip = "Percentage of fat in the original sample"
    }

    // Fields. null if simple mode
    private val envNameField = AutoSuggestField(10)
    private val envDescriptionTextArea = if (isAdvanced) JTextArea(5, 30) else null
    private val envUnitField = AutoSuggestField(10)
    private val productionMethodComboBox = if (isAdvanced) JComboBox<String>() else null
    private val packagingComboBox = if (isAdvanced) JComboBox<String>() else null
    private val productTreatmentComboBox = if (isAdvanced) JComboBox<String>() else null
    private val originCountryField = if (isAdvanced) AutoSuggestField(10) else null
    private val originAreaField = if (isAdvanced) AutoSuggestField(10) else null
    private val fisheriesAreaField = if (isAdvanced) AutoSuggestField(10) else null
    private val productionDateChooser = if (isAdvanced) FixedJDateChooser() else null
    private val expirationDateChooser = if (isAdvanced) FixedJDateChooser() else null
    private val moisturePercentageSpinnerModel = if (isAdvanced) createSpinnerPercentageModel() else null
    private val fatPercentageSpinnerModel = if (isAdvanced) createSpinnerPercentageModel() else null

    init {
        product?.let {
            envNameField.selectedItem = it.environmentName
            envDescriptionTextArea?.text = it.environmentDescription
            envUnitField.selectedItem = it.environmentUnit

            /*
            TODO: init value of packagingComboBox
            TODO: init value of productTreatmentComboBox
            I ignore currently how to set a number of selected items in Swing ComboBox. KNIME include a list
            selection widget that supports this but lacks the autocompletion feature from the FCL widget.

            KNIME widget would be a temporary solution but ideally a new widget based on the KNIME widget and including
               the autocompletion feature should be developed.
            */
            originCountryField?.selectedItem = it.countryOfOrigin
            originAreaField?.selectedItem = it.areaOfOrigin
            fisheriesAreaField?.selectedItem = it.fisheriesArea
            productionDateChooser?.date = it.productionDate
            expirationDateChooser?.date = it.expirationDate
            if (it.moisturePercentage != null) moisturePercentageSpinnerModel?.value = it.moisturePercentage
            if (it.fatPercentage != null) fatPercentageSpinnerModel?.value = it.fatPercentage
        }
        initUI()
    }

    private fun initUI() {

        // Create labels
        val envNameLabel = createLabel(text = envName, tooltip = envNameTooltip)
        val envDescriptionLabel = createLabel(text = envDescription, tooltip = envDescriptionTooltip)
        val envUnitLabel = createLabel(text = envUnit, tooltip = envUnitTooltip)
        val productionMethodLabel = createLabel(text = productionMethod, tooltip = productionMethodTooltip)
        val packagingLabel = createLabel(text = packaging, tooltip = packagingTooltip)
        val productTreatmentLabel = createLabel(text = productTreatment, tooltip = productTreatmentTooltip)
        val originCountryLabel = createLabel(text = originCountry, tooltip = originCountryTooltip)
        val originAreaLabel = createLabel(text = originArea, tooltip = originAreaTooltip)
        val fisheriesAreaLabel = createLabel(text = fisheriesArea, tooltip = fisheriesAreaTooltip)
        val productionDateLabel = createLabel(text = productionDate, tooltip = productionDateTooltip)
        val expirationDateLabel = createLabel(text = expirationDate, tooltip = expirationDateTooltip)
        val moisturePercentageLabel = createLabel(text = moisturePercentage, tooltip = moisturePercentageTooltip)
        val fatPercentageLabel = createLabel(text = fatPercentage, tooltip = fatPercentageTooltip)

        // Init combo boxes
        envNameField.setPossibleValues(vocabs.get("Environment name"))
        envUnitField.setPossibleValues(vocabs.get("Environment unit"))
        productionMethodComboBox?.let { vocabs["Method of production"]?.forEach(it::addItem) }
        packagingComboBox?.let { vocabs["Packaging"]?.forEach(it::addItem) }
        productTreatmentComboBox?.let { vocabs["Product treatment"]?.forEach(it::addItem) }
        originCountryField?.setPossibleValues(vocabs["Country of origin"])
        originAreaField?.setPossibleValues(vocabs["Area of origin"])
        fisheriesAreaField?.setPossibleValues(vocabs["Fisheries area"])

        val pairList = mutableListOf<Pair<JLabel, JComponent>>()
        pairList.add(Pair(first = envNameLabel, second = envNameField))
        envDescriptionTextArea?.let { pairList.add(Pair(first = envDescriptionLabel, second = it)) }
        pairList.add(Pair(first = envUnitLabel, second = envUnitField))
        productionMethodComboBox?.let { pairList.add(Pair(first = productionMethodLabel, second = it)) }
        packagingComboBox?.let { pairList.add(Pair(first = packagingLabel, second = it)) }
        productTreatmentComboBox?.let { pairList.add(Pair(first = productTreatmentLabel, second = it)) }
        originCountryField?.let { pairList.add(Pair(first = originCountryLabel, second = it)) }
        originAreaField?.let { pairList.add(Pair(first = originAreaLabel, second = it)) }
        fisheriesAreaField?.let { pairList.add(Pair(first = fisheriesAreaLabel, second = it)) }
        productionDateChooser?.let { pairList.add(Pair(first = productionDateLabel, second = it)) }
        expirationDateChooser?.let { pairList.add(Pair(first = expirationDateLabel, second = it)) }
        moisturePercentageSpinnerModel?.let {
            val spinner = createSpinner(it)
            pairList.add(Pair(first = moisturePercentageLabel, second = spinner))
        }
        fatPercentageSpinnerModel?.let {
            val spinner = createSpinner(it)
            pairList.add(Pair(first = fatPercentageLabel, second = spinner))
        }

        addGridComponents(pairs = pairList)
    }

    // TODO: toProduct
    fun toProduct(): Product {

        val envUnit = if (envUnitField.selectedItem == null) "" else envUnitField.selectedItem as String

        val product = Product(environmentName = envNameField.selectedItem as String, environmentUnit = envUnit)
        envDescriptionTextArea?.text?.let { product.environmentDescription = it }
        packagingComboBox?.selectedObjects?.forEach { product.packaging.add(it as String) }
//        productTreatmentComboBox?.  // TODO: product treatment
//        productTreatmentComboBox?.selectedObjects?.forEach { product.}
        product.countryOfOrigin = originCountryField?.selectedItem as String?
        product.areaOfOrigin = originAreaField?.selectedItem as String?
        product.fisheriesArea = fisheriesAreaField?.selectedItem as String?
        product.productionDate = productionDateChooser?.date
        product.expirationDate = expirationDateChooser?.date
        product.moisturePercentage = moisturePercentageSpinnerModel?.number?.toDouble()
        product.fatPercentage = fatPercentageSpinnerModel?.number?.toDouble()

        return product
    }
}

class EditHazardPanel(hazard: Hazard? = null, isAdvanced: Boolean) : JPanel(GridBagLayout()) {

    companion object {
        val hazardType = "Hazard type"
        val hazardTypeTooltip = "General classification of the hazard"

        val hazardName = "Hazard name"
        val hazardNameTooltip = "Name of the hazard for which the model or data applies"

        val hazardDescription = "Hazard description"
        val hazardDescriptionTooltip = "Description of te hazard for which the model or data applies"

        val hazardUnit = "Hazard unit"
        val hazardUnitTooltip = "Unit of the hazard for which the model or data applies"

        val adverseEffect = "Adverse effect"
        val adverseEffectTooltip = "morbity, mortality, effect"

        val origin = "Origin"
        val originTooltip = "source of contamination, source"

        val bmd = "Benchmark dose, BMD"
        val bmdTooltip = "A dose or concentration that produces a predetermined change in response rate of an adverse effect"

        val maxResidueLimit = "Maximumm Residue Limit"
        val maxResidueLimitTooltip = "International regulations and permissible maximum residue levels in food and drinking water"

        val noObservedAdverse = "No observed adverse"
        val noObservedAdverseTooltip = "Level of exposure of an organism, found by experiment or observation"

        val lowestObserve = "Lowest observe"
        val lowestObserveTooltip = """
            |<html>
            |<p>Lowest concentration or amount of a substance found by experiment or observation
            |<p>that causes an adverse alteration of morphology, function, capacity, growth,
            |<p>development, or lifespan of a target organism distinguished from normal organisms
            |<p>of the same species under defined conditions of exposure.
            |</html>
            """.trimMargin()

        val acceptableOperator = "Acceptable operator"
        val acceptableOperatorTooltip = """
            |<html>
            |<p>Maximum amount of active substance to which the operator may be exposed without
            |<p>any adverse health effects. The AOEL is expressed as milligrams of the chemical
            |<p>per kilogram body weight of the operator.
            |</html>
            """.trimMargin()

        val acuteReferenceDose = "Acute reference dose"
        val acuteReferenceDoseTooltip = """
            |<html>
            |<p>An estimate (with uncertainty spanning perhaps an order of magnitude) of daily
            |<p>oral exposure for an acute duration (24 hours or less) to the human population
            |<p>including sensitive subgroups) that is likely to be without an appreciate risk
            |<p>of deleterious effects during a lifetime.
            |</html>
            """.trimMargin()

        val acceptableDailyIntake = "Acceptable daily intake"
        val acceptableDailyIntakeTooltip = """
            |<html>
            |<p>measure of amount of a specific substance in food or in drinking water that can
            |<p>be ingested (orally) on a daily basis over a lifetime without an appreciable
            |<p>health risk.
            |</html>
            """.trimMargin()

        val indSum = "Hazard ind/sum"
        val indSumTooltip = """
            |<html>
            |<p>Define if the parameter reported is an individual residue/analyte, a summed
            |<p>residue definition or part of a sum a summed residue definition.
            |</html>
            """.trimMargin()

        val labName = "Laboratory name"
        val labNameTooltip = """
            |<html>
            |<p>Laboratory code (National laboratory code if available) or Laboratory name
            |</html>
            """.trimMargin()

        val labCountry = "Laboratory country"
        val labCountryTooltip = "Country where the laboratory is placed. (ISO 3166-1-alpha-2)."

        val detectionLimit = "Limit of detection"
        val detectionLimitTooltip = """
            |<html>
            |<p>Limit of detection reported in the unit specified by the variable “Hazard unit”.
            |</html>
            """.trimMargin()

        val quantificationLimit = "Limit of quantification"
        val quantificationLimitTootlip = """
            |<html>
            |<p>Limit of quantification reported in the unit specified by the variable “Hazard
            |<p>unit”
            |</html>
            """.trimMargin()

        val leftCensoredData = "Left-censored data"
        val leftCensoredDataTooltip = "percentage of measures equal to LOQ and/or LOD"

        val contaminationRange = "Range of contamination"
        val contaminationRangeTooltip = """
            |<html>
            |<p>Range of result of the analytical measure reported in the unit specified by the
            |<p>variable “Hazard unit”
            |</html>
            """.trimMargin()
    }

    // Fields. Null if simple mode.
    private val hazardTypeComboBox = JComboBox<String>()
    private val hazardNameComboBox = JComboBox<String>()
    private val hazardDescriptionTextArea = if (isAdvanced) JTextArea(5, 30) else null
    private val hazardUnitComboBox = JComboBox<String>()
    private val adverseEffectTextField = if (isAdvanced) JTextField(30) else null
    private val originTextField = if (isAdvanced) JTextField(30) else null
    private val bmdTextField = if (isAdvanced) JTextField(30) else null
    private val maxResidueLimitTextField = if (isAdvanced) JTextField(30) else null
    private val noObservedAdverseTextField = if (isAdvanced) JTextField(30) else null
    private val lowestObserveTextField = if (isAdvanced) JTextField(30) else null
    private val acceptableOperatorTextField = if (isAdvanced) JTextField(30) else null
    private val acuteReferenceDoseTextField = if (isAdvanced) JTextField(30) else null
    private val acceptableDailyIntakeTextField = if (isAdvanced) JTextField(30) else null
    private val indSumTextField = if (isAdvanced) JTextField(30) else null
    private val labNameTextField = if (isAdvanced) JTextField(30) else null
    private val labCountryComboBox = if (isAdvanced) JComboBox<String>() else null
    private val detectionLimitTextField = if (isAdvanced) JTextField(30) else null
    private val quantificationLimitTextField = if (isAdvanced) JTextField(30) else null
    private val leftCensoredDataTextField = if (isAdvanced) JTextField(30) else null
    private val contaminationRangeTextField = if (isAdvanced) JTextField(30) else null

    init {
        // Populate interface if `hazard` is passed
        hazard?.let {
            hazardTypeComboBox.selectedItem = it.hazardType
            hazardNameComboBox.selectedItem = it.hazardName
            hazardDescriptionTextArea?.text = it.hazardDescription
            hazardUnitComboBox.selectedItem = it.hazardUnit
            adverseEffectTextField?.text = it.adverseEffect
            originTextField?.text = it.origin
            bmdTextField?.text = it.benchmarkDose
            maxResidueLimitTextField?.text = it.maximumResidueLimit
            noObservedAdverseTextField?.text = it.noObservedAdverse
            acceptableOperatorTextField?.text = it.acceptableOperator
            acuteReferenceDoseTextField?.text = it.acuteReferenceDose
            indSumTextField?.text = it.hazardIndSum
            acceptableDailyIntakeTextField?.text = it.acceptableDailyIntake
            labNameTextField?.text = it.laboratoryName
            labCountryComboBox?.selectedItem = it.laboratoryCountry
            detectionLimitTextField?.text = it.detectionLimit
            quantificationLimitTextField?.text = it.quantificationLimit
            leftCensoredDataTextField?.text = it.leftCensoredData
            contaminationRangeTextField?.text = it.rangeOfContamination
        }

        initUI()
    }

    private fun initUI() {

        val pairList = mutableListOf<Pair<JLabel, JComponent>>()

        val hazardTypeLabel = createLabel(text = hazardType, tooltip = hazardTypeTooltip)
        val hazardNameLabel = createLabel(text = hazardName, tooltip = hazardNameTooltip)
        val hazardDescriptionLabel = createLabel(text = hazardDescription, tooltip = hazardDescriptionTooltip)
        val hazardUnitLabel = createLabel(text = hazardUnit, tooltip = hazardUnitTooltip)
        val adverseEffectLabel = createLabel(text = adverseEffect, tooltip = adverseEffectTooltip)
        val originLabel = createLabel(text = origin, tooltip = originTooltip)
        val bmdLabel = createLabel(text = bmd, tooltip = bmdTooltip)
        val maxResidueLimitLabel = createLabel(text = maxResidueLimit, tooltip = maxResidueLimitTooltip)
        val noObservedAdverseLabel = createLabel(text = noObservedAdverse, tooltip = noObservedAdverseTooltip)
        val lowestObserveLabel = createLabel(text = lowestObserve, tooltip = lowestObserveTooltip)
        val acceptableOperatorLabel = createLabel(text = acceptableOperator, tooltip = acceptableOperatorTooltip)
        val acuteReferenceDoseLabel = createLabel(text = acuteReferenceDose, tooltip = acuteReferenceDoseTooltip)
        val acceptableDailyIntakeLabel = createLabel(text = acceptableDailyIntake, tooltip = acceptableDailyIntakeTooltip)
        val indSumLabel = createLabel(text = indSum, tooltip = indSumTooltip)
        val labNameLabel = createLabel(text = labName, tooltip = labNameTooltip)
        val labCountryLabel = createLabel(text = labCountry, tooltip = labCountryTooltip)
        val detectionLimitLabel = createLabel(text = detectionLimit, tooltip = detectionLimitTooltip)
        val quantificationLimitLabel = createLabel(text = quantificationLimit, tooltip = quantificationLimitTootlip)
        val leftCensoredDataLabel = createLabel(text = leftCensoredData, tooltip = leftCensoredDataTooltip)
        val contaminationRangeLabel = createLabel(text = contaminationRange, tooltip = contaminationRangeTooltip)

        pairList.add(Pair(first = hazardTypeLabel, second = hazardTypeComboBox))
        pairList.add(Pair(first = hazardNameLabel, second = hazardNameComboBox))
        hazardDescriptionTextArea?.let { pairList.add(Pair(first = hazardDescriptionLabel, second = it)) }
        pairList.add(Pair(first = hazardUnitLabel, second = hazardUnitComboBox))
        adverseEffectTextField?.let { pairList.add(Pair(first = adverseEffectLabel, second = it)) }
        originTextField?.let { pairList.add(Pair(first = originLabel, second = it)) }
        bmdTextField?.let { pairList.add(Pair(first = bmdLabel, second = it)) }
        maxResidueLimitTextField?.let { pairList.add(Pair(first = maxResidueLimitLabel, second = it)) }
        noObservedAdverseTextField?.let { pairList.add(Pair(first = noObservedAdverseLabel, second = it)) }
        lowestObserveTextField?.let { pairList.add(Pair(first = lowestObserveLabel, second = it)) }
        acceptableOperatorTextField?.let { pairList.add(Pair(first = acceptableOperatorLabel, second = it)) }
        acuteReferenceDoseTextField?.let { pairList.add(Pair(first = acuteReferenceDoseLabel, second = it)) }
        acceptableDailyIntakeTextField?.let { pairList.add(Pair(first = acceptableDailyIntakeLabel, second = it)) }
        indSumTextField?.let { pairList.add(Pair(first = indSumLabel, second = it)) }
        labNameTextField?.let { pairList.add(Pair(first = labNameLabel, second = it)) }
        labCountryComboBox?.let { pairList.add(Pair(first = labCountryLabel, second = it)) }
        detectionLimitTextField?.let { pairList.add(Pair(first = detectionLimitLabel, second = it)) }
        quantificationLimitTextField?.let { pairList.add(Pair(first = quantificationLimitLabel, second = it)) }
        leftCensoredDataTextField?.let { pairList.add(Pair(first = leftCensoredDataLabel, second = it)) }
        contaminationRangeTextField?.let { pairList.add(Pair(first = contaminationRangeLabel, second = it)) }

        addGridComponents(pairs = pairList)
    }

    fun toHazard(): Hazard {
        // Collect mandatory properties first.
        /*
        TODO: safe-cast comboboxes temporarily to empty strings.
        Should be validated so that one item is always selected
         */
        val type = if (hazardTypeComboBox.selectedItem == null) "" else hazardTypeComboBox.selectedItem as String
        val hazardName = if (hazardNameComboBox.selectedItem == null) "" else hazardNameComboBox.selectedItem as String
        val hazardUnit = if (hazardUnitComboBox.selectedItem == null) "" else hazardUnitComboBox.selectedItem as String

        val hazard = Hazard(hazardType = type, hazardName = hazardName, hazardUnit = hazardUnit)

        hazard.hazardDescription = hazardDescriptionTextArea?.text
        hazard.adverseEffect = adverseEffectTextField?.text
        hazard.origin = originTextField?.text
        hazard.benchmarkDose = bmdTextField?.text
        hazard.maximumResidueLimit = maxResidueLimitTextField?.text
        hazard.noObservedAdverse = noObservedAdverseTextField?.text
        hazard.acceptableOperator = acceptableOperatorTextField?.text
        hazard.acuteReferenceDose = acuteReferenceDoseTextField?.text
        hazard.acceptableDailyIntake = acceptableDailyIntakeTextField?.text
        // TODO: hazardIndSum
        hazard.laboratoryName = labNameTextField?.text
        hazard.laboratoryCountry = if (labCountryComboBox?.selectedItem == null) {
            ""
        } else {
            labCountryComboBox.selectedItem as String
        }
        hazard.detectionLimit = detectionLimitTextField?.text
        hazard.quantificationLimit = quantificationLimitTextField?.text
        hazard.leftCensoredData = leftCensoredDataTextField?.text
        hazard.rangeOfContamination = contaminationRangeTextField?.text

        return hazard
    }
}

class EditPopulationGroupPanel(populationGroup: PopulationGroup? = null, isAdvanced: Boolean) : JPanel(GridBagLayout()) {

    companion object {
        val populationName = "Population name"
        val populationNameTooltip = "Name of the population for which the model or data applies"

        val targetPopulation = "Target population"
        val targetPopulationTooltip = """
            |<html>
            |<p>population of individual that we are interested in describing and making
            |<p>statistical inferences about
            |</html>
            """.trimMargin()

        val populationSpan = "Population span"
        val populationSpanTooltip = """
            |<html>
            |<p>Temporal information on the exposure pattern of the population to the
            |<p>hazard SUGGESTION: Temporal information on the exposure of the
            |<p>population to the hazard OR Temporal information on the exposure period
            |<p>of the population to the hazard.
            |</html>
            """.trimMargin()

        val populationDescription = "Population assayDescription"
        val populationDescriptionTooltip = """
            |<html>
            |<p>Description of the population for which the model applies (demographic
            |<p>and socio-economic characteristics for example). Background information
            |<p>that are needed in the data analysis phase: size of household, education
            |<p>level, employment status, professional category, ethnicity, etc.
            |</html>
            """.trimMargin()

        val populationAge = "Population age"
        val populationAgeTooltip = "describe the range of age or group of age"

        val populationGender = "Population gender"
        val populationGenderTooltip = "describe the percentage of gender"

        val bmi = "BMI"
        val bmiTooltip = "describe the range of BMI or class of BMI or BMI mean"

        val specialDietGroups = "Special diet groups"
        val specialDietGroupsTooltip = """
            |<html>
            |<p>sub-population with special diets (vegetarians, diabetics, group
            |<p>following special ethnic diets)
            |</html>
            """

        val patternConsumption = "Pattern consumption"
        val patternConsumptionTooltip = """
            |<html>
            |<p>describe the consumption of different food items: frequency, portion
            |<p>size
            |</html>
            """.trimMargin()

        val region = "Region"
        val regionTooltip = "Spatial information (area) on which the model or data applies"

        val country = "Country"
        val countryTooltip = "Country on which the model or data applies"

        val riskAndPopulation = "Risk and population"
        val riskAndPopulationTooltip = """
            |<html>
            |<p>population risk factor that may influence the outcomes of the
            |<p>study, confounder should be included
            |</html>
            """.trimMargin()

        val season = "Season"
        val seasonTooltip = "distribution of surveyed people according to the season (influence consumption pattern)"
    }

    private val populationNameTextField = JTextField(30)
    private val targetPopulationTextField = if (isAdvanced) JTextField(30) else null
    private val populationSpanTextField = if (isAdvanced) JTextField(30) else null
    private val populationDescriptionTextArea = if (isAdvanced) JTextField(30) else null
    private val populationAgeTextField = if (isAdvanced) JTextField(30) else null
    private val populationGenderTextField = if (isAdvanced) JTextField(30) else null
    private val bmiTextField = if (isAdvanced) JTextField(30) else null
    private val specialDietGroupTextField = if (isAdvanced) JTextField(30) else null
    private val patternConsumptionTextField = if (isAdvanced) JTextField(30) else null
    private val regionComboBox = if (isAdvanced) JComboBox<String>() else null
    private val countryComboBox = if (isAdvanced) JComboBox<String>() else null
    private val riskAndPopulationTextField = if (isAdvanced) JTextField(30) else null
    private val seasonTextField = if (isAdvanced) JTextField(30) else null

    init {
        // Populate interface if `populationGroup` is passed
        populationGroup?.let {
            populationNameTextField.text = it.populationName
            targetPopulationTextField?.text = it.targetPopulation
            populationSpanTextField?.text = it.populationSpan[0]
            populationDescriptionTextArea?.text = it.populationDescription[0]
            populationAgeTextField?.text = it.populationAge[0]
            populationGenderTextField?.text = it.populationAge[0]
            bmiTextField?.text = it.bmi[0]
            specialDietGroupTextField?.text = it.specialDietGroups[0]
            patternConsumptionTextField?.text = it.patternConsumption[0]
            regionComboBox?.selectedItem = it.region
            countryComboBox?.selectedItem = it.country
            riskAndPopulationTextField?.text = it.populationRiskFactor[0]
            seasonTextField?.text = it.season[0]
        }

        initUI()
    }

    private fun initUI() {
        val populationNameLabel = createLabel(text = populationName, tooltip = populationNameTooltip)
        val targetPopulationLabel = createLabel(text = targetPopulation, tooltip = targetPopulationTooltip)
        val populationSpanLabel = createLabel(text = populationSpan, tooltip = populationSpanTooltip)
        val populationDescriptionLabel = createLabel(text = populationDescription, tooltip = populationDescriptionTooltip)
        val populationAgeLabel = createLabel(text = populationAge, tooltip = populationAgeTooltip)
        val populationGenderLabel = createLabel(text = populationGender, tooltip = populationGenderTooltip)
        val bmiLabel = createLabel(text = bmi, tooltip = bmiTooltip)
        val specialDietGroupLabel = createLabel(text = specialDietGroups, tooltip = specialDietGroupsTooltip)
        val patternConsumptionLabel = createLabel(text = patternConsumption, tooltip = patternConsumptionTooltip)
        val regionLabel = createLabel(text = region, tooltip = regionTooltip)
        val countryLabel = createLabel(text = country, tooltip = countryTooltip)
        val riskAndPopulationLabel = createLabel(text = riskAndPopulation, tooltip = riskAndPopulationTooltip)
        val seasonLabel = createLabel(text = season, tooltip = seasonTooltip)

        val pairList = mutableListOf<Pair<JLabel, JComponent>>()
        pairList.add(Pair(first = populationNameLabel, second = populationNameTextField))
        targetPopulationTextField?.let { pairList.add(Pair(first = targetPopulationLabel, second = it)) }
        populationSpanTextField?.let { pairList.add(Pair(first = populationSpanLabel, second = it)) }
        populationDescriptionTextArea?.let { pairList.add(Pair(first = populationDescriptionLabel, second = it)) }
        populationAgeTextField?.let { pairList.add(Pair(first = populationAgeLabel, second = it)) }
        populationGenderTextField?.let { pairList.add(Pair(first = populationGenderLabel, second = it)) }
        bmiTextField?.let { pairList.add(Pair(first = bmiLabel, second = it)) }
        specialDietGroupTextField?.let { pairList.add(Pair(first = specialDietGroupLabel, second = it)) }
        patternConsumptionTextField?.let { pairList.add(Pair(first = patternConsumptionLabel, second = it)) }
        regionComboBox?.let { pairList.add(Pair(first = regionLabel, second = it)) }
        countryComboBox?.let { pairList.add(Pair(first = countryLabel, second = it)) }
        riskAndPopulationTextField?.let { pairList.add(Pair(first = riskAndPopulationLabel, second = it)) }
        seasonTextField?.let { pairList.add(Pair(first = seasonLabel, second = it)) }

        addGridComponents(pairs = pairList)
    }

    fun toPopulationGroup(): PopulationGroup {

        val populationGroup = PopulationGroup(populationName = populationNameTextField.text)
        populationGroup.targetPopulation = targetPopulationTextField?.text
        populationSpanTextField?.let { populationGroup.populationSpan.add(it.text) }
        populationDescriptionTextArea?.let { populationGroup.populationDescription.add(it.text) }
        populationAgeTextField?.let { populationGroup.populationAge.add(it.text) }
        populationGroup.populationGender = populationGenderTextField?.text
        bmiTextField?.let { populationGroup.bmi.add(it.text) }
        specialDietGroupTextField?.let { populationGroup.specialDietGroups.add(it.text) }
        patternConsumptionTextField?.let { populationGroup.patternConsumption.add(it.text) }
        regionComboBox?.selectedObjects?.forEach { populationGroup.region.add(it as String) }
        countryComboBox?.selectedObjects?.forEach { populationGroup.country.add(it as String) }
        riskAndPopulationTextField?.let { populationGroup.populationRiskFactor.add(it.text) }
        seasonTextField?.let { populationGroup.season.add(it.text) }

        return populationGroup
    }
}

class DataBackgroundPanel(var dataBackground: DataBackground? = null) : Box(BoxLayout.PAGE_AXIS) {

    companion object {
        val studySample = "Study sample"
        val dietaryAssessmentMethod = "Dietary assessment method"
        val laboratoryAccreditation = "Laboratory accreditation"
        val assay = "Assay"
    }

    val advancedCheckBox = JCheckBox("Advanced")

    init {
        initUI()
    }

    private fun initUI() {

        val propertiesPanel = JPanel(GridBagLayout())

        val studyPanel = StudyPanel()
        studyPanel.border = BorderFactory.createTitledBorder("Study")

        val studySampleButton = JButton()
        studySampleButton.toolTipText = "Click me to add Study Sample"
        studySampleButton.addActionListener { _ ->
            val editPanel = EditStudySamplePanel(studySample = dataBackground?.studySample, isAdvanced = advancedCheckBox.isSelected)
            val result = showConfirmDialog(panel = editPanel, title = "Create Study sample")
            if (result == JOptionPane.OK_OPTION) {
                val studySample = editPanel.toStudySample()

                if (dataBackground == null) dataBackground = DataBackground()
                dataBackground?.studySample = studySample
                println(studySample)
            }
        }

        val dietaryAssessmentMethodButton = JButton()
        dietaryAssessmentMethodButton.toolTipText = "Click me to add Dietary assessment method"
        dietaryAssessmentMethodButton.addActionListener { _ ->
            val editPanel = EditDietaryAssessmentMethodPanel(
                    dietaryAssessmentMethod = dataBackground?.dietaryAssessmentMethod, isAdvanced = advancedCheckBox.isSelected)
            val result = showConfirmDialog(panel = editPanel, title = "Create dietary assessment method")
            if (result == JOptionPane.OK_OPTION) {
                val dietaryAssessmentMethod = editPanel.toDietaryAssessmentMethod()

                if (dataBackground == null) dataBackground = DataBackground(dietaryAssessmentMethod = dietaryAssessmentMethod)
                else dataBackground?.dietaryAssessmentMethod = dietaryAssessmentMethod
            }
        }

        val assayButton = JButton()
        assayButton.toolTipText = "Click me to add Assay"
        assayButton.addActionListener { _ ->
            val editPanel = EditAssayPanel(assay = dataBackground?.assay, isAdvanced = advancedCheckBox.isSelected)
            val result = showConfirmDialog(panel = editPanel, title = "Create assay")
            if (result == JOptionPane.OK_OPTION) {
                val assay = editPanel.toAssay()

                if (dataBackground == null) dataBackground = DataBackground(assay = assay)
                else dataBackground?.assay = assay

            }
        }

        propertiesPanel.add(comp = studyPanel, gridy = 0, gridx = 0, gridwidth = 3)
        propertiesPanel.add(comp = JLabel(studySample), gridy = 1, gridx = 0)
        propertiesPanel.add(comp = studySampleButton, gridy = 1, gridx = 1)

        propertiesPanel.add(comp = JLabel(dietaryAssessmentMethod), gridy = 2, gridx = 0)
        propertiesPanel.add(comp = dietaryAssessmentMethodButton, gridy = 2, gridx = 1)

        propertiesPanel.add(comp = JLabel(laboratoryAccreditation), gridy = 3, gridx = 0)

        propertiesPanel.add(comp = JLabel(assay), gridy = 4, gridx = 0)
        propertiesPanel.add(comp = assayButton, gridy = 4, gridx = 1)

        // `Advanced` checkbox
        advancedCheckBox.addItemListener { _ ->
            studyPanel.toggleAdvancedMode()
            println("dummy listener")  // TODO: implement listener
        }

        add(createAdvancedPanel(checkbox = advancedCheckBox))
        add(Box.createGlue())
        add(propertiesPanel)
    }
}

class StudyPanel(study: Study? = null) : JPanel(GridBagLayout()) {

    companion object {
        val studyIdentifier = "Study identifier"
        val studyIdentifierTooltip = "A user defined identifier for the study"

        val studyTitle = "Study title"
        val studyTitleTooltip = "A title for the Study."

        val studyDescription = "Study assayDescription"
        val studyDescriptionTooltip = "A brief assayDescription of the study aims."

        val studyDesignType = "Study design type"
        val studyDesignTypeTooltip = "The type of study design being employed"

        val studyAssayMeasurements = "Study assay measurements"
        val studyAssayMeasurementsTooltip = "The measurement being observed in this assay"

        val studyAssayTechnology = "Study assay technology"
        val studyAssayTechnologyTooltip = "The technology being employed to observe this measurement"

        val accreditationProcedure = "Accreditation procedure"
        val accreditationProcedureTooltip = "The type of study design being employed"

        val studyProtocolName = "Study protocol name"
        val studyProtocolNameTooltip = "The name of the protocol, e.g.Extraction Protocol"

        val studyProtocolType = "Study protocol type"
        val studyProtocolTypeTooltip = """
            |<html>
            |<p>The type of the protocol, preferably coming from an Ontology, e.g.
            |<p>Extraction Protocol
            |</html>
            """.trimMargin()

        val studyProtocolDescription = "Study protocol"
        val studyProtocolDescriptionTooltip = "A description of the Protocol."

        val studyProtocolURI = "Study protocol URI"
        val studyProtocolURITooltip = "A URI to link out to a publication, web page, etc. describing the protocol."

        val studyProtocolVersion = "Study protocol version"
        val studyProtocolVersionTooltip = "The version of the protocol used, where applicable."

        val studyProtocolParameters = "Study protocol parameters"
        val studyProtocolParametersTooltip = "The parameters used when executing this protocol."

        val studyProtocolComponents = "Study protocol components"
        val studyProtocolComponentsTooltip = "The components used when carrying out this protocol."
    }

    val studyIdentifierLabel = createLabel(text = studyIdentifier, tooltip = studyIdentifierTooltip)
    val studyIdentifierTextField = JTextField(30)

    val studyTitleLabel = createLabel(text = studyTitle, tooltip = studyTitleTooltip)
    val studyTitleTextField = JTextField(30)

    val studyDescriptionLabel = createLabel(text = studyDescription, tooltip = studyDescriptionTooltip)
    val studyDescriptionTextArea = JTextArea(5, 30)

    val studyDesignTypeLabel = createLabel(text = studyDesignType, tooltip = studyDesignTypeTooltip)
    val studyDesignTypeComboBox = JComboBox<String>()

    val studyAssayMeasurementsLabel = createLabel(text = studyAssayMeasurements, tooltip = studyAssayMeasurementsTooltip)
    val studyAssayMeasurementsComboBox = JComboBox<String>()

    val studyAssayTechnologyLabel = createLabel(text = studyAssayTechnology, tooltip = studyAssayTechnologyTooltip)
    val studyAssayTechnologyComboBox = JComboBox<String>()

    val accreditationProcedureLabel = createLabel(text = accreditationProcedure, tooltip = accreditationProcedureTooltip)
    val accreditationProcedureComboBox = JComboBox<String>()

    val studyProtocolNameLabel = createLabel(text = studyProtocolName, tooltip = studyProtocolNameTooltip)
    val studyProtocolNameTextField = JTextField(30)

    val studyProtocolTypeLabel = createLabel(text = studyProtocolType, tooltip = studyProtocolTypeTooltip)
    val studyProtocolTypeComboBox = JComboBox<String>()

    val studyProtocolDescriptionLabel = createLabel(text = studyProtocolDescription, tooltip = studyProtocolDescriptionTooltip)
    val studyProtocolDescriptionTextField = JTextField(30)

    val studyProtocolURILabel = createLabel(text = studyProtocolURI, tooltip = studyProtocolURITooltip)
    val studyProtocolURITextField = JTextField(30)

    val studyProtocolVersionLabel = createLabel(text = studyProtocolVersion, tooltip = studyProtocolVersionTooltip)
    val studyProtocolVersionTextField = JTextField(30)

    val studyProtocolParametersLabel = createLabel(text = studyProtocolParameters, tooltip = studyProtocolParametersTooltip)
    val studyProtocolParametersComboBox = JComboBox<String>()

    val studyProtocolComponentsLabel = createLabel(text = studyProtocolComponents, tooltip = studyProtocolComponentsTooltip)
    val studyProtocolComponentsComboBox = JComboBox<String>()

    init {

        // hide advanced elements initially
        studyDescriptionLabel.isVisible = false
        studyDescriptionTextArea.isVisible = false

        studyDesignTypeLabel.isVisible = false
        studyDesignTypeComboBox.isVisible = false

        studyAssayMeasurementsLabel.isVisible = false
        studyAssayMeasurementsComboBox.isVisible = false

        studyAssayTechnologyLabel.isVisible = false
        studyAssayTechnologyComboBox.isVisible = false

        accreditationProcedureLabel.isVisible = false
        accreditationProcedureComboBox.isVisible = false

        studyProtocolNameLabel.isVisible = false
        studyProtocolNameTextField.isVisible = false

        studyProtocolTypeLabel.isVisible = false
        studyProtocolTypeComboBox.isVisible = false

        studyProtocolDescriptionLabel.isVisible = false
        studyProtocolDescriptionTextField.isVisible = false

        studyProtocolURILabel.isVisible = false
        studyProtocolURITextField.isVisible = false

        studyProtocolVersionLabel.isVisible = false
        studyProtocolVersionTextField.isVisible = false

        studyProtocolParametersLabel.isVisible = false
        studyProtocolParametersComboBox.isVisible = false

        studyProtocolComponentsLabel.isVisible = false
        studyProtocolComponentsComboBox.isVisible = false

        val pairList = listOf<Pair<JLabel, JComponent>>(
                Pair(first = studyIdentifierLabel, second = studyIdentifierTextField),
                Pair(first = studyTitleLabel, second = studyTitleTextField),
                Pair(first = studyDescriptionLabel, second = studyDescriptionTextArea),
                Pair(first = studyDesignTypeLabel, second = studyDesignTypeComboBox),
                Pair(first = studyAssayMeasurementsLabel, second = studyAssayMeasurementsComboBox),
                Pair(first = studyAssayTechnologyLabel, second = studyAssayTechnologyComboBox),
                Pair(first = accreditationProcedureLabel, second = accreditationProcedureComboBox),
                Pair(first = studyProtocolNameLabel, second = studyProtocolNameTextField),
                Pair(first = studyProtocolTypeLabel, second = studyProtocolTypeComboBox),
                Pair(first = studyProtocolDescriptionLabel, second = studyProtocolDescriptionTextField),
                Pair(first = studyProtocolURILabel, second = studyProtocolURITextField),
                Pair(first = studyProtocolVersionLabel, second = studyProtocolVersionTextField),
                Pair(first = studyProtocolParametersLabel, second = studyProtocolParametersComboBox),
                Pair(first = studyProtocolComponentsLabel, second = studyProtocolComponentsComboBox)
        )

        addGridComponents(pairs = pairList)
    }

    fun toggleAdvancedMode() {

        val newVisibilityStatus = !studyDescriptionTextArea.isVisible

        studyDescriptionLabel.isVisible = newVisibilityStatus
        studyDescriptionTextArea.isVisible = newVisibilityStatus

        studyDesignTypeLabel.isVisible = newVisibilityStatus
        studyDesignTypeComboBox.isVisible = newVisibilityStatus

        studyAssayMeasurementsLabel.isVisible = newVisibilityStatus
        studyAssayMeasurementsComboBox.isVisible = newVisibilityStatus

        studyAssayTechnologyLabel.isVisible = newVisibilityStatus
        studyAssayTechnologyComboBox.isVisible = newVisibilityStatus

        accreditationProcedureLabel.isVisible = newVisibilityStatus
        accreditationProcedureComboBox.isVisible = newVisibilityStatus

        studyProtocolNameLabel.isVisible = newVisibilityStatus
        studyProtocolNameTextField.isVisible = newVisibilityStatus

        studyProtocolTypeLabel.isVisible = newVisibilityStatus
        studyProtocolTypeComboBox.isVisible = newVisibilityStatus

        studyProtocolDescriptionLabel.isVisible = newVisibilityStatus
        studyProtocolDescriptionTextField.isVisible = newVisibilityStatus

        studyProtocolURILabel.isVisible = newVisibilityStatus
        studyProtocolURITextField.isVisible = newVisibilityStatus

        studyProtocolVersionLabel.isVisible = newVisibilityStatus
        studyProtocolVersionTextField.isVisible = newVisibilityStatus

        studyProtocolParametersLabel.isVisible = newVisibilityStatus
        studyProtocolParametersComboBox.isVisible = newVisibilityStatus

        studyProtocolComponentsLabel.isVisible = newVisibilityStatus
        studyProtocolComponentsComboBox.isVisible = newVisibilityStatus
    }
}

class EditStudySamplePanel(studySample: StudySample? = null, isAdvanced: Boolean) : JPanel(GridBagLayout()) {

    companion object {
        val sampleName = "Sample name (ID)"
        val sampleNameTooltip = "An unambiguous ID given to the samples used in the assay"

        val sampleProtocol = "Protocol of sample"
        val sampleProtocolTooltip = """
            |<html>
            |<p>Additional protocol for sample and sample collection. Corresponds to the
            |<p>Protocol REF in ISA
            |</html>
            """.trimMargin()

        val samplingStrategy = "Sampling strategy"
        val samplingStrategyTooltip = """
            |<html>
            |<p>Sampling strategy (ref. EUROSTAT - Typology of sampling strategy,
            |<p>version of July 2009)
            |</html>
            """.trimMargin()

        val samplingType = "Type of sampling"
        val samplingTypeTooltip = """
            |<html>
            |<p>Indicate the type programme for which the samples have been collected"
            |</html>
            """.trimMargin()

        val samplingMethod = "Sampling method"
        val samplingMethodTooltip = "Sampling method used to take the sample"

        val samplingPlan = "Sampling plan"
        val samplingPlanTooltip = """
            |<html>
            |<p>assayDescription of data collection technique: stratified or complex sampling
            |<p>(several stages)
            |</html>
            """.trimMargin()

        val samplingWeight = "Sampling weight"
        val samplingWeightTooltip = """
            |<html>
            |<p>assayDescription of the method employed to compute sampling weight
            |<p>(nonresponse-adjusted weight)
            |</html>
            """.trimMargin()

        val samplingSize = "Sampling size"
        val samplingSizeTooltip = """
            |<html>
            |<p>number of units, full participants, partial participants, eligibles, not
            |<p>eligible, unresolved (eligibility status not resolved)…
            |</html>
            """.trimMargin()

        val lotSizeUnit = "Lot size unit"
        val lotSizeUnitTooltip = "Unit in which the lot size is expressed."

        val samplingPoint = "Sampling point"
        val samplingPointTooltip = """
            |<html>
            |<p>Point in the food chain where the sample was taken.
            |<p>(Doc. ESTAT/F5/ES/155 "Data dictionary of activities of the
            |<p>establishments").
            |</html>
            """.trimMargin()
    }

    // Fields. null if advanced mode
    val sampleNameTextField = JTextField(30)
    val sampleProtocolTextField = JTextField(30)
    val samplingStrategyComboBox = if (isAdvanced) JComboBox<String>() else null
    val samplingTypeComboBox = if (isAdvanced) JComboBox<String>() else null
    val samplingMethodComboBox = if (isAdvanced) JComboBox<String>() else null
    val samplingPlanTextField = JTextField(30)
    val samplingWeightTextField = JTextField(30)
    val samplingSizeTextField = JTextField(30)
    val lotSizeUnitComboBox = if (isAdvanced) JComboBox<String>() else null
    val samplingPointComboBox = if (isAdvanced) JComboBox<String>() else null

    init {

        // Populate UI with passed `studySample`
        studySample?.let {
            sampleNameTextField.text = it.sample
            sampleProtocolTextField.text = it.collectionProtocol
            samplingStrategyComboBox?.selectedItem = it.samplingStrategy
            samplingTypeComboBox?.selectedItem = it.samplingProgramType
            samplingMethodComboBox?.selectedItem = it.samplingMethod
            samplingPlanTextField.text = it.samplingPlan
            samplingWeightTextField.text = it.samplingWeight
            samplingSizeTextField.text = it.samplingSize
            lotSizeUnitComboBox?.selectedItem = it.lotSizeUnit
            samplingPointComboBox?.selectedItem = it.samplingPoint
        }

        initUI()
    }

    private fun initUI() {

        val sampleNameLabel = createLabel(text = sampleName, tooltip = sampleNameTooltip)
        val sampleProtocolLabel = createLabel(text = sampleProtocol, tooltip = sampleProtocolTooltip)
        val samplingStrategyLabel = createLabel(text = samplingStrategy, tooltip = samplingStrategyTooltip)
        val samplingTypeLabel = createLabel(text = samplingType, tooltip = samplingStrategyTooltip)
        val samplingMethodLabel = createLabel(text = samplingMethod, tooltip = samplingMethodTooltip)
        val samplingPlanLabel = createLabel(text = samplingPlan, tooltip = samplingPlanTooltip)
        val samplingWeightLabel = createLabel(text = samplingWeight, tooltip = samplingWeightTooltip)
        val samplingSizeLabel = createLabel(text = samplingSize, tooltip = samplingSizeTooltip)
        val lotSizeUnitLabel = createLabel(text = lotSizeUnit, tooltip = lotSizeUnitTooltip)
        val samplingPointLabel = createLabel(text = samplingPoint, tooltip = samplingPointTooltip)

        val pairList = mutableListOf<Pair<JLabel, JComponent>>()
        pairList.add(Pair(first = sampleNameLabel, second = sampleNameTextField))
        pairList.add(Pair(first = sampleProtocolLabel, second = sampleProtocolTextField))
        samplingStrategyComboBox?.let { pairList.add(Pair(first = samplingStrategyLabel, second = it)) }
        samplingTypeComboBox?.let { pairList.add(Pair(first = samplingTypeLabel, second = it)) }
        samplingMethodComboBox?.let { pairList.add(Pair(first = samplingMethodLabel, second = it)) }
        pairList.add(Pair(first = samplingPlanLabel, second = samplingPlanTextField))
        pairList.add(Pair(first = samplingWeightLabel, second = samplingWeightTextField))
        pairList.add(Pair(first = samplingSizeLabel, second = samplingSizeTextField))
        lotSizeUnitComboBox?.let { pairList.add(Pair(first = lotSizeUnitLabel, second = it)) }
        samplingPointComboBox?.let { pairList.add(Pair(first = samplingPointLabel, second = it)) }

        addGridComponents(pairs = pairList)
    }

    fun toStudySample(): StudySample {
        /*
         TODO: mandatory fields need to be validated
         Mandatory fields are casted to empty strings TEMPORARILY.
          */

        val sampleName = sampleNameTextField.text ?: ""
        val collectionProtocol = sampleProtocolTextField.text ?: ""
        val samplingPlan = samplingPlanTextField.text ?: ""
        val samplingWeight = samplingWeightTextField.text ?: ""
        val samplingSize = samplingSizeTextField.text ?: ""

        val studySample = StudySample(sample = sampleName, collectionProtocol = collectionProtocol,
                samplingPlan = samplingPlan, samplingWeight = samplingWeight, samplingSize = samplingSize)

        studySample.samplingStrategy = samplingStrategyComboBox?.selectedItem as String?
        studySample.samplingMethod = samplingMethodComboBox?.selectedItem as String?
        studySample.lotSizeUnit = lotSizeUnitComboBox?.selectedItem as String?
        studySample.samplingPoint = samplingPointComboBox?.selectedItem as String?

        return studySample
    }
}

class EditDietaryAssessmentMethodPanel(dietaryAssessmentMethod: DietaryAssessmentMethod? = null, isAdvanced: Boolean)
    : JPanel(GridBagLayout()) {

    companion object {
        val dataCollectionTool = "Methodological tool to collect data"
        val dataCollectionToolTooltip = """
            |<html>
            |<p>food diaries, interview, 24-hour recall interview, food propensy
            |<p>questionnaire, portion size measurement aids, eating outside
            |<p>questionnaire
            """.trimMargin()

        val nonConsecutiveOneDays = "Number of non-conseccutive one-day"
        val nonConsecutiveOneDayTooltip = ""

        val dietarySoftwareTool = "Dietary software tool"
        val dietarySoftwareTooltip = ""

        val foodItemNumber = "Number of food items"
        val foodItemNumberTooltip = ""

        val recordType = "Type of records"
        val recordTypeTooltip = """
            |<html>
            |<p>consumption occasion, mean of consumption, quantified and described as
            |<p>eaten, recipes for self-made
            |</html>
            """.trimMargin()

        val foodDescription = "Food assayDescription"
        val foodDescriptionTooltip = "use foodex2 facets"
    }

    // fields. null if advanced
    val dataCollectionToolComboBox = JComboBox<String>()
    val nonConsecutiveOneDayTextField = JTextField(30)
    val dietarySoftwareToolTextField = if (isAdvanced) JTextField(30) else null
    val foodItemNumberTextField = if (isAdvanced) JTextField(30) else null
    val recordTypeTextField = if (isAdvanced) JTextField(30) else null
    val foodDescriptorTextField = if (isAdvanced) JTextField(30) else null

    init {

        // Populate interface with passed `dietaryAssessmentMethod`
        dietaryAssessmentMethod?.let {
            dataCollectionToolComboBox.selectedItem = it.collectionTool
            nonConsecutiveOneDayTextField.text = it.numberOfNonConsecutiveOneDay.toString()
            dietarySoftwareToolTextField?.text = it.softwareTool
            foodItemNumberTextField?.text = it.numberOfFoodItems[0]
            recordTypeTextField?.text = it.recordTypes[0]
            foodDescriptorTextField?.text = it.foodDescriptors[0]
        }

        initUI()
    }

    private fun initUI() {
        val dataCollectionToolLabel = createLabel(text = dataCollectionTool, tooltip = dataCollectionToolTooltip)
        val nonConsecutiveOneDayLabel = createLabel(text = nonConsecutiveOneDays, tooltip = nonConsecutiveOneDayTooltip)
        val dietarySoftwareToolLabel = createLabel(text = dietarySoftwareTool, tooltip = dietarySoftwareTooltip)
        val foodItemNumberLabel = createLabel(text = foodItemNumber, tooltip = foodItemNumberTooltip)
        val recordTypeLabel = createLabel(text = recordType, tooltip = recordTypeTooltip)
        val foodDescriptionLabel = createLabel(text = foodDescription, tooltip = foodDescriptionTooltip)

        val pairList = mutableListOf<Pair<JLabel, JComponent>>()
        pairList.add(Pair(first = dataCollectionToolLabel, second = dataCollectionToolComboBox))
        pairList.add(Pair(first = nonConsecutiveOneDayLabel, second = nonConsecutiveOneDayTextField))
        dietarySoftwareToolTextField?.let { pairList.add(Pair(first = dietarySoftwareToolLabel, second = it)) }
        foodItemNumberTextField?.let { pairList.add(Pair(first = foodItemNumberLabel, second = it)) }
        recordTypeTextField?.let { pairList.add(Pair(first = recordTypeLabel, second = it)) }
        foodDescriptorTextField?.let { pairList.add(Pair(first = foodDescriptionLabel, second = it)) }

        addGridComponents(pairs = pairList)
    }

    fun toDietaryAssessmentMethod(): DietaryAssessmentMethod {

        // TODO: cast temporarily null values to empty string and 0 (SHOULD be validated)
        val dataCollectionTool = if (dataCollectionToolComboBox.selectedItem == null) "" else dataCollectionToolComboBox.selectedItem as String
        val nonConsecutiveOneDays = nonConsecutiveOneDayTextField.text?.toIntOrNull() ?: 0

        val method = DietaryAssessmentMethod(collectionTool = dataCollectionTool, numberOfNonConsecutiveOneDay = nonConsecutiveOneDays)
        method.softwareTool = dietarySoftwareToolTextField?.text
        foodItemNumberTextField?.text?.let { method.numberOfFoodItems.add(it) }
        recordTypeTextField?.text?.let { method.recordTypes.add(it) }
        foodDescriptorTextField?.text?.let { method.foodDescriptors.add(it) }

        return method
    }
}

class EditAssayPanel(assay: Assay? = null, isAdvanced: Boolean) : JPanel(GridBagLayout()) {

    companion object {
        val assayName = "Name"
        val assayNameTooltip = "A name given to the assay"

        val assayDescription = "Description"
        val assayDescriptionTooltip = "General assayDescription of the assay. Corresponds to the Protocol REF in ISA"
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
}

class ModelMathPanel(modelMath: ModelMath? = null) : Box(BoxLayout.PAGE_AXIS) {

    companion object {

        val parameters = "Parameters"
        val qualityMeasures = "Quality measures"
        val modelEquation = "Model equation"
        val fittingProcedure = "Fitting procedure"
    }

    val advancedCheckBox = JCheckBox("Advanced")

    init {

        val parametersPanel = ParameterPanel(isAdvanced = advancedCheckBox.isSelected)

        val qualityMeasuresPanel = QualityMeasuresPanel()

        val modelEquationPanel = ModelEquationsPanel(isAdvanced = advancedCheckBox.isSelected)

//        val fittingProcedure = JPanel()
//        fittingProcedure.border = BorderFactory.createTitledBorder(fittingProcedure)

        val propertiesPanel = JPanel(GridBagLayout())
        propertiesPanel.add(comp = parametersPanel, gridy = 0, gridx = 0)
        propertiesPanel.add(comp = qualityMeasuresPanel, gridy = 1, gridx = 0)
        propertiesPanel.add(comp = modelEquationPanel, gridy = 2, gridx = 0)

        add(createAdvancedPanel(checkbox = advancedCheckBox))
        add(Box.createGlue())
        add(propertiesPanel)
    }
}

// TODO: idTextField <- Create UUID automatically
class EditParameterPanel(parameter: Parameter? = null, isAdvanced: Boolean) : JPanel(GridBagLayout()) {

    companion object {

        val id = "Parameter ID"
        val idTooltip = "An unambiguous and sequential ID given to the parameter"

        val classification = "Parameter classification"
        val classificationTooltip = "General classification of the parameter (e.g. Input, Constant, Output...)"

        val parameterName = "Parameter name"
        val parameterNameTooltip = "A name given to the parameter"

        val description = "Parameter classification"
        val descriptionTooltip = "General description of the parameter"

        val type = "Parameter type"
        val typeTooltip = "The type of the parameter"

        val unit = "Parameter unit"
        val unitTooltip = "Unit of the parameter"

        val unitCategory = "Parameter unit category"
        val unitCategoryTooltip = "General classification of the parameter unit"

        val dataType = "Parameter data type"
        val dataTypeTooltip = """
            |<html>
            |<p>Information on the data format of the parameter, e.g. if it is a
            |<p>categorical variable, int, double, array of size x,y,z
            |</html>
            """.trimMargin()

        val source = "Parameter source"
        val sourceTooltip = "Information on the type of knowledge used to define the parameter value"

        val subject = "Parameter subject"
        val subjectTooltip = """
            |<html>
            |<p>Scope of the parameter, e.g. if it refers to an animal, a batch of
            |<p>animals, a batch of products, a carcass, a carcass skin etc
            |</html>
            """.trimMargin()

        val distribution = "Parameter distribution"
        val distributionTooltip = """
            |<html>
            |<p>Information on the expected distribution of parameter values in of
            |<p>uncertainty and variability - if available. SUGGESTION: Information on
            |<p>the distribution describing the parameter (e.g variability, uncertainty,
            |<p>point estimate...)
            |</html>
            """.trimMargin()

        val value = "Parameter value"
        val valueTooltip = "Numerical value of the parameter"

        val reference = "Parameter reference"
        val referenceTooltip = """
            |<html>
            |<p>Information on the source, where the value of the parameter has been
            |<p>extracted from - if available. The format should use that used in other
            |<p>"Reference" metadata"
            |</html>
            """.trimMargin()

        val variabilitySubject = """
            |<html>
            |<p>Parameter variability
            |<p>subject
            |</html>
            """.trimMargin()
        val variabilitySubjectTooltip = """
            |<html>
            |<p>Information "per what" the variability is described. It can be
            |<p>variability between broiler in a flock,  variability between all meat
            |<p>packages sold in Denmark, variability between days, etc.
            |</html>
            """.trimMargin()

        val applicability = """
            |<html>
            |<p>Range of applicability
            |<p>of the model
            |</html>
            """.trimMargin()

        val applicabilityTooltip = """
            |<html>
            |<p>Numerical values of the maximum and minimum limits of the parameter that
            |<p>determine the range of applicability for which the model applies
            |</html>
            """.trimMargin()

        val error = "Parameter error"
        val errorTooltip = "Error of the parameter value"
    }

    val idLabel = createLabel(text = id, tooltip = idTooltip)
    val idTextField = JTextField(30)

    // TODO: classificationComboBox is a ComboBox and in the GUI appears a Text entry instead
    val classificationLabel = createLabel(text = classification, tooltip = classificationTooltip)
    val classificationComboBox = JComboBox<String>()

    val nameLabel = createLabel(text = parameterName, tooltip = parameterNameTooltip)
    val nameTextField = JTextField(30)

    val descriptionLabel = createLabel(text = description, tooltip = descriptionTooltip)
    val descriptionTextArea = JTextArea(5, 30)

    val typeLabel = createLabel(text = type, tooltip = typeTooltip)
    val typeComboBox = JComboBox<String>()

    val unitLabel = createLabel(text = unit, tooltip = unitTooltip)
    val unitComboBox = JComboBox<String>()

    val unitCategoryLabel = createLabel(text = unitCategory, tooltip = unitCategoryTooltip)
    val unitCategoryComboBox = JComboBox<String>()

    val dataTypeLabel = createLabel(text = dataType, tooltip = dataTypeTooltip)
    val dataTypeComboBox = JComboBox<String>()

    val sourceLabel = createLabel(text = source, tooltip = sourceTooltip)
    val sourceComboBox = JComboBox<String>()

    val subjectLabel = createLabel(text = subject, tooltip = subjectTooltip)
    val subjectComboBox = JComboBox<String>()

    val distributionLabel = createLabel(text = distribution, tooltip = distributionTooltip)
    val distributionComboBox = JComboBox<String>()

    val valueLabel = createLabel(text = value, tooltip = valueTooltip)
    val valueTextField = JTextField(30)

    val referenceLabel = createLabel(text = reference, tooltip = referenceTooltip)
    val referenceTextField = JTextField(30)

    val variabilitySubjectLabel = createLabel(text = variabilitySubject, tooltip = variabilitySubjectTooltip)
    val variabilitySubjectTextArea = JTextArea(5, 30)

    val applicabilityLabel = createLabel(text = applicability, tooltip = applicabilityTooltip)
    val applicabilityTextArea = JTextArea(5, 30)

    val errorLabel = createLabel(text = error, tooltip = errorTooltip)
    val errorSpinnerModel = createSpinnerDoubleModel()

    init {

        val pairList = listOf<Pair<JLabel, JComponent>>(
                Pair(first = idLabel, second = idTextField),
                Pair(first = classificationLabel, second = classificationComboBox),
                Pair(first = nameLabel, second = nameTextField),
                Pair(first = descriptionLabel, second = descriptionTextArea),
                Pair(first = typeLabel, second = typeComboBox),
                Pair(first = unitLabel, second = unitComboBox),
                Pair(first = unitCategoryLabel, second = unitCategoryComboBox),
                Pair(first = dataTypeLabel, second = dataTypeComboBox),
                Pair(first = sourceLabel, second = sourceComboBox),
                Pair(first = subjectLabel, second = subjectComboBox),
                Pair(first = distributionLabel, second = distributionComboBox),
                Pair(first = valueLabel, second = valueTextField),
                Pair(first = referenceLabel, second = referenceTextField),
                Pair(first = variabilitySubjectLabel, second = variabilitySubjectTextArea),
                Pair(first = applicabilityLabel, second = applicabilityTextArea),
                Pair(first = errorLabel, second = createSpinner(errorSpinnerModel))
        )

        addGridComponents(pairs = pairList)
    }

    // TODO: toParameter
}

class ParameterPanel(val parameters: MutableList<Parameter> = mutableListOf(), isAdvanced: Boolean) : JPanel(BorderLayout()) {

    init {
        border = BorderFactory.createTitledBorder(ModelMathPanel.parameters)

        val dtm = NonEditableTableModel()
        parameters.forEach { dtm.addRow(arrayOf(it)) }

        val renderer = object : DefaultTableCellRenderer() {
            override fun setValue(value: Any?) {
                text = (value as Parameter?)?.id
            }
        }
        val myTable = HeadlessTable(model = dtm, renderer = renderer)

        // buttons
        val buttonsPanel = ButtonsPanel()
        buttonsPanel.addButton.addActionListener { _ ->
            val editPanel = EditParameterPanel(isAdvanced = isAdvanced)
            val result = showConfirmDialog(panel = editPanel, title = "Create parameter")
            if (result == JOptionPane.OK_OPTION) {
                // FIXME: Uncomment once EditParameterPanel.toParameter is implemented
//                dtm.addRow(arrayOf(editParameterPanel.toParameter()))
            }
        }

        buttonsPanel.modifyButton.addActionListener { _ ->
            println("dummy listener")
        }

        buttonsPanel.removeButton.addActionListener { _ ->
            println("dummy listener")
        }

        add(myTable, BorderLayout.NORTH)
        add(buttonsPanel, BorderLayout.SOUTH)
    }
}

class QualityMeasuresPanel(sse: Double? = null, mse: Double? = null, rmse: Double? = null,
                           r2: Double? = null, aic: Double? = null, bic: Double? = null) : JPanel(GridBagLayout()) {

    val sseSpinnerModel = createSpinnerDoubleModel()
    val mseSpinnerModel = createSpinnerDoubleModel()
    val rmseSpinnerModel = createSpinnerDoubleModel()
    val r2SpinnerModel = createSpinnerDoubleModel()
    val aicSpinnerModel = createSpinnerDoubleModel()
    val bicSpinnerModel = createSpinnerDoubleModel()

    init {
        val pairList = listOf<Pair<JLabel, JComponent>>(
                Pair(first = JLabel("SSE"), second = createSpinner(sseSpinnerModel)),
                Pair(first = JLabel("MSE"), second = createSpinner(mseSpinnerModel)),
                Pair(first = JLabel("RMSE"), second = createSpinner(rmseSpinnerModel)),
                Pair(first = JLabel("r-Squared"), second = createSpinner(r2SpinnerModel)),
                Pair(first = JLabel("AIC"), second = createSpinner(aicSpinnerModel)),
                Pair(first = JLabel("BIC"), second = createSpinner(bicSpinnerModel))
        )

        addGridComponents(pairs = pairList)

        border = BorderFactory.createTitledBorder(ModelMathPanel.qualityMeasures)
    }

    // TODO: toQualityMeasures
}

class ModelEquationsPanel(
        val equations: MutableList<ModelEquation> = mutableListOf(),
        isAdvanced: Boolean
) : JPanel(BorderLayout()) {

    init {
        border = BorderFactory.createTitledBorder(ModelMathPanel.modelEquation)

        val dtm = NonEditableTableModel()
        equations.forEach { dtm.addRow(arrayOf(it)) }

        val renderer = object : DefaultTableCellRenderer() {
            override fun setValue(value: Any?) {
                text = (value as ModelEquation?)?.equationName
            }
        }
        val myTable = HeadlessTable(model = dtm, renderer = renderer)

        val buttonsPanel = ButtonsPanel()
        buttonsPanel.addButton.addActionListener { _ ->
            val editPanel = EditModelEquationPanel(isAdvanced = isAdvanced)
            val result = showConfirmDialog(panel = editPanel, title = "Create equation")
            if (result == JOptionPane.OK_OPTION) {
                // TODO: process result
            }
        }

        buttonsPanel.modifyButton.addActionListener { _ ->
            val rowToEdit = myTable.selectedRow
            if (rowToEdit != -1) {
                val equation = dtm.getValueAt(rowToEdit, 0) as ModelEquation

                val editPanel = EditModelEquationPanel(equation = equation, isAdvanced = isAdvanced)
                val result = showConfirmDialog(panel = editPanel, title = "Modify equation")
                if (result == JOptionPane.OK_OPTION) {
                    // TODO: process result
                }
            }
        }

        buttonsPanel.removeButton.addActionListener { _ ->
            val rowToDelete = myTable.selectedRow
            if (rowToDelete != -1) dtm.removeRow(rowToDelete)
        }

        add(myTable, BorderLayout.NORTH)
        add(buttonsPanel, BorderLayout.SOUTH)
    }
}

class EditModelEquationPanel(equation: ModelEquation? = null, isAdvanced: Boolean) : JPanel(GridBagLayout()) {

    companion object {

        val equationName = "Model equation name"
        val equationNameTooltip = "A name given to the model equation"

        val equationClass = "Model equation class"
        val equationClassTooltip = "Information on that helps to categorize model equations"

        val script = "Equation"
        val scriptToolTip = "The pointer to the file that holds the software code (e.g. R-script)"
    }

    val equationNameLabel = createLabel(text = equationName, tooltip = equationNameTooltip)
    val equationNameTextField = JTextField(30)

    val equationClassLabel = createLabel(text = equationClass, tooltip = equationClassTooltip)
    val equationClassTextField = JTextField(30)

    val scriptLabel = createLabel(text = script, tooltip = scriptToolTip)
    val scriptTextArea = JTextArea(5, 30)

    init {

        val referencePanel = ReferencePanel(refs = equation?.equationReference ?: mutableListOf(), isAdvanced = isAdvanced)

        add(comp = equationNameLabel, gridy = 0, gridx = 0)
        add(comp = equationNameTextField, gridy = 0, gridx = 1)

        add(comp = equationClassLabel, gridy = 1, gridx = 0)
        add(comp = equationClassTextField, gridy = 1, gridx = 1)

        add(comp = referencePanel, gridy = 2, gridx = 0, gridwidth = 2)

        add(comp = scriptLabel, gridy = 3, gridx = 0)
        add(comp = scriptTextArea, gridy = 3, gridx = 1, gridwidth = 2)
    }
}

private fun createLabel(text: String, tooltip: String): JLabel {
    val label = JLabel(text)
    label.toolTipText = tooltip

    return label
}

private fun createAdvancedPanel(checkbox: JCheckBox): JPanel {
    val panel = JPanel()
    panel.background = Color.lightGray
    panel.add(checkbox)

    return panel
}

/** Creates a JSpinner with 5 columns. */
private fun createSpinner(spinnerModel: AbstractSpinnerModel): JSpinner {
    val spinner = JSpinner(spinnerModel)
    (spinner.editor as JSpinner.DefaultEditor).textField.columns = 5

    return spinner
}

/** Creates a SpinnerNumberModel for integers with no limits and initial value 0. */
private fun createSpinnerIntegerModel() = SpinnerNumberModel(0, null, null, 1)

/** Creates a SpinnerNumberModel for real numbers with no limits and initial value 0.0. */
private fun createSpinnerDoubleModel() = SpinnerNumberModel(0.0, null, null, .01)

/**
 * Creates a SpinnerNumberModel for percentages (doubles) and initial value 0.0.
 *
 * Has limits 0.0 and 1.0.
 * */
private fun createSpinnerPercentageModel() = SpinnerNumberModel(0.0, 0.0, 1.0, .01)

private class NonEditableTableModel : DefaultTableModel(arrayOf(), arrayOf("header")) {
    override fun isCellEditable(row: Int, column: Int) = false
}

private class HeadlessTable(model: NonEditableTableModel, val renderer: DefaultTableCellRenderer) : JTable(model) {

    init {
        tableHeader = null  // Hide header
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    }

    override fun getCellRenderer(row: Int, column: Int) = renderer
}

private class ButtonsPanel : JPanel() {

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
private fun showConfirmDialog(panel: JPanel, title: String): Int {
    return JOptionPane.showConfirmDialog(null, panel, title, JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE)
}