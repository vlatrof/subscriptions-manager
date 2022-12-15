package com.vlatrof.subscriptionsmanager.presentation.screens.newsubscription

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.vlatrof.subscriptionsmanager.presentation.app.SubscriptionsManagerApp
import com.vlatrof.subscriptionsmanager.domain.di.IoDispatcher
import com.vlatrof.subscriptionsmanager.domain.usecases.InsertSubscriptionUseCase
import com.vlatrof.subscriptionsmanager.presentation.screens.common.BaseViewModel
import com.vlatrof.subscriptionsmanager.presentation.utils.AlertPeriodOptions
import com.vlatrof.subscriptionsmanager.presentation.utils.RenewalPeriodOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.NumberFormatException
import java.util.Currency
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class NewSubscriptionViewModel @Inject constructor(

    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val application: SubscriptionsManagerApp,
    private val insertSubscriptionUseCase: InsertSubscriptionUseCase

) : BaseViewModel() {

    private val availableCurrencies = Currency.getAvailableCurrencies()

    private val mutableNameInputState = MutableLiveData<InputState>(InputState.Initial)
    val nameInputState: LiveData<InputState> = mutableNameInputState

    private val mutableCostInputState = MutableLiveData<InputState>(InputState.Initial)
    val costInputState: LiveData<InputState> = mutableCostInputState

    private val mutableCurrencyInputState = MutableLiveData<InputState>(InputState.Initial)
    val currencyInputState: LiveData<InputState> = mutableCurrencyInputState

    private val mutableStartDateInputSelectionLiveData =
        MutableLiveData(MaterialDatePicker.todayInUtcMilliseconds())
    val startDateInputSelectionLiveData: LiveData<Long> = mutableStartDateInputSelectionLiveData

    private val mutableButtonCreateState = MutableLiveData(false)
    val buttonCreateState: LiveData<Boolean> = mutableButtonCreateState

    var currencyInputValue: String = ""

    var renewalPeriodInputValue = RenewalPeriodOptions(application.resources).default

    var alertPeriodInputValue = AlertPeriodOptions(application.resources).default

    init {
        currencyInputValue = application.getLastUsedCurrencyCode()
        mutableCurrencyInputState.value = validateCurrencyValue(currencyInputValue)
    }

    fun insertNewSubscription(
        subscription: com.vlatrof.subscriptionsmanager.domain.models.Subscription
    ) {
        viewModelScope.launch(ioDispatcher) {
            insertSubscriptionUseCase(subscription)
        }
        saveLastCurrencyCode(subscription.paymentCurrency.currencyCode)
    }

    fun handleNewNameInputValue(newValue: String) {
        mutableNameInputState.value = validateNameValue(newValue)
        updateCreateButtonState()
    }

    fun handleNewCostInputValue(newValue: String) {
        mutableCostInputState.value = validateCostValue(newValue)
        updateCreateButtonState()
    }

    fun handleNewCurrencyValue(newValue: String) {
        currencyInputValue = newValue
        mutableCurrencyInputState.value = validateCurrencyValue(newValue = newValue)
        updateCreateButtonState()
    }

    fun handleNewStartDateValue(newValue: Long) {
        mutableStartDateInputSelectionLiveData.value = newValue
    }

    fun handleNewRenewalPeriodValue(newValue: String) {
        renewalPeriodInputValue = newValue
    }

    fun handleNewAlertPeriodValue(newValue: String) {
        alertPeriodInputValue = newValue
    }

    private fun validateNameValue(newValue: String): InputState {
        return if (newValue.isEmpty()) InputState.Empty
        else if (newValue.isBlank()) InputState.Wrong
        else InputState.Correct
    }

    private fun validateCostValue(newValue: String): InputState {
        if (newValue.isEmpty()) return InputState.Empty

        try {
            newValue.toDouble()
        } catch (nfe: NumberFormatException) {
            return InputState.Wrong
        }

        return InputState.Correct
    }

    private fun validateCurrencyValue(newValue: String): InputState {
        if (newValue.isEmpty()) {
            return InputState.Empty
        }

        try {
            if (!availableCurrencies.contains(Currency.getInstance(newValue))) {
                return InputState.Wrong
            }
        } catch (exception: IllegalArgumentException) {
            return InputState.Wrong
        }

        return InputState.Correct
    }

    private fun updateCreateButtonState() {
        mutableButtonCreateState.value =
            nameInputState.value == InputState.Correct &&
            costInputState.value == InputState.Correct &&
            currencyInputState.value == InputState.Correct
    }

    private fun saveLastCurrencyCode(currencyCode: String) {
        application.saveLastCurrencyCode(currencyCode)
    }
}
