package BikeRack

import kotlinx.serialization.Serializable

@Serializable
data class BikeCustomer(
    val userid: Int = 0, val name: String = "") {
}