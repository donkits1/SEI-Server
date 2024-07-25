package BikeRack

import com.example.plugins.configureDatabaseConnection
import com.google.gson.Gson
import com.zaxxer.hikari.HikariDataSource
import io.ktor.http.*
import io.ktor.network.sockets.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.time.LocalDate
import java.sql.Connection

// function to parse out the information in a request e.i. (/info=user_info) -> user_info
fun getVal(value: String) : String {
    val ans = value.split("=")

    if(ans.size > 2) {
        throw Exception("INVALID PARAMETERS")
    }
    else {
        return ans[ans.size-1]
    }
}

fun Application.configureBikeRouting(connection: Connection?) {

    //val connection = configureDatabaseConnection()


    routing {
        // Returns all bikes and prices
        get("/allbikes") {

            val query = connection!!.prepareStatement("SELECT * FROM \"Bikes\";")
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

        // returns all transaction history
        get("/trxhistory") {

            val query = connection!!.prepareStatement("SELECT * FROM \"TRXHistory\";")
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

        // gets id and customer name from database
        get("/getid/{customerName}") {

            val customerName : String = getVal(call.parameters["customerName"].toString()).replace("_", " ")

            val query = connection!!.prepareStatement("SELECT * FROM BikeCustomer WHERE name = \'$customerName\';")
            val result = query.executeQuery()
            val bikeCustomer = mutableListOf<BikeCustomer>()

            while (result.next()) {
                val id = result.getInt("id")
                val name = result.getString("name")

                bikeCustomer.add(BikeCustomer(id, name))
            }

            val gson = Gson()
            val returnData = gson.toJson(bikeCustomer)

            call.respondText(returnData)
        }

        get("/checkcustomer/{customerName}") {

            val customerName : String = getVal(call.parameters["customerName"].toString()).replace("_", " ")

            val query = connection!!.prepareStatement("SELECT * FROM BikeCustomer WHERE name = \'$customerName\';")
            val result = query.executeQuery()

            if (result.next()) {
                val customer = BikeCustomer(result.getInt("userid"),result.getString("name"))

                val gson = Gson()
                val returnData = gson.toJson(customer)
                call.respondText(returnData)
            } else {
                call.response.status(HttpStatusCode(204, "Customer not found"))
            }
        }


        post ("/addtrx/{order}/{total}/{userid}") {

            val order : String = getVal(call.parameters["order"].toString())
            val total : String = getVal(call.parameters["total"].toString())
            val userid : String = getVal(call.parameters["userid"].toString()).replace("_", " ")

            val currentTime = LocalDate.now()
            val query = connection!!.prepareStatement("INSERT INTO \"TRXHistory\" (\"desc\", \"totalCost\", \"date\", \"userid\") VALUES ('$order', $total, '$currentTime', $userid);")
            query.executeUpdate()
            call.response.status(HttpStatusCode.OK)

        }

        post ("/newCustomer/{name}") {

            val name : String = getVal(call.parameters["name"].toString()).replace("_", " ")

            val query = connection!!.prepareStatement("INSERT INTO BikeCustomer (name) VALUES (\'$name\');")
            query.executeUpdate()
            call.response.status(HttpStatusCode.OK)

        }

        delete ("/delete-customer/{id}") {

            val id : String = getVal(call.parameters["id"].toString())

            val query = connection!!.prepareStatement("DELETE FROM BikeCustomer WHERE userid = $id;")
            query.executeUpdate()
            call.response.status(HttpStatusCode.OK)
        }

        delete ("/delete-customer-trx/{id}") {

            val id : String = getVal(call.parameters["id"].toString())

            val query = connection!!.prepareStatement("DELETE FROM \"TRXHistory\" WHERE userid = $id;")
            query.executeUpdate()
            call.response.status(HttpStatusCode.OK)
        }
    }
}
