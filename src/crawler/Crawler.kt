package com.example.crawler

import com.example.store.Draw
import com.example.store.LottoHistoryStore
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

    fun scrapeAndSaveDrawsFromPeriod(startDate: LocalDate, endDate: LocalDate) {
        val period = startDate..endDate
        val dateSequence = period.getStartAndEndDateAndEverySundayInBetween()

        val allScrapedDraws: MutableList<Draw> = mutableListOf()

        var previousDate: LocalDate = dateSequence.first()

        for (currentDate in dateSequence) {
            if (currentDate != previousDate) {
                val draws = veikkausHttpClient.getLottoDrawsBetweenDates(
                    startDate = previousDate,
                    endDate = currentDate
                )

                allScrapedDraws.addAll(draws)
            }

            previousDate = currentDate
        }

        val allUniqueScrapedDraws = allScrapedDraws.distinctBy(Draw::id)
        lottoHistoryStore.saveDraws(allUniqueScrapedDraws)
    }

    fun getDateToStartScrapingFrom(): LocalDate = lottoHistoryStore.getLatestDraw()
        ?.date
        ?.plusDays(1)
        ?: LocalDate.parse(INITIAL_SCRAPE_FROM)

}