package com.example

import com.example.crawler.Crawler
import com.example.crawler.VeikkausHttpClient
import com.example.engine.LottoEngine
import com.example.store.PersistedLottoHistoryStore
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.content.default
import io.ktor.http.content.files
import io.ktor.http.content.static
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing

val store = PersistedLottoHistoryStore()
val client = VeikkausHttpClient()
val crawler = Crawler(store, client)
val engine = LottoEngine(store)

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        get("/api/row") {
            call.respond(engine.getRandomRow())
        }

        get("/actions/scrape") {
            crawler.scrapeAndSaveDrawsSinceLatestSavedDraw()

            call.respondText("Scraped successfully", contentType = ContentType.Text.Plain)
        }

        get("/api/stats") {
            val map = store.getAllDraws()
                .filter { it.primaryNumbers.size == 7 }
                .flatMap { it.primaryNumbers }
                .groupingBy { it }
                .eachCount()

            call.respond(map)
        }

        static("/") {
            files("static")
            default("static/index.html")
        }
    }
}

