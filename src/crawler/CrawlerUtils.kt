package com.example.crawler

import java.time.*

fun localDateToEpochMilliseconds(localDate: LocalDate): Long =
    localDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000

fun epochMillisecondsToLocalDate(epochMillis: Long): LocalDate =
    Instant.ofEpochMilli(epochMillis).atOffset(ZoneOffset.UTC).toLocalDate()

fun ClosedRange<LocalDate>.everySunday() = sequence {
    var current = start
    do {
        if (current.isSunday()) {
            yield(current)
        }

        current = current.plusDays(1)
    } while (current <= endInclusive)
}

private fun LocalDate.isSunday() = this.dayOfWeek == DayOfWeek.SUNDAY
