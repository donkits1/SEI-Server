package Models

import kotlinx.serialization.Serializable

@Serializable
data class Client(
    val id: Int, val name: String, val pin: Int, val checkin: Boolean) {
}