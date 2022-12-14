package com.vlatrof.subscriptionsmanager.domain.usecases

import com.vlatrof.subscriptionsmanager.domain.di.IoDispatcher
import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import com.vlatrof.subscriptionsmanager.domain.repositories.SubscriptionsRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface GetSubscriptionByIdUseCase {

    suspend operator fun invoke(id: Int): Subscription

    class Base @Inject constructor(

        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
        private val subscriptionsRepository: SubscriptionsRepository

    ) : GetSubscriptionByIdUseCase {

        override suspend fun invoke(id: Int) = withContext(ioDispatcher) {
            subscriptionsRepository.getSubscriptionById(id)
        }
    }
}
