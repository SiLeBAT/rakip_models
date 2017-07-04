package de.bund.bfr.rakip.toxicological

import de.bund.bfr.rakip.generic.*


data class ToxicologicalModel(
        var generalInformation: GeneralInformation,
        var scope: Scope,
        var dataBackground: DataBackground? = null,
        var modelMath: ModelMath,
        var simulation: Simulation? = null
)

data class Scope(
        var hazard: Hazard,
        var populationGroup: PopulationGroup? = null,
        var generalComment: String? = null,
        var temporalInformation: String? = null,
        val region: MutableList<String> = mutableListOf(),
        val country: MutableList<String> = mutableListOf()
)