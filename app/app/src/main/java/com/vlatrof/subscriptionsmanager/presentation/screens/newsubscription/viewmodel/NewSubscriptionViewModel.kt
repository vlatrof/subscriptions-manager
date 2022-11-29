package com.vlatrof.subscriptionsmanager.presentation.screens.newsubscription.viewmodel

import androidx.lifecycle.LiveData
import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import com.vlatrof.subscriptionsmanager.presentation.screens.common.BaseViewModel

abstract class NewSubscriptionViewModel : BaseViewModel() {

    abstract val nameInputState: LiveData<InputState>

    abstract val costInputState: LiveData<InputState>

    abstract val currencyInputState: LiveData<InputState>

    abstract val startDateInputSelectionLiveData: LiveData<Long>

    abstract val buttonCreateState: LiveData<Boolean>

    abstract var currencyInputValue: String

    abstract var renewalPeriodInputValue: String

    abstract var alertPeriodInputValue: String

    abstract fun insertNewSubscription(subscription: Subscription)

    abstract fun handleNewNameInputValue(newValue: String)

    abstract fun handleNewCostInputValue(newValue: String)

    abstract fun handleNewCurrencyValue(newValue: String)

    abstract fun handleNewStartDateValue(newValue: Long)

    abstract fun handleNewRenewalPeriodValue(newValue: String)

    abstract fun handleNewAlertPeriodValue(newValue: String)

}
