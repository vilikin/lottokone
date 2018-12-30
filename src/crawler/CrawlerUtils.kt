package com.example.crawler

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneOffset

fun LocalDate.getMillisecondsSinceEpoch() =
    this.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000

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
