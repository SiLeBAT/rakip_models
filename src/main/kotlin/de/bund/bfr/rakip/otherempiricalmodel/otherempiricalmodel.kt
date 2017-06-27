package de.bund.bfr.rakip.otherempiricalmodel

import de.bund.bfr.rakip.generic.*

data class OtherEmpiricalModel(
        var generalInformation: GeneralInformation,
        var scope: Scope,
        var dataBackground: DataBackground,
        var modelMath: ModelMath,
        var simulation: de.bund.bfr.rakip.generic.Simulation? = null
)

data class DataBackground(
        var study: Study? = null,
        var studySample: StudySample? = null,
        var laboratoryAccreditation: MutableList<String> = mutableListOf(),
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