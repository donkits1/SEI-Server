package com.example

import BikeRack.configureBikeRouting
import com.example.plugins.configureDatabaseConnection
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
//    configureDatabases()
//    configureRouting()
//    connectToPostgres(embedded = true)
    val connection = configureDatabaseConnection()
    configureBikeRouting(connection)
}