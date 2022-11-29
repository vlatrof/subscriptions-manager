package com.vlatrof.subscriptionsmanager.presentation.screens.subscriptions.viewmodel

import androidx.lifecycle.LiveData
import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import com.vlatrof.subscriptionsmanager.presentation.screens.common.BaseViewModel

abstract class SubscriptionsViewModel : BaseViewModel() {
    
    abstract val subscriptionsLiveData: LiveData<List<Subscription>>
}
