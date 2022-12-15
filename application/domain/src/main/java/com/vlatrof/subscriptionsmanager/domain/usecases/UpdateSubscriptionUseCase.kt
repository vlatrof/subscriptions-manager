package com.vlatrof.subscriptionsmanager.domain.usecases

import com.vlatrof.subscriptionsmanager.domain.di.IoDispatcher
import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import com.vlatrof.subscriptionsmanager.domain.repositories.SubscriptionsRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface UpdateSubscriptionUseCase {

    suspend operator fun invoke(subscription: Subscription)

    class Base @Inject constructor(

        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
        private val subscriptionsRepository: SubscriptionsRepository

    ) : UpdateSubscriptionUseCase {

        override suspend fun invoke(subscription: Subscription) = withContext(ioDispatcher) {
            subscriptionsRepository.update(subscription)
        }
    }
}
