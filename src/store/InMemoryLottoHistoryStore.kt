package com.example.store

class InMemoryLottoHistoryStore : LottoHistoryStore {
    private val draws: MutableList<Draw> = mutableListOf()

    override fun saveDraws(draws: List<Draw>) {
        this.draws.addAll(draws)
    }

    override fun getLatestDraw(): Draw? {
        if (draws.isEmpty()) return null
        return this.draws.maxBy(Draw::date)
    }

    override fun getAllDraws(): List<Draw> {
        return this.draws
    }
}