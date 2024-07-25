package BikeRack

import kotlinx.serialization.Serializable

@Serializable
data class Bike(
    val bikeName: String, val cost: Int) {
}