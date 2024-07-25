package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }


    @Test
    fun testNewCustomer() = testApplication {
        client.post("/newCustomer/name=Salazar").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun testGetBikeID() = testApplication {
        client.get("/checkcustomer/customerName=Berb").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("{\"userid\":43,\"name\":\"Berb\"}", bodyAsText())
        }
    }

//    @Test
//    fun testRecordTRX() = testApplication {
//        client.post("/addtrx/order=1Bike/total=20/userid=1").apply {
//            assertEquals(HttpStatusCode.OK, status)
//        }
//    }

//    @Test
//    fun testDelete() = testApplication {
//        client.post("/addtrx/order=1Bike/total=20/userid=1").apply {
//            assertEquals(HttpStatusCode.OK, status)
//        }
//    }
}
