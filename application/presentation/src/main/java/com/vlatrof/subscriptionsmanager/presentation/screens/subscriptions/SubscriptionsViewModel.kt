package com.vlatrof.subscriptionsmanager.presentation.screens.subscriptions

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import com.vlatrof.subscriptionsmanager.domain.usecases.GetAllSubscriptionsFlowUseCase
import com.vlatrof.subscriptionsmanager.presentation.screens.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    
    getAllSubscriptionsFlowUseCase: GetAllSubscriptionsFlowUseCase

) : BaseViewModel() {

    val subscriptionsLiveData: LiveData<List<Subscription>> =
        getAllSubscriptionsFlowUseCase().asLiveData()
}
