package com.example

import com.example.crawler.Crawler
import com.example.crawler.VeikkausHttpClient
import com.example.store.InMemoryLottoHistoryStore
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.gson.*
import io.ktor.features.*

val store = InMemoryLottoHistoryStore()
val client = VeikkausHttpClient()
val crawler = Crawler(store, client)

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        get("/") {
            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
        }

        get("/actions/scrape") {
            crawler.scrapeAndSaveDrawsSinceLatestSavedDraw()

            call.respondText("Scraped successfully", contentType = ContentType.Text.Plain)
        }

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

