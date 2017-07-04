package de.bund.bfr.rakip.drm

import com.gmail.gcolaianni5.jris.bean.Record
import de.bund.bfr.rakip.generic.*
import ezvcard.VCard
import java.net.URL
import java.util.*

data class DoseResponseModel(
        var generalInformation: GeneralInformation,
        var scope: Scope,
        var dataBackground: DataBackground? = null,
        var modelMath: ModelMath,
        var simulation: Simulation? = null
)

data class GeneralInformation(
        var name: String,
        var source: String? = null,
        var identifier: String,
        val creators: MutableList<VCard> = mutableListOf(),
        var creationDate: Date,
        val modificationDate: MutableList<Date> = mutableListOf(),
        var rights: String,
        var isAvailable: Boolean,
        var url: URL? = null,
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

data class Scope(
        var product: Product,
        var populationGroup: PopulationGroup,
        var generalComment: String? = null,
        var temporalInformation: String? = null,
        val region: MutableList<String> = mutableListOf(),
        val country: MutableList<String> = mutableListOf()
)

data class DataBackground(
        var study: Study,
        var studySample: StudySample? = null,
        var dietaryAssessmentMethod: DietaryAssessmentMethod? = null,
        val laboratoryAccreditation: MutableList<String> = mutableListOf(),
        var assay: Assay? = null
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
        val event: MutableList<String> = mutableListOf()
)