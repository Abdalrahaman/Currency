package com.example.currency.data.module

import com.example.currency.data.repository.CurrencyRepository
import com.example.currency.data.repository.CurrencyRepositoryImpl
import com.example.currency.data.source.remote.CurrencyApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideCurrencyRepository(currencyApiService: CurrencyApiService): CurrencyRepository =
        CurrencyRepositoryImpl(currencyApiService)
}