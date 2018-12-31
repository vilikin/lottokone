package com.example.store

class MockLottoHistoryStore : LottoHistoryStore {
    val draws: MutableList<Draw> = mutableListOf()

    override fun saveDraws(vararg draws: Draw) {
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