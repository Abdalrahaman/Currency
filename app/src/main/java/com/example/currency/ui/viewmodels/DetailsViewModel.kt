package com.example.currency.ui.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.currency.data.model.pojo.HistoricalRates
import com.example.currency.data.repository.CurrencyRepository
import com.example.currency.ui.base.BaseViewModel
import com.example.currency.ui.fragments.DetailsFragmentArgs
import com.example.currency.utils.Result
import com.example.currency.utils.WhileUiSubscribed
import com.example.currency.utils.asResult
import com.example.currency.utils.getLastThreeDaysDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    currencyRepository: CurrencyRepository
) : BaseViewModel() {

    private val args = DetailsFragmentArgs.fromSavedStateHandle(savedStateHandle)
    private val baseCode = args.baseCurrencyCode ?: ""

    val historicalRatesState: StateFlow<HistoricalRatesUiState> =
        currencyRepository.getHistoricalForLast3Days(
            date = getLastThreeDaysDate(),
            base = baseCode,
            symbols = ""
        ).asResult().map {
            when (it) {
                is Result.Success -> {
                    if (it.data.success) {
                        HistoricalRatesUiState.Success(
                            historicalRates = it.data.rates!!.map { rate ->
                                HistoricalRates(rate.key, rate.value)
                            }
                        )
                    } else {
                        HistoricalRatesUiState.Error(
                            it.data.error?.type ?: ""
                        )
                    }
                }

                is Result.Loading -> {
                    HistoricalRatesUiState.Loading
                }

                is Result.Error -> {
                    HistoricalRatesUiState.Error(
                        it.message ?: ""
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = HistoricalRatesUiState.Loading
        )

    val latestRatesForOtherCurrenciesState: StateFlow<LatestRatesForOtherCurrenciesUiState> =
        currencyRepository.getLatestRatesForOtherCurrencies(
            base = baseCode,
            symbols = ""
        ).asResult().map {
            when (it) {
                is Result.Success -> {
                    if (it.data.success) {
                        LatestRatesForOtherCurrenciesUiState.Success(
                            latestRates = it.data.rates!!.map { rate ->
                                HistoricalRates(rate.key, rate.value)
                            }.take(10)
                        )
                    } else {
                        LatestRatesForOtherCurrenciesUiState.Error(
                            it.data.error?.type ?: ""
                        )
                    }
                }

                is Result.Loading -> {
                    LatestRatesForOtherCurrenciesUiState.Loading
                }

                is Result.Error -> {
                    LatestRatesForOtherCurrenciesUiState.Error(
                        it.message ?: ""
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = LatestRatesForOtherCurrenciesUiState.Loading
        )
}

sealed interface HistoricalRatesUiState {
    data class Success(val historicalRates: List<HistoricalRates>) : HistoricalRatesUiState
    object Loading : HistoricalRatesUiState
    data class Error(val message: String) : HistoricalRatesUiState
}

sealed interface LatestRatesForOtherCurrenciesUiState {
    data class Success(val latestRates: List<HistoricalRates>) :
        LatestRatesForOtherCurrenciesUiState

    object Loading : LatestRatesForOtherCurrenciesUiState
    data class Error(val message: String) : LatestRatesForOtherCurrenciesUiState
}