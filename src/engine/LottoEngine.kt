package com.example.engine

import com.example.store.Draw
import com.example.store.LottoHistoryStore

class LottoEngine(private val store: LottoHistoryStore) {
    fun getRandomRow(): Set<Int> {
        val draws = store.getAllDraws()
        val allNumbersEver = draws.flatMap(Draw::primaryNumbers)

        val randomRow: MutableSet<Int> = mutableSetOf()

        for (i in 1..7) {
            var number: Int

            do {
                number = allNumbersEver.random()
            } while (number in randomRow)

            randomRow.add(number)
        }

        return randomRow.sorted().toSet()
    }
}