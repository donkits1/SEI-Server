package BikeRack

import Models.Client
import com.google.gson.Gson
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate

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

fun Application.configureBikeRouting() {
    routing {
        get("/allbikes") {

            val dataSource = HikariDataSource()
            dataSource.username = "postgres"
            dataSource.password = "postgres"

            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"

            val connection = dataSource.connection
            val query = connection.prepareStatement("SELECT * FROM \"Bikes\";")
            println("\n\n\nSELECT * FROM \"Bikes\";\n\n\n")
            val result = query.executeQuery()
            val bikes = mutableListOf<Bike>()

            while (result.next()) {
                val bikeName = result.getString("Bike Name")
                val cost = result.getInt("Cost")

                bikes.add(Bike(bikeName, cost))
            }

            val gson = Gson()
            val returnData = gson.toJson(bikes)

            call.respondText(returnData)
        }


        get("/trxhistory") {

            val dataSource = HikariDataSource()
            dataSource.username = "postgres"
            dataSource.password = "postgres"

            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"

            val connection = dataSource.connection
            val query = connection.prepareStatement("SELECT * FROM \"TRXHistory\";")
            println("\n\n\nSELECT * FROM \"TRXHistory\";\n\n\n")
            val result = query.executeQuery()
            val transactions = mutableListOf<bikeTRX>()

            while (result.next()) {
                val id = result.getInt("id")
                val desc = result.getString("desc")
                val totalCost = result.getInt("totalCost")
                val date = result.getString("date")

                transactions.add(bikeTRX(id, desc, totalCost, date))
            }

            val gson = Gson()
            val returnData = gson.toJson(transactions)

            call.respondText(returnData)
        }

        get("/getid/{customerName}") {

            val customerName : String = getVal(call.parameters["customerName"].toString()).replace("_", " ")
            val dataSource = HikariDataSource()
            dataSource.username = "postgres"
            dataSource.password = "postgres"

            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"

            val connection = dataSource.connection
            val query = connection.prepareStatement("SELECT * FROM BikeCustomer WHERE name = \'$customerName\';")
            println("\n\n\nSELECT * FROM BikeCustomer WHERE name = '$customerName';\n\n\n")
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

        get("/checkcustomer/{customerName}") {

            val dataSource = HikariDataSource()
            val customerName : String = getVal(call.parameters["customerName"].toString()).replace("_", " ")
            val singlePerson : BikeCustomer

            dataSource.username = "postgres"
            dataSource.password = "postgres"

            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"

            val connection = dataSource.connection
            val query = connection.prepareStatement("SELECT * FROM BikeCustomer WHERE name = \'$customerName\';")
            println("\n\n\nSELECT * FROM BikeCustomer WHERE name = \'$customerName\';\n\n\n")
            val result = query.executeQuery()

            if (result.next()) {
                val customer = BikeCustomer(result.getInt("userid"),result.getString("name"))

                val gson = Gson()
                val returnData = gson.toJson(customer)
                call.respondText(returnData)
            } else {
                call.response.status(HttpStatusCode(204, "Customer not found"))
            }

//            if (result.fetchSize == 1) {
//                singlePerson = BikeCustomer(result.getInt("userid"),result.getString("name"))
//                call.respondText(singlePerson.userid.toString())
//            } else {


//            if (result.next()) {
//                result.previous()
//                while (result.next()) {
//                    val userid = result.getInt("userid")
//                    val name = result.getString("name")
//                    customers.add(BikeCustomer(userid, name))
//                }
//            } else {
//                result.previous()
//                call.respondText("No Data Found", ContentType.Text.Plain, HttpStatusCode.NotFound)
//            }


//            }

            // IMPORTANT @@@@@@@@@@@@@
            // IMPORTANT @@@@@@@@@@@@@

            //call.respondText(returnData,ContentType.Application.Json, HttpStatusCode.OK)
//            call.respondText(returnData)
        }


        post ("/addtrx/{order}/{total}/{userid}") {

            val dataSource = HikariDataSource()
            val order : String = getVal(call.parameters["order"].toString())
            val total : String = getVal(call.parameters["total"].toString())
            val userid : String = getVal(call.parameters["userid"].toString()).replace("_", " ")
//            val name: String? = call.parameters["name"]
//            val pin: Int? = call.parameters["pin"]?.toInt()

            dataSource.username = "postgres"
            dataSource.password = "postgres"
            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"


            val connection = dataSource.connection
            val currentTime = LocalDate.now()
            val query = connection.prepareStatement("INSERT INTO \"TRXHistory\" (\"desc\", \"totalCost\", \"date\", \"userid\") VALUES ('$order', $total, '$currentTime', $userid);")
            println("\n\n\nINSERT INTO \"TRXHistory\" (\"desc\", \"totalCost\", \"date\", \"userid\") VALUES ('$order', $total, '$currentTime', $userid);\n\n\n")
            query.executeUpdate()
            call.response.status(HttpStatusCode.OK)

        }

        post ("/newCustomer/{name}") {

            val dataSource = HikariDataSource()
            val name : String = getVal(call.parameters["name"].toString()).replace("_", " ")
//            val name: String? = call.parameters["name"]
//            val pin: Int? = call.parameters["pin"]?.toInt()

            dataSource.username = "postgres"
            dataSource.password = "postgres"
            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"


            val connection = dataSource.connection
            val currentTime = LocalDate.now()
            val query = connection.prepareStatement("INSERT INTO BikeCustomer (name) VALUES (\'$name\');")
            println("\n\n\nINSERT INTO BikeCustomer (name) VALUES ('$name');\n\n\n")
            query.executeUpdate()
            call.response.status(HttpStatusCode.OK)

        }

        delete ("/delete-customer/{id}") {

            val dataSource = HikariDataSource()
            val id : String = getVal(call.parameters["id"].toString())
//            val id : String = com.example.plugins.getVal(call.parameters["id"].toString())
//            val pin : String = com.example.plugins.getVal(call.parameters["pin"].toString())
//            val id: Int? = call.parameters["id"]?.toInt()
//            val pin: Int? = call.parameters["pin"]?.toInt()

            dataSource.username = "postgres"
            dataSource.password = "postgres"
            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"

            val connection = dataSource.connection
            val query = connection.prepareStatement("DELETE FROM BikeCustomer WHERE userid = $id;")
            println("\n\n\nDELETE FROM BikeCustomer WHERE userid = $id;\n\n\n")
            query.executeUpdate()
            call.response.status(HttpStatusCode.OK)
        }

        delete ("/delete-customer-trx/{id}") {

            val dataSource = HikariDataSource()
            val id : String = getVal(call.parameters["id"].toString())
//            val id : String = com.example.plugins.getVal(call.parameters["id"].toString())
//            val pin : String = com.example.plugins.getVal(call.parameters["pin"].toString())
//            val id: Int? = call.parameters["id"]?.toInt()
//            val pin: Int? = call.parameters["pin"]?.toInt()

            dataSource.username = "postgres"
            dataSource.password = "postgres"
            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"

            val connection = dataSource.connection
            val query = connection.prepareStatement("DELETE FROM \"TRXHistory\" WHERE userid = $id;")
            println("\n\n\nDELETE FROM \"TRXHistory\" WHERE userid = $id;\n\n\n")
            query.executeUpdate()
            call.response.status(HttpStatusCode.OK)
        }

//        delete ("/delete-client/{id}/{pin}") {
//
//            val dataSource = HikariDataSource()
//            val id : String = getVal(call.parameters["id"].toString())
//            val pin : String = getVal(call.parameters["pin"].toString())
////            val id: Int? = call.parameters["id"]?.toInt()
////            val pin: Int? = call.parameters["pin"]?.toInt()
//
//            dataSource.username = "postgres"
//            dataSource.password = "postgres"
//            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"
//
//            val connection = dataSource.connection
//            val query = connection.prepareStatement("DELETE FROM Clients WHERE pin = $pin WHERE id = $id;")
//            query.executeUpdate()
//            call.response.status(HttpStatusCode.OK)
//        }
//
//        put ("/update-pin/{id}/{pin}") {
//
//            val dataSource = HikariDataSource()
//
//
//            val id : String = getVal(call.parameters["id"].toString())
//            val pin : String = getVal(call.parameters["pin"].toString())
//
////            val id: Int? = call.parameters["id"]?.toInt()
////            val pin: Int? = call.parameters["newpin"]?.toInt()
//
//            dataSource.username = "postgres"
//            dataSource.password = "postgres"
//            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"
//
//            val connection = dataSource.connection
//            val query = connection.prepareStatement("UPDATE Clients SET pin = $pin WHERE id = $id;")
//            query.executeUpdate()
//            call.response.status(HttpStatusCode.OK)
//        }
//
//        put ("/check-in/{id}/{pin}") {
//            //http://localhost:8080/check-in/id=1/pin=1111
//            val dataSource = HikariDataSource()
//            //println(call.parameters["id"])
//            println("\n"+getVal(call.parameters["id"].toString()))
//
//            val id : String = getVal(call.parameters["id"].toString())
//            val pin : String = getVal(call.parameters["pin"].toString())
//
//
//
//            //val id: Int? = call.parameters["id"]?.toInt()
//            //val pin: Int? = call.parameters["pin"]?.toInt()
//
//            dataSource.username = "postgres"
//            dataSource.password = "postgres"
//            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"
//
//
//            val connection = dataSource.connection
//            val query = connection.prepareStatement("UPDATE Clients SET checkin = 1 WHERE id = $id AND pin = $pin;")
//            query.executeUpdate()
//
//            call.response.status(HttpStatusCode.OK)
//        }
//
//        put ("/check-out/{id}/{pin}") {
//            //http://localhost:8080/check-out/id=1/pin=1111
//            val dataSource = HikariDataSource()
//
//
//            val id : String = getVal(call.parameters["id"].toString())
//            val pin : String = getVal(call.parameters["pin"].toString())
//
////            val id: Int? = call.parameters["id"]?.toInt()
////            val pin: Int? = call.parameters["pin"]?.toInt()
//
//            dataSource.username = "postgres"
//            dataSource.password = "postgres"
//            dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"
//
//
//            val connection = dataSource.connection
//            val query = connection.prepareStatement("UPDATE Clients SET checkin = 0 WHERE id = $id AND pin = $pin;")
//            query.executeUpdate()
//
//            call.response.status(HttpStatusCode.OK)
//        }

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
