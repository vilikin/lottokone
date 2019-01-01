package com.example.store

import java.time.LocalDate

data class Draw(
    val id: String,
    val date: LocalDate,
    val primaryNumbers: Set<Int>,
    val secondaryNumbers: Set<Int>? = null,
    val tertiaryNumbers: Set<Int>? = null
)

interface LottoHistoryStore {
    fun saveDraws(draws: List<Draw>)
    fun getLatestDraw(): Draw?
    fun getAllDraws(): List<Draw>
}