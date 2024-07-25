package com.example.plugins

import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import java.sql.Connection

fun Application.configureDatabaseConnection(): Connection? {
    val dataSource = HikariDataSource()
    dataSource.username = "postgres"
    dataSource.password = "postgres"

    dataSource.jdbcUrl = "jdbc:postgresql://localhost:5440/donne.berberabe"

    return dataSource.connection
}
