package com.example.northstar.ui.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.northstar.data.remote.CurrencyApiService
import com.example.northstar.data.repository.IncomeRepository
import com.example.northstar.domain.model.Income
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed class IncomeUiState {
    object Idle : IncomeUiState()
    object Loading : IncomeUiState()
    object Success : IncomeUiState()
    data class Error(val message: String) : IncomeUiState()
}

@HiltViewModel
class IncomeViewModel @Inject constructor(
    private val repository: IncomeRepository,
    private val apiService: CurrencyApiService
) : ViewModel() {

    private val sourceCurrencyMap = mapOf(
        "Salary" to listOf("LKR", "USD"),
        "Freelance" to listOf("LKR", "USD"),
        "Social Media" to listOf("USD"),
        "Google AdSense" to listOf("USD"),
        "Investments" to listOf("LKR"),
        "E-commerce" to listOf("USD"),
        "Affiliate" to listOf("USD"),
        "Crypto" to listOf("USDT", "BTC", "ETH", "ALT"),
        "Digital Products" to listOf("USD"),
        "Tutoring" to listOf("LKR", "USD"),
        "Other" to listOf("LKR", "USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD", "INR", "CNY")
    )

    private val _uiState = MutableStateFlow<IncomeUiState>(IncomeUiState.Idle)
    val uiState: StateFlow<IncomeUiState> = _uiState.asStateFlow()

    private val _selectedSource = MutableStateFlow("")
    val selectedSource: StateFlow<String> = _selectedSource.asStateFlow()

    private val _selectedCurrency = MutableStateFlow("LKR")
    val selectedCurrency: StateFlow<String> = _selectedCurrency.asStateFlow()

    private val _availableCurrencies = MutableStateFlow(listOf("LKR", "USD"))
    val availableCurrencies: StateFlow<List<String>> = _availableCurrencies.asStateFlow()

    private val _exchangeRate = MutableStateFlow(1.0)
    val exchangeRate = _exchangeRate.asStateFlow()

    private val _isFetchingRate = MutableStateFlow(false)
    val isFetchingRate = _isFetchingRate.asStateFlow()

    private val _totalLkrEstimate = MutableStateFlow(0.0)
    val totalLkrEstimate: StateFlow<Double> = _totalLkrEstimate.asStateFlow()

    private var currentAmountInput: String = ""

    fun onSourceSelected(source: String) {
        _selectedSource.value = source
        val currencies = sourceCurrencyMap[source] ?: listOf("LKR")
        _availableCurrencies.value = currencies
        onCurrencySelected(currencies.first())
    }

    fun onCurrencySelected(currency: String) {
        _selectedCurrency.value = currency
        if (currency == "LKR") {
            _exchangeRate.value = 1.0
            updateLiveAmount(currentAmountInput)
        } else {
            fetchLatestRate(currency.lowercase())
        }
    }

    fun updateExchangeRate(newRate: String) {
        val rate = newRate.toDoubleOrNull() ?: 0.0
        _exchangeRate.value = rate
        updateLiveAmount(currentAmountInput)
    }

    fun updateLiveAmount(amountStr: String) {
        currentAmountInput = amountStr
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        _totalLkrEstimate.value = amount * _exchangeRate.value
    }

    fun refreshRate() {
        val currency = _selectedCurrency.value
        if (currency != "LKR") {
            fetchLatestRate(currency.lowercase())
        }
    }

    private fun fetchLatestRate(base: String) {
        viewModelScope.launch {
            _isFetchingRate.value = true
            try {
                val normalizedBase = base.trim().lowercase()
                val response = apiService.getExchangeRates(normalizedBase)
                val innerMap = response[normalizedBase] as? Map<String, Any>
                val lkrRateValue = innerMap?.get("lkr")

                val lkrRate = when (lkrRateValue) {
                    is Number -> lkrRateValue.toDouble()
                    is String -> lkrRateValue.toDoubleOrNull()
                    else -> null
                }

                if (lkrRate != null && lkrRate > 0) {
                    _exchangeRate.value = lkrRate
                    updateLiveAmount(currentAmountInput)
                }
            } catch (ignored: Exception) { // Fix: Rename 'e' to 'ignored' or use it
                _uiState.value = IncomeUiState.Error("Live rate unavailable.")
            } finally {
                _isFetchingRate.value = false
            }
        }
    }

    fun addIncome(sourceType: String, projectName: String?, amountStr: String, currency: String, exchangeRate: Double, date: Long, notes: String?) {
        val amountInCents = (amountStr.toDoubleOrNull() ?: 0.0).times(100).toLong()
        if (amountInCents <= 0) return

        viewModelScope.launch {
            _uiState.value = IncomeUiState.Loading
            val lkrAmountInCents = (amountInCents * exchangeRate).toLong()
            val income = Income(
                id = UUID.randomUUID().toString(),
                sourceType = sourceType,
                projectName = projectName,
                originalAmount = amountInCents,
                originalCurrency = currency,
                lkrAmount = lkrAmountInCents,
                exchangeRate = exchangeRate,
                date = date,
                notes = notes,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            repository.addIncome(income)
            _uiState.value = IncomeUiState.Success
        }
    }

    fun resetState() { _uiState.value = IncomeUiState.Idle }
}