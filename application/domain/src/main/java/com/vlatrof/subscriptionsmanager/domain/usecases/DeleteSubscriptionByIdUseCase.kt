package com.vlatrof.subscriptionsmanager.domain.usecases

import com.vlatrof.subscriptionsmanager.domain.di.IoDispatcher
import com.vlatrof.subscriptionsmanager.domain.repositories.SubscriptionsRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface DeleteSubscriptionByIdUseCase {

    suspend operator fun invoke(id: Int)

    class Base @Inject constructor(

        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
        private val subscriptionsRepository: SubscriptionsRepository

    ) : DeleteSubscriptionByIdUseCase {

        override suspend fun invoke(id: Int) = withContext(ioDispatcher) {
            subscriptionsRepository.deleteSubscriptionById(id)
        }
    }
}
