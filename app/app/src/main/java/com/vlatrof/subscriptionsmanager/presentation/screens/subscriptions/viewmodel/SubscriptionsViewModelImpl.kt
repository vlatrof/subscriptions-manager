package com.vlatrof.subscriptionsmanager.presentation.screens.subscriptions.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import com.vlatrof.subscriptionsmanager.domain.usecases.getallflow.GetAllSubscriptionsFlowUseCase

class SubscriptionsViewModelImpl(

    getAllSubscriptionsFlowUseCase: GetAllSubscriptionsFlowUseCase

) : SubscriptionsViewModel() {

    override val subscriptionsLiveData: LiveData<List<Subscription>> =
        getAllSubscriptionsFlowUseCase().asLiveData()
}
