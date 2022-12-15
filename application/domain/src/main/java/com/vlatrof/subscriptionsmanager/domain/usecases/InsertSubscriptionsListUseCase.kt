package com.vlatrof.subscriptionsmanager.domain.usecases

import com.vlatrof.subscriptionsmanager.domain.di.IoDispatcher
import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import com.vlatrof.subscriptionsmanager.domain.repositories.SubscriptionsRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface InsertSubscriptionsListUseCase {

    suspend operator fun invoke(subscriptions: List<Subscription>)

    class Base @Inject constructor(

        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
        private val subscriptionsRepository: SubscriptionsRepository

    ) : InsertSubscriptionsListUseCase {

        override suspend fun invoke(subscriptions: List<Subscription>) = withContext(ioDispatcher) {
            subscriptionsRepository.insertList(subscriptions)
        }
    }
}
