package de.bund.bfr.rakip.generic

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.gmail.gcolaianni5.jris.bean.Record
import com.gmail.gcolaianni5.jris.engine.JRis
import ezvcard.Ezvcard
import ezvcard.VCard
import ezvcard.property.StructuredName
import java.io.StringWriter
import java.net.URI
import java.net.URL
import java.util.*


/**
 * @property modelClass Type of model. Ultimate goal of the global model.
 * @property modelSubClass Sub-classification of the model given modelClass
 * @property basicProcess Impact of the specific process in the hazard
 */
data class ModelCategory(
        var modelClass: String,
        val modelSubClass: MutableList<String> = mutableListOf(),
        var modelClassComment: String? = null,
        val modelSubSubClass: MutableList<String> = mutableListOf(),
        val basicProcess: MutableList<String> = mutableListOf()
)

/**
 * @property name Name given to the model or data
 * @property source Related resource from which the resource is derived
 * @property identifier Unambiguous ID given to the model or data
 * @property creationDate Model creation date
 * @property rights Rights held in over the resource
 * @property isAvailable Availability of data or model
 * @property url Web address referencing the resource location
 * @property format Form of data (file extension)
 * @property language Language of the resource
 * @property software Program in which the model has been implemented
 * @property languageWrittenIn  Language used to write the model
 * @property status Curation status of the model
 * @property objective Objective of the model or data
 * @property description General assayDescription of the study, data or model
 */
data class GeneralInformation(
        var name: String,
        var source: String? = null,
        var identifier: String,
        val creators: MutableList<VCard> = mutableListOf(),
        var creationDate: Date,
        val modificationDate: MutableList<Date> = mutableListOf(),
        var rights: String,
        var isAvailable: Boolean,
        var url: URL,
        var format: String? = null,
        val reference: MutableList<Record> = mutableListOf(),
        var language: String? = null,
        var software: String? = null,
        var languageWrittenIn: String? = null,
        var modelCategory: ModelCategory? = null,
        var status: String? = null,
        var objective: String? = null,
        var description: String? = null
)

data class Product(
        var environmentName: String,
        var environmentDescription: String? = null,
        var environmentUnit: String,
        val productionMethod: MutableList<String> = mutableListOf(),
        val packaging: MutableList<String> = mutableListOf(),
        val productTreatment: MutableList<String> = mutableListOf(),
        var originCountry: String? = null,
        var areaOfOrigin: String? = null,
        var fisheriesArea: String? = null,
        var productionDate: Date? = null,
        var expirationDate: Date? = null
)

data class Hazard(
        var hazardType: String,
        var hazardName: String,
        var hazardDescription: String? = null,
        var hazardUnit: String,
        var adverseEffect: String? = null,
        var origin: String? = null,
        var benchmarkDose: String? = null,
        var maximumResidueLimit: String? = null,
        var noObservedAdverse: String? = null,
        var lowestObservedAdverse: String? = null,
        var acceptableOperator: String? = null,
        var acuteReferenceDose: String? = null,
        var acceptableDailyIntake: String? = null,
        var hazardIndSum: String? = null,
        var laboratoryName: String? = null,
        var laboratoryCountry: String? = null,
        var detectionLimit: String? = null,
        var quantificationLimit: String? = null,
        var leftCensoredData: String? = null,
        var rangeOfContamination: String? = null
)

/**
 * @property populationName Population name
 * @property targetPopulation Population of interest
 * TODO:
 */
data class PopulationGroup(
        var populationName: String,
        var targetPopulation: String? = null,
        val populationSpan: MutableList<String> = mutableListOf(),
        val populationDescription: MutableList<String> = mutableListOf(),
        val populationAge: MutableList<String> = mutableListOf(),
        var populationGender: String? = null,
        val bmi: MutableList<String> = mutableListOf(),
        val specialDietGroups: MutableList<String> = mutableListOf(),
        val patternConsumption: MutableList<String> = mutableListOf(),
        val region: MutableList<String> = mutableListOf(),
        val country: MutableList<String> = mutableListOf(),
        val populationRiskFactor: MutableList<String> = mutableListOf(),
        val season: MutableList<String> = mutableListOf()
)

/**
 * @property generalComment General comment on the model
 * @property temporalInformation Temporal information on which the model applies
 * @property region information on which the model applies
 * @property country countries on which the model applies
 */
data class Scope(
        var product: Product? = null,
        var hazard: Hazard? = null,
        var populationGroup: PopulationGroup? = null,
        var generalComment: String? = null,
        var temporalInformation: String? = null,
        val region: MutableList<String> = mutableListOf(),
        val country: MutableList<String> = mutableListOf()
)

/**
 * @property title Study title
 * @property description Study assayDescription
 * @property designType Study type
 * @property measurementType Observed measure in the assay
 * @property technologyType Employed technology to observe this measurement
 * @property technologyPlatform Used technology platform
 * @property accreditationProcedure Used accreditation procedure
 * @property protocolDescription Type of the protocol (e.g. Extraction protocol)
 * @property parametersName Parameters used when executing this protocol
 */
data class Study(
        var title: String,
        var description: String? = null,
        var designType: String? = null,
        var measurementType: String? = null,
        var technologyType: String? = null,
        var technologyPlatform: String? = null,
        var accreditationProcedure: String? = null,
        var protocolName: String? = null,
        var protocolType: String? = null,
        var protocolDescription: String? = null,
        var protocolURI: URI? = null,
        var protocolVersion: String? = null,
        var parametersName: String? = null,
        var componentsName: String? = null,
        var componentsType: String? = null
)

data class  StudySample(
        var sample: String,
        var moisturePercentage: Double? = null,
        var fatPercentage: Double? = null,
        var collectionProtocol: String,
        var samplingStrategy: String? = null,
        var samplingProgramType: String? = null,
        var samplingMethod: String? = null,
        var samplingPlan: String,
        var samplingWeight: String,
        var samplingSize: String,
        var lotSizeUnit: String? = null,
        var samplingPoint: String? = null
)

/**
 * @property collectionTool Methodological tool to collect data
 * @property numberOfNonConsecutiveOneDay
 * @property softwareTool Dietary software tool
 * @property numberOfFoodItems
 * @property recordTypes Type of records: consumption occasion, mean of consumption, etc.
 * @propertyf foodDescriptors foodex2 facets
 */
data class DietaryAssessmentMethod(
        var collectionTool: String,
        var numberOfNonConsecutiveOneDay: Int,
        var softwareTool: String? = null,
        val numberOfFoodItems: MutableList<String> = mutableListOf(),
        val recordTypes: MutableList<String> = mutableListOf(),
        val foodDescriptors: MutableList<String> = mutableListOf()
)

data class Assay(var name: String, var description: String? = null)

data class DataBackground(
        var study: Study? = null,
        var studySample: StudySample? = null,
        var dietaryAssessmentMethod: DietaryAssessmentMethod? = null,
        val laboratoryAccreditation: MutableList<String> = mutableListOf(),
        var assay: Assay? = null
)

enum class ParameterClassification { input, output, constant }

data class Parameter(
        var id: String,
        var classification: ParameterClassification,
        var name: String,
        var description: String? = null,
        var unit: String,
        var unitCategory: String,
        var dataType: String,
        var source: String? = null,
        var subject: String? = null,
        var distribution: String? = null,
        var value: String? = null,
        var reference: String? = null,
        var variabilitySubject: String? = null,
        val modelApplicability: MutableList<String> = mutableListOf(),
        var error: Double? = null
)

/**
 * @property equationName Model equation name
 * @property equationClass Model equation class
 * @property equationReference Model equation references (RIS)
 * @property equation Model equation or script
 */
data class ModelEquation(
        var equationName: String,
        var equationClass: String? = null,
        val equationReference: MutableList<Record> = mutableListOf(),
        var equation: String
)

data class ModelMath(
        val parameter: MutableList<Parameter> = mutableListOf(),
        var sse: Double? = null,
        var mse: Double? = null,
        var rmse: Double? = null,
        var rSquared: Double? = null,
        var aic: Double? = null,
        var bic: Double? = null,
        var modelEquation: ModelEquation? = null,
        var fittingProcedure: String? = null,
        var exposure: Exposure? = null,
        val event: MutableList<String> = mutableListOf()
)

/**
 * @property treatment Methodological treatment of left-censored data
 * @property contaminationLevel Level of contamination after left-censored data treatment
 * @property exposureType Type of exposure
 * @property scenario Scenario of exposure assessment
 * @property uncertaintyEstimation Analysis to estimate uncertainty
 */
data class Exposure(
        var treatment: String? = null,
        var contaminationLevel: String? = null,
        var exposureType: String? = null,
        var scenario: String? = null,
        var uncertaintyEstimation: String? = null
)

/**
 * @property algorithm Simulation algorithm
 * @property simulatedModel URI of the model applied for simulation/prediction
 * @property parameterSettings
 * @property description General assayDescription of the simulation
 * @property plotSettings Information on the parameters to be plotted
 * @property visualizationScript Pointer to software code (R script)
 */
data class Simulation(
        var algorithm: String,
        var simulatedModel: String,
        val parameterSettings: List<String> = listOf(),
        var description: String? = null,
        val plotSettings: MutableList<String> = mutableListOf(),
        var visualizationScript: String? = null
)

data class GenericModel(
        var generalInformation: GeneralInformation,
        var scope: Scope,
        var dataBackground: DataBackground? = null,
        var modelMath: ModelMath? = null,
        var simulation: Simulation? = null
)

class RisReferenceSerializer : JsonSerializer<Record>() {

    override fun serialize(value: Record, gen: JsonGenerator, provider: SerializerProvider) {

        gen.writeStartObject()

        val stringWriter = StringWriter()
        JRis.build(listOf(value), stringWriter)
        gen.writeStringField("reference", stringWriter.toString())
        stringWriter.close()

        gen.writeEndObject()
    }
}

class RisReferenceDeserializer : JsonDeserializer<Record>() {

    override fun deserialize(parser: JsonParser, context: DeserializationContext): Record {
        val node = parser.readValueAsTree<JsonNode>()
        val referenceString = node.get("reference").asText()
        return referenceString.reader().use { JRis.parse(it)[0] }
    }
}

class VCardSerializer : JsonSerializer<VCard>() {

    override fun serialize(card: VCard, gen: JsonGenerator, serializer: SerializerProvider) {
        gen.writeStartObject()
        gen.writeStringField("creator", card.writeJson())
        gen.writeEndObject()
    }
}

class VCardDeserializer : JsonDeserializer<VCard>() {

    override fun deserialize(parser: JsonParser, context: DeserializationContext): VCard {
        val node = parser.readValueAsTree<JsonNode>()
        val cardString = node.get("creator").asText()
        return Ezvcard.parseJson(cardString).first()
    }
}

class RakipModule : SimpleModule("RakipModule", Version.unknownVersion()) {
    init {
        addSerializer(Record::class.java, RisReferenceSerializer())
        addDeserializer(Record::class.java, RisReferenceDeserializer())

        addSerializer(VCard::class.java, VCardSerializer())
        addDeserializer(VCard::class.java, VCardDeserializer())
    }
}

// To do fun tests
fun main(args: Array<String>) {

    val mapper = jacksonObjectMapper().registerModule(RakipModule())
    mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY)

//    // Define test object 2
//    val aReference = Record()
//    aReference.addAuthor("miguel")
//    aReference.title = "a Kotlin module"
//
//    val modelEquation = ModelEquation(equationName = "a_name", equation = "2 + 2")
//    modelEquation.equationReference.add(aReference)

    // Define test object 3
    val gi = GeneralInformation(creationDate = Date(),
            isAvailable = true,
            identifier = "a_not_so_unique_identifier",
            name="boring_names_are_the_best_ones",
            rights="you_shall_not_pass",
            url=URL("http:/wannacry.com"))

    val vCard = VCard()
    val n = StructuredName()
    n.family = "Doe"
    n.given = "Jonathan"
    n.prefixes.add("Mr")
    vCard.structuredName = n
    vCard.setFormattedName("John Doe")
    gi.creators.add(vCard)

    // Serialization & de-serialization tests
    println("--------------------")
    println("Write to JSON string")
    val jsonString = mapper.writeValueAsString(gi)
    println(jsonString)
    println()

    println("---------------------")
    println("Read from JSON string")
    val readObject = mapper.readValue(jsonString, GeneralInformation::class.java)
    println(readObject)
}