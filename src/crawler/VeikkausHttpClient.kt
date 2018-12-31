package com.example.crawler

import com.example.store.Draw
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import java.time.LocalDate

const val VEIKKAUS_DRAWS_URL = "https://www.veikkaus.fi/api/hdr/v2/draw-games/draws"

data class VeikkausResponseRoot(
    val draws: List<VeikkausResponseDraw>
)

data class VeikkausResponseDraw(
    val drawTime: Long,
    val results: List<VeikkausResponseDrawResults>
)

data class VeikkausResponseDrawResults(
    val primary: Set<String>,
    val secondary: Set<String>?,
    val tertiary: Set<String>?
)

class VeikkausHttpClient {
    fun getLottoDrawsBetweenDates(startDate: LocalDate, endDate: LocalDate): List<Draw> {
        val (_, _, result) = VEIKKAUS_DRAWS_URL
            .httpGet(
                listOf(
                    "game-names" to "LOTTO",
                    "status" to "RESULTS_AVAILABLE",
                    "date-from" to localDateToEpochMilliseconds(startDate),
                    "date-to" to localDateToEpochMilliseconds(endDate)
                )
            )
            .header("X-ESA-API-Key" to "WWW")
            .responseObject<VeikkausResponseRoot>()

        result.fold(
            success = { responseRoot ->
                return responseRoot.draws.map { draw ->
                    Draw(
                        date = epochMillisecondsToLocalDate(draw.drawTime),
                        primaryNumbers = draw.results.first().primary.map(String::toInt).toSet(),
                        secondaryNumbers = draw.results.first().secondary?.map(String::toInt)?.toSet(),
                        tertiaryNumbers = draw.results.first().tertiary?.map(String::toInt)?.toSet()
                    )
                }
            },
            failure = { error ->
                val statusCode = error.response.statusCode
                val responseBody = String(error.errorData)
                throw Exception("Failed to fetch draws from Veikkaus API. Status: $statusCode Response: $responseBody")
            }
        )
    }
}