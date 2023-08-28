package com.example.currency.data.source.remote

import com.example.currency.data.model.response.HistoricalResponse
import com.example.currency.data.model.response.LatestRatesResponse
import com.example.currency.data.model.response.SymbolsResponse
import com.example.currency.utils.Constants.Companion.API_KEY
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrencyApiService {

    @GET("symbols")
    suspend fun getSymbols(@Query("access_key") apiKey: String = API_KEY): SymbolsResponse

    @GET("latest")
    suspend fun convert(
        @Query("access_key") apiKey: String = API_KEY,
        @Query("base") from: String,
        @Query("symbols") to: String
    ): LatestRatesResponse

    @GET("{date}")
    suspend fun getHistoricalForLast3Days(
        @Path("date") date: String,
        @Query("access_key") apiKey: String = API_KEY,
        @Query("base") base: String,
        @Query("symbols") symbols: String
    ): HistoricalResponse

    @GET("latest")
    suspend fun getLatestRatesForOtherCurrencies(
        @Query("access_key") apiKey: String = API_KEY,
        @Query("base") base: String,
        @Query("symbols") symbols: String
    ): LatestRatesResponse
}