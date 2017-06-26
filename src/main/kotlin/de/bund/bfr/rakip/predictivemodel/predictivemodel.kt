package de.bund.bfr.rakip.predictivemodel

import de.bund.bfr.rakip.generic.*
import java.util.*

data class PredictiveModel(
        var generalInformation: de.bund.bfr.rakip.generic.GeneralInformation,
        var scope: Scope,
        var dataBackground: DataBackground? = null,
        var modelMath: ModelMath? = null,
        var simulation: de.bund.bfr.rakip.generic.Simulation? = null
)

data class Scope(
        var product: Product,
        var hazard: de.bund.bfr.rakip.generic.Hazard,
        var generalComment: String? = null,
        val region: MutableList<String> = mutableListOf(),
        val country: MutableList<String> = mutableListOf()
)

data class Product(
        var environmentName: String,
        var environmentDescription: String? = null,
        var environmentUnit: String? = null,
        val productionMethod: MutableList<String> = mutableListOf(),
        val packaging: MutableList<String> = mutableListOf(),
        val productTreatment: MutableList<String> = mutableListOf(),
        var originCountry: String? = null,
        var areaOfOrigin: String? = null,
        var fisheriesArea: String? = null,
        var productionDate: Date? = null,
        var expirationDate: Date? = null
)

data class DataBackground(
        var study: Study? = null,
        var studySample: StudySample? = null,
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