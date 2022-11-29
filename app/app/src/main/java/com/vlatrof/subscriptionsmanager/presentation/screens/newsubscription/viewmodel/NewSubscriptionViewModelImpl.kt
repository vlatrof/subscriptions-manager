package com.vlatrof.subscriptionsmanager.presentation.screens.newsubscription.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.vlatrof.subscriptionsmanager.app.App
import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import com.vlatrof.subscriptionsmanager.domain.usecases.insertnew.InsertNewSubscriptionUseCase
import com.vlatrof.subscriptionsmanager.utils.AlertPeriodOptions
import com.vlatrof.subscriptionsmanager.utils.RenewalPeriodOptions
import java.lang.NumberFormatException
import java.util.Currency
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NewSubscriptionViewModelImpl(

    private val application: App,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val insertNewSubscriptionUseCase: InsertNewSubscriptionUseCase

) : NewSubscriptionViewModel() {

    override val nameInputState = MutableLiveData<InputState>(InputState.Initial)

    override val costInputState = MutableLiveData<InputState>(InputState.Initial)

    override val currencyInputState = MutableLiveData<InputState>(InputState.Initial)

    override val startDateInputSelectionLiveData =
        MutableLiveData(MaterialDatePicker.todayInUtcMilliseconds())

    override val buttonCreateState = MutableLiveData(false)

    override var currencyInputValue: String = ""

    override var renewalPeriodInputValue = RenewalPeriodOptions(application.resources).default

    override var alertPeriodInputValue = AlertPeriodOptions(application.resources).default

    private val availableCurrencies = Currency.getAvailableCurrencies()

    init {
        currencyInputValue = getLastCurrencyCode()
        validateCurrencyValue(currencyInputValue)
    }

    override fun insertNewSubscription(subscription: Subscription) {
        viewModelScope.launch(ioDispatcher) {
            insertNewSubscriptionUseCase(subscription)
        }
        saveLastCurrencyCode(subscription.paymentCurrency.currencyCode)
    }

    override fun handleNewNameInputValue(newValue: String) {
        nameInputState.value = validateNameValue(newValue)
        updateCreateButtonState()
    }

    override fun handleNewCostInputValue(newValue: String) {
        costInputState.value = validateCostValue(newValue)
        updateCreateButtonState()
    }

    override fun handleNewCurrencyValue(newValue: String) {
        currencyInputValue = newValue
        currencyInputState.value = validateCurrencyValue(newValue = newValue)
        updateCreateButtonState()
    }

    override fun handleNewStartDateValue(newValue: Long) {
        startDateInputSelectionLiveData.value = newValue
    }

    override fun handleNewRenewalPeriodValue(newValue: String) {
        renewalPeriodInputValue = newValue
    }

    override fun handleNewAlertPeriodValue(newValue: String) {
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
        buttonCreateState.value =
            nameInputState.value == InputState.Correct &&
            costInputState.value == InputState.Correct &&
            currencyInputState.value == InputState.Correct
    }

    private fun getLastCurrencyCode(): String {
        return application.getLastCurrencyCode()
    }

    private fun saveLastCurrencyCode(currencyCode: String) {
        application.saveLastCurrencyCode(currencyCode)
    }

}
