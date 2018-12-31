package com.example.crawler

import com.example.store.LottoHistoryStore

class Crawler(
    val lottoHistoryStore: LottoHistoryStore,
    val veikkausHttpClient: VeikkausHttpClient
) {
    fun scrapeAndSaveDrawsSinceLastSavedDraw() {

    }
}