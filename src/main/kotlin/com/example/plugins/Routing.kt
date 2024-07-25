package com.example.plugins


import Models.Client
import com.google.gson.Gson
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

//CRUD Create, Read, Update, Delete
//Customer names should not have numerical characters
//PINs should not have alphabetical characters
//Customer checkin

fun getVal(value: String) : String {
    val ans = value.split("=")

    if(ans.size > 2) {
        throw Exception("INVALID PARAMETERS")
    }
    else {
        return ans[ans.size-1]
    }
}

fun Application.configureRouting() {
    routing {
        get("/") {

            val dataSource = HikariDataSource()
            dataSource.username = "postgres"
            dataSource.password = "postgres"

            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"

            val connection = dataSource.connection
            val query = connection.prepareStatement("SELECT * FROM Clients;")
            val result = query.executeQuery()
            val clients = mutableListOf<Client>()

            while (result.next()) {
                val name = result.getString("name")
                val id = result.getInt("id")
                val pin = result.getInt("pin")
                val checkin = result.getBoolean("checkin")

                clients.add(Client(id, name, pin, checkin))
            }

            val gson = Gson()
            val returnData = gson.toJson(clients)

            call.respondText(returnData)
        }

        get("/verify/{id}/{pin}") {

            val id : String = getVal(call.parameters["id"].toString())
            val pin : String = getVal(call.parameters["pin"].toString())
            val dataSource = HikariDataSource()
            dataSource.username = "postgres"
            dataSource.password = "postgres"
            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"


            val connection = dataSource.connection
            val query = connection.prepareStatement("SELECT * FROM Clients WHERE id = $id AND pin = $pin;")
            val result = query.executeQuery()
            val clients = mutableListOf<Client>()

            while (result.next()) {
                val name = result.getString("name")
                val id2 = result.getInt("id")
                val pin2 = result.getInt("pin")
                val checkIn = result.getBoolean("checkin")

                clients.add(Client(id2, name, pin2, checkIn))
            }
            //clients.add(Client(0, "test", 0, false))

            call.respondText(clients.toString())
        }

        post ("/add-client/{name}/{pin}") {

            val dataSource = HikariDataSource()
            val name : String = getVal(call.parameters["name"].toString())
            val pin : String = getVal(call.parameters["pin"].toString())
//            val name: String? = call.parameters["name"]
//            val pin: Int? = call.parameters["pin"]?.toInt()

            dataSource.username = "postgres"
            dataSource.password = "postgres"
            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"


            val connection = dataSource.connection
            val query = connection.prepareStatement("INSERT INTO Clients (name, pin, checkin) VALUES ('$name', $pin, 0);")
            query.executeUpdate()
            call.response.status(HttpStatusCode.OK)
        }

        delete ("/delete-client/{id}/{pin}") {

            val dataSource = HikariDataSource()
            val id : String = getVal(call.parameters["id"].toString())
            val pin : String = getVal(call.parameters["pin"].toString())
//            val id: Int? = call.parameters["id"]?.toInt()
//            val pin: Int? = call.parameters["pin"]?.toInt()

            dataSource.username = "postgres"
            dataSource.password = "postgres"
            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"

            val connection = dataSource.connection
            val query = connection.prepareStatement("DELETE FROM Clients WHERE pin = $pin WHERE id = $id;")
            query.executeUpdate()
            call.response.status(HttpStatusCode.OK)
        }

        put ("/update-pin/{id}/{pin}") {

            val dataSource = HikariDataSource()


            val id : String = getVal(call.parameters["id"].toString())
            val pin : String = getVal(call.parameters["pin"].toString())

//            val id: Int? = call.parameters["id"]?.toInt()
//            val pin: Int? = call.parameters["newpin"]?.toInt()

            dataSource.username = "postgres"
            dataSource.password = "postgres"
            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"

            val connection = dataSource.connection
            val query = connection.prepareStatement("UPDATE Clients SET pin = $pin WHERE id = $id;")
            query.executeUpdate()
            call.response.status(HttpStatusCode.OK)
        }

        put ("/check-in/{id}/{pin}") {
            //http://localhost:8080/check-in/id=1/pin=1111
            val dataSource = HikariDataSource()
            //println(call.parameters["id"])
            println("\n"+getVal(call.parameters["id"].toString()))

            val id : String = getVal(call.parameters["id"].toString())
            val pin : String = getVal(call.parameters["pin"].toString())



            //val id: Int? = call.parameters["id"]?.toInt()
            //val pin: Int? = call.parameters["pin"]?.toInt()

            dataSource.username = "postgres"
            dataSource.password = "postgres"
            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"


            val connection = dataSource.connection
            val query = connection.prepareStatement("UPDATE Clients SET checkin = 1 WHERE id = $id AND pin = $pin;")
            query.executeUpdate()

            call.response.status(HttpStatusCode.OK)
        }

        put ("/check-out/{id}/{pin}") {
            //http://localhost:8080/check-out/id=1/pin=1111
            val dataSource = HikariDataSource()


            val id : String = getVal(call.parameters["id"].toString())
            val pin : String = getVal(call.parameters["pin"].toString())

//            val id: Int? = call.parameters["id"]?.toInt()
//            val pin: Int? = call.parameters["pin"]?.toInt()

            dataSource.username = "postgres"
            dataSource.password = "postgres"
            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"


            val connection = dataSource.connection
            val query = connection.prepareStatement("UPDATE Clients SET checkin = 0 WHERE id = $id AND pin = $pin;")
            query.executeUpdate()

            call.response.status(HttpStatusCode.OK)
        }

//        get("/berberabe-pin") {
//
//            val dataSource = HikariDataSource()
//
//            dataSource.username = "postgres"
//            dataSource.password = "postgres"
//            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"
//
//            val connection = dataSource.connection
//            val query = connection.prepareStatement("SELECT pin FROM client WHERE name = 'Berb';")
//            val result = query.executeQuery()
//            val pin = mutableListOf<Int>()
//            while (result.next()) {
//
//                pin.add(result.getInt("pin"))
//            }
//
//            call.respondText(pin.toString())
//        }
//        put ("/berberabe-update/{pin}") {
//
//            val dataSource = HikariDataSource()
//            val newPin: Int? = call.parameters["pin"]?.toInt()
//
//            dataSource.username = "postgres"
//            dataSource.password = "postgres"
//            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"
//
//
//            val connection = dataSource.connection
//            val query = connection.prepareStatement("UPDATE client SET pin = $newPin WHERE id = 1;")
//            query.executeQuery()
//            //call.respondText("Updated Berb's PIN to 8888")
//
//        }
//        get("/add-mantell") {
//
//
//            val dataSource = HikariDataSource()
//
//            dataSource.username = "postgres"
//            dataSource.password = "postgres"
//            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"
//
//
//            val connection = dataSource.connection
//            val query = connection.prepareStatement("INSERT INTO client VALUES ('Mantell', 2, 0, 0);")
//            query.executeQuery()
//        }
//        delete("/delete-mantell") {
//
//            val dataSource = HikariDataSource()
//
//            dataSource.username = "postgres"
//            dataSource.password = "postgres"
//            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"
//
//
//            val connection = dataSource.connection
//            val query = connection.prepareStatement("DELETE FROM client WHERE id = 2")
//            query.executeQuery()
//        }
    }
}
