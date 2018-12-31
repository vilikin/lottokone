package com.example.store

import java.time.LocalDate

data class Draw(
    val date: LocalDate,
    val primaryNumbers: Set<Int>,
    val secondaryNumbers: Set<Int>?,
    val tertiaryNumbers: Set<Int>?
)

interface LottoHistoryStore {
    fun saveDraws(vararg draws: Draw)
    fun getLatestDraw(): Draw?
    fun getAllDraws(): List<Draw>
}