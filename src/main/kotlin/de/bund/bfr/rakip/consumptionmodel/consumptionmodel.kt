package de.bund.bfr.rakip.consumptionmodel

import de.bund.bfr.rakip.generic.*

data class ConsumptionModel(
        var generalInformation: GeneralInformation,
        var scope: Scope,
        var dataBackground: DataBackground? = null,
        var modelMath: ModelMath,
        var simulation: Simulation? = null
)

data class Scope(
        var product: Product,
        var populationGroup: PopulationGroup,
        var generalComment: String? = null,
        var temporalInformation: String? = null,
        val region: MutableList<String> = mutableListOf(),
        val country: MutableList<String> = mutableListOf()
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