package com.example.currency.data.repository

import com.example.currency.data.model.response.HistoricalResponse
import com.example.currency.data.model.response.LatestRatesResponse
import com.example.currency.data.model.response.SymbolsResponse
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {

    fun getSymbols(): Flow<SymbolsResponse>

    fun convert(from: String, to: String): Flow<LatestRatesResponse>

    fun getHistoricalForLast3Days(
        date: String,
        base: String,
        symbols: String
    ): Flow<HistoricalResponse>

    fun getLatestRatesForOtherCurrencies(base: String, symbols: String): Flow<LatestRatesResponse>
}