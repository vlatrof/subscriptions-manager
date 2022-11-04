package com.vlatrof.subscriptionsmanager.presentation.screens.newsubscription

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.vlatrof.subscriptionsmanager.app.App
import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import com.vlatrof.subscriptionsmanager.domain.usecases.insertnew.InsertNewSubscriptionUseCase
import com.vlatrof.subscriptionsmanager.presentation.screens.base.BaseViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.NumberFormatException
import java.util.Currency

class NewSubscriptionViewModel(

    private val application: App,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val insertNewSubscriptionUseCase: InsertNewSubscriptionUseCase

) : BaseViewModel() {

    // value holders
    var currencyInputValue = ""
    var renewalPeriodInputSelection = ""
    var alertInputSelection = ""
    val startDateInputSelection = MutableLiveData(
        MaterialDatePicker.todayInUtcMilliseconds()
    )

    // state holders
    val nameInputState = MutableLiveData(InputState.INITIAL)
    val costInputState = MutableLiveData(InputState.INITIAL)
    val currencyInputState = MutableLiveData(InputState.INITIAL)
    val buttonCreateState = MutableLiveData(false)

    private val availableCurrencies = Currency.getAvailableCurrencies()

    fun validateNameInput(newValue: String) {
        nameInputState.value =
            if (newValue.isEmpty()) InputState.EMPTY
            else if (newValue.isBlank()) InputState.WRONG
            else InputState.CORRECT
    }

    fun validateCostInput(newValue: String) {
        if (newValue.isEmpty()) {
            costInputState.value = InputState.EMPTY
            return
        }

        try {
            newValue.toDouble()
        } catch (nfe: NumberFormatException) {
            costInputState.value = InputState.WRONG
            return
        }

        costInputState.value = InputState.CORRECT
    }

    fun validateCurrencyInput(newValue: String) {
        if (newValue.isEmpty()) {
            currencyInputState.value = InputState.EMPTY
            return
        }

        try {
            if (!availableCurrencies.contains(Currency.getInstance(newValue))) {
                currencyInputState.value = InputState.WRONG
                return
            }
        } catch (exception: IllegalArgumentException) {
            currencyInputState.value = InputState.WRONG
            return
        }

        currencyInputState.value = InputState.CORRECT
    }

    fun updateCreateButtonState() {
        buttonCreateState.value =
            nameInputState.value == InputState.CORRECT &&
            costInputState.value == InputState.CORRECT &&
            currencyInputState.value == InputState.CORRECT
    }

    fun insertNewSubscription(subscription: Subscription) {
        viewModelScope.launch(ioDispatcher) {
            insertNewSubscriptionUseCase(subscription)
        }
    }

    fun getLastCurrencyCode(): String {
        return application.getLastCurrencyCode()
    }

    fun saveLastCurrencyCode(currencyCode: String) {
        application.saveLastCurrencyCode(currencyCode)
    }
}