package BikeRack

import kotlinx.serialization.Serializable

@Serializable
data class bikeTRX(
    val id: Int, val desc: String, val totalCost: Int, val date: String) {
}