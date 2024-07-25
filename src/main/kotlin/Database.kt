import java.sql.Connection
import java.sql.DriverManager

object Database {
    fun connect(): Connection {
        val url = "jdbc:postgresql://localhost:5440/donne.berberabe"
        val user = "postgres"
        val password = "postgres"
        return DriverManager.getConnection(url, user, password)
    }
}
