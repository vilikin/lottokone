package com.example

import com.example.crawler.Crawler
import com.example.crawler.VeikkausHttpClient
import com.example.store.Draw
import com.example.store.PersistedLottoHistoryStore
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing

val store = PersistedLottoHistoryStore()
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
        get("/api/random-row") {
            val draws = store.getAllDraws()
            val allNumbersEver = draws.flatMap(Draw::primaryNumbers)

            val lottoRow: MutableSet<Int> = mutableSetOf()

            for (i in 1..7) {
                var number: Int

                do {
                    number = allNumbersEver.random()
                } while (number in lottoRow)

                lottoRow.add(number)
            }

            call.respondText(lottoRow.joinToString())
        }

        get("/actions/scrape") {
            crawler.scrapeAndSaveDrawsSinceLatestSavedDraw()

            call.respondText("Scraped successfully", contentType = ContentType.Text.Plain)
        }

        get("/actions/get-latest") {
            call.respond(store.getLatestDraw()!!)
        }

        get("/json/gson") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}

