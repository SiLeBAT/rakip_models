package de.bund.bfr.rakip.processmodel

import de.bund.bfr.rakip.generic.*

data class Scope(
        var product: Product? = null,
        var hazard: Hazard? = null,
        var populationGroup: PopulationGroup? = null,
        var generalComment: String? = null,
        var temporalInformation: String? = null,
        val region: MutableList<String> = mutableListOf(),
        val country: MutableList<String> = mutableListOf()
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

data class PredictiveModel(
        var generalInformation: de.bund.bfr.rakip.generic.GeneralInformation,
        var scope: Scope,
        var dataBackground: DataBackground? = null,
        var modelMath: ModelMath? = null,
        var simulation: de.bund.bfr.rakip.generic.Simulation? = null
)