package com.example.crawler

import com.example.store.Draw
import com.example.store.LottoHistoryStore
import kotlinx.coroutines.runBlocking
import java.time.LocalDate

const val INITIAL_SCRAPE_FROM = "1971-01-01"

class Crawler(
    private val lottoHistoryStore: LottoHistoryStore,
    private val veikkausHttpClient: VeikkausHttpClient
) {
    fun scrapeAndSaveDrawsSinceLatestSavedDraw() {
        scrapeAndSaveDrawsFromPeriod(
            startDate = getDateToStartScrapingFrom(),
            endDate = LocalDate.now()
        )
    }

    fun scrapeAndSaveDrawsFromPeriod(startDate: LocalDate, endDate: LocalDate) = runBlocking {
        val period = startDate..endDate
        val dateSequence = period.getStartAndEndDateAndEverySundayInBetween()
        val datePairsSequence = dateSequence.zipWithNext()

        println("Starting to scrape ${datePairsSequence.count()} periods from Veikkaus API")

        val uniqueScrapedDraws: List<Draw> = datePairsSequence.mapIndexed { index, pair ->
            val draws = veikkausHttpClient.getLottoDrawsBetweenDates(
                startDate = pair.first,
                endDate = pair.second
            )

            println("Fetched ${draws.size} draws for period #$index")

            draws
        }
            .flatten()
            .distinctBy(Draw::id)
            .toList()

        lottoHistoryStore.saveDraws(uniqueScrapedDraws)

        println("Saved a total of ${uniqueScrapedDraws.size} unique draws")
    }

    fun getDateToStartScrapingFrom(): LocalDate = lottoHistoryStore.getLatestDraw()
        ?.date
        ?.plusDays(1)
        ?: LocalDate.parse(INITIAL_SCRAPE_FROM)

}