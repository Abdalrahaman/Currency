package com.example.currency.data.repository

import com.example.currency.data.model.response.HistoricalResponse
import com.example.currency.data.model.response.LatestRatesResponse
import com.example.currency.data.model.response.SymbolsResponse
import com.example.currency.data.source.remote.CurrencyApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrencyRepositoryImpl @Inject constructor(
    private val currencyApiService: CurrencyApiService
) : CurrencyRepository {

    override fun getSymbols(): Flow<SymbolsResponse> = flow {
        emit(
            currencyApiService.getSymbols()
        )
    }

    override fun convert(from: String, to: String): Flow<LatestRatesResponse> = flow {
        emit(
            currencyApiService.convert(from = from, to = to)
        )
    }

    override fun getHistoricalForLast3Days(
        date: String,
        base: String,
        symbols: String
    ): Flow<HistoricalResponse> = flow {
        emit(
            currencyApiService.getHistoricalForLast3Days(
                date = date,
                base = base,
                symbols = symbols
            )
        )
    }

    override fun getLatestRatesForOtherCurrencies(
        base: String,
        symbols: String
    ): Flow<LatestRatesResponse> = flow {
        emit(
            currencyApiService.getLatestRatesForOtherCurrencies(
                base = base,
                symbols = symbols
            )
        )
    }
}