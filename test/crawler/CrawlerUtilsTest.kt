package com.example.crawler

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import java.time.LocalDate

@DisplayName("Crawler")
class CrawlerUtilsTest {

    @DisplayName("should get milliseconds of a LocalDate")
    @Test
    fun shouldGetMillisecondsOfLocalDate() {
        val date = LocalDate.of(2018, 12, 23)
        val actualMilliseconds = localDateToEpochMilliseconds(date)
        assertEquals(1545523200000, actualMilliseconds)
    }

    @DisplayName("should find every Sunday from range of LocalDates that starts and ends with Sunday")
    @Test
    fun shouldFindEverySundayFromRangeThatStartsAndEndsWithSunday() {
        val startDate = LocalDate.of(2018, 12, 2)
        val endDate = LocalDate.of(2018, 12, 30)
        val range = startDate..endDate
        val actualListOfSundays = range.everySunday().toList()

        val expectedListOfSundays = listOf(
            LocalDate.of(2018, 12, 2),
            LocalDate.of(2018, 12, 9),
            LocalDate.of(2018, 12, 16),
            LocalDate.of(2018, 12, 23),
            LocalDate.of(2018, 12, 30)
        )

        assertEquals(expectedListOfSundays, actualListOfSundays)
    }

    @DisplayName("should find every Sunday from range of LocalDates that doesn't start and end with Sunday")
    @Test
    fun shouldFindEverySundayFromRangeThatDoesntStartAndEndWithSunday() {
        val startDate = LocalDate.of(2018, 12, 3)
        val endDate = LocalDate.of(2018, 12, 29)
        val range = startDate..endDate
        val actualListOfSundays = range.everySunday().toList()

        val expectedListOfSundays = listOf(
            LocalDate.of(2018, 12, 9),
            LocalDate.of(2018, 12, 16),
            LocalDate.of(2018, 12, 23)
        )

        assertEquals(expectedListOfSundays, actualListOfSundays)
    }

}