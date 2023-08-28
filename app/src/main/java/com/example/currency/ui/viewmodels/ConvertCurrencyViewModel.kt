package com.example.currency.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.currency.data.repository.CurrencyRepository
import com.example.currency.ui.base.BaseViewModel
import com.example.currency.utils.Result
import com.example.currency.utils.WhileUiSubscribed
import com.example.currency.utils.asResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ConvertCurrencyViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository
) : BaseViewModel() {

    val baseCurrencyCode = MutableLiveData<String>()
    val targetCurrencyCode = MutableLiveData<String>()

    val baseCurrencyAmount = MutableLiveData("1")

    private val _targetCurrencyAmount = MutableStateFlow(0.0)
    val targetCurrencyAmount: StateFlow<Double> = _targetCurrencyAmount.asStateFlow()

    private val _errorConvert = MutableLiveData<String?>()
    val errorConvert: LiveData<String?>
        get() = _errorConvert

    val symbolUiState: StateFlow<SymbolUiState> = currencyRepository.getSymbols().asResult().map {
        when (it) {
            is Result.Success -> {
                if (it.data.success)
                    SymbolUiState.Success(
                        symbols = it.data.symbols.keys.toList()
                    )
                else
                    SymbolUiState.Error(
                        it.data.error?.type ?: ""
                    )
            }

            is Result.Loading -> {
                SymbolUiState.Loading
            }

            is Result.Error -> {
                SymbolUiState.Error(
                    it.message ?: ""
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = WhileUiSubscribed,
        initialValue = SymbolUiState.Loading
    )

    fun convert(
        from: String = baseCurrencyCode.value ?: "",
        to: String = targetCurrencyCode.value ?: ""
    ) {
        currencyRepository.convert(from = from, to = to).asResult().map {
            when (it) {
                is Result.Success -> {
                    if (it.data.success) {
                        _targetCurrencyAmount.value =
                            baseCurrencyAmount.value!!.toDouble() * it.data.rates!!.values.first()
                    } else {
                        _errorConvert.value = it.data.error?.type
                    }
                }

                is Result.Error -> {
                    _errorConvert.value = it.message
                }

                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    fun swapCurrencies() {
        baseCurrencyCode.value =
            targetCurrencyCode.value.also { targetCurrencyCode.value = baseCurrencyCode.value }
    }

    fun onConvertErrorFinished(){
        _errorConvert.value = null
    }

}

sealed interface SymbolUiState {
    data class Success(val symbols: List<String>) : SymbolUiState
    object Loading : SymbolUiState
    data class Error(val message: String) : SymbolUiState
}