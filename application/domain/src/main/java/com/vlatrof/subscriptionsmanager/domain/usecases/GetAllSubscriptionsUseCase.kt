package com.vlatrof.subscriptionsmanager.domain.usecases

import com.vlatrof.subscriptionsmanager.domain.di.IoDispatcher
import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import com.vlatrof.subscriptionsmanager.domain.repositories.SubscriptionsRepository
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

interface GetAllSubscriptionsUseCase {

    suspend operator fun invoke(): List<Subscription>

    class Base @Inject constructor(

        @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
        private val subscriptionsRepository: SubscriptionsRepository

    ) : GetAllSubscriptionsUseCase {

        override suspend fun invoke() = withContext(ioDispatcher) {
            subscriptionsRepository.getAll()
        }
    }
}
