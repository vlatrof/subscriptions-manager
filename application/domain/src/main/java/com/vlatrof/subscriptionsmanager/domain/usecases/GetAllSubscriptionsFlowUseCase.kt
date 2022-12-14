package com.vlatrof.subscriptionsmanager.domain.usecases

import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import com.vlatrof.subscriptionsmanager.domain.repositories.SubscriptionsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

interface GetAllSubscriptionsFlowUseCase {

    operator fun invoke(): Flow<List<Subscription>>

    class Base @Inject constructor(

        private val subscriptionsRepository: SubscriptionsRepository

    ) : GetAllSubscriptionsFlowUseCase {

        override fun invoke() = subscriptionsRepository.allSubscriptionsFlow
    }
}
