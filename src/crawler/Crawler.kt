package com.example.crawler

import com.example.store.Draw
import com.example.store.LottoHistoryStore
import kotlinx.coroutines.runBlocking
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

        val datePairsCount = datePairsSequence.count()
        println("Starting to scrape $datePairsCount periods from Veikkaus API")

        val uniqueScrapedDraws: List<Draw> = datePairsSequence.mapIndexed { index, pair ->
            val draws = veikkausHttpClient.getLottoDrawsBetweenDates(
                startDate = pair.first,
                endDate = pair.second
            )

            printStatus(index + 1, datePairsCount, pair.first.format(DateTimeFormatter.ISO_DATE))

            draws
        }
            .flatten()
            .distinctBy(Draw::id)
            .toList()

        println("Saving a total of ${uniqueScrapedDraws.size} unique draws")

        lottoHistoryStore.saveDraws(uniqueScrapedDraws)

        println("Scraping and saving completed.")
    }

    private fun printStatus(current: Int, total: Int, additionalInfo: String) {
        val status = if (current < total) {
            "Scraping period $current / $total ($additionalInfo)".padEnd(80, ' ') + "\r"
        } else {
            "Scraping done.".padEnd(80, ' ') + "\n"
        }

        print(status)
    }

    fun getDateToStartScrapingFrom(): LocalDate = lottoHistoryStore.getLatestDraw()
        ?.date
        ?.plusDays(1)
        ?: LocalDate.parse(INITIAL_SCRAPE_FROM)

}