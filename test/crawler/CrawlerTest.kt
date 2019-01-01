package com.example.crawler

import com.example.store.Draw
import com.example.store.InMemoryLottoHistoryStore
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate

@DisplayName("Crawler")
class CrawlerTest {

    private val mockDraw1 = Draw("1", LocalDate.MAX, emptySet())
    private val mockDraw2 = Draw("2", LocalDate.MAX, emptySet())
    private val mockDraw3 = Draw("3", LocalDate.MAX, emptySet())

    @DisplayName("should scrape draws since last saved draw when there are existing draws stored")
    @Test
    fun shouldScrapeDrawsSinceLastSavedDraw() {
        val mockClient = mockk<VeikkausHttpClient>()
        val mockStore = mockk<InMemoryLottoHistoryStore>()
        val crawler = Crawler(mockStore, mockClient)

        every { mockStore.getLatestDraw() } returns Draw(
            id = "1",
            date = LocalDate.of(2018, 12, 1),
            primaryNumbers = emptySet()
        )

        val expectedStartDate = LocalDate.of(2018, 12, 2)
        val actualStartDate = crawler.getDateToStartScrapingFrom()

        assertEquals(expectedStartDate, actualStartDate)
    }

    @DisplayName("should scrape draws since 1971 when there are no existing draws stored")
    @Test
    fun shouldAttemptToScrapeDrawsSince1971() {
        val mockClient = mockk<VeikkausHttpClient>()
        val mockStore = mockk<InMemoryLottoHistoryStore>()
        val crawler = Crawler(mockStore, mockClient)

        every { mockStore.getLatestDraw() } returns null

        val expectedStartDate = LocalDate.of(1971, 1, 1)
        val actualStartDate = crawler.getDateToStartScrapingFrom()

        assertEquals(expectedStartDate, actualStartDate)
    }

    @DisplayName("should attempt to scrape draws in max week long periods")
    @Test
    fun shouldScrapeInPeriodsOfWeeks() {
        val mockClient = getMockClientWithDraws(listOf(mockDraw1, mockDraw2, mockDraw3))
        val mockStore = spyk<InMemoryLottoHistoryStore>()
        val crawler = Crawler(mockStore, mockClient)

        crawler.scrapeAndSaveDrawsFromPeriod(
            LocalDate.of(2018, 12, 1),
            LocalDate.of(2018, 12, 15)
        )

        verify {
            mockClient.getLottoDrawsBetweenDates(
                LocalDate.of(2018, 12, 1),
                LocalDate.of(2018, 12, 2)
            )

            mockClient.getLottoDrawsBetweenDates(
                LocalDate.of(2018, 12, 2),
                LocalDate.of(2018, 12, 9)
            )

            mockClient.getLottoDrawsBetweenDates(
                LocalDate.of(2018, 12, 9),
                LocalDate.of(2018, 12, 15)
            )
        }
    }

    @DisplayName("should attempt to save all unique scraped draws")
    @Test
    fun shouldAttemptToSaveScrapedUniqueDraws() {
        val mockClient = getMockClientWithDraws(listOf(mockDraw1, mockDraw2, mockDraw1))
        val mockStore = spyk<InMemoryLottoHistoryStore>()
        val crawler = Crawler(mockStore, mockClient)

        crawler.scrapeAndSaveDrawsFromPeriod(
            LocalDate.of(2018, 12, 1),
            LocalDate.of(2018, 12, 15)
        )

        verify {
            mockStore.saveDraws(listOf(mockDraw1, mockDraw2))
        }
    }

}

fun getMockClientWithDraws(draws: List<Draw>): VeikkausHttpClient {
    val mockClient = spyk<VeikkausHttpClient>()
    val drawsIterator = draws.iterator()

    every { mockClient.getLottoDrawsBetweenDates(any(), any()) } answers {
        listOf(drawsIterator.next())
    }

    return mockClient
}