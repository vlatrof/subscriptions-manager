package com.vlatrof.subscriptionsmanager.domain.usecases.getbyid

import com.vlatrof.subscriptionsmanager.domain.repositories.SubscriptionsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetSubscriptionByIdUseCaseImpl(

    private val subscriptionsRepository: SubscriptionsRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

) : GetSubscriptionByIdUseCase {

    override suspend fun invoke(id: Int) = withContext(ioDispatcher) {
        subscriptionsRepository.getSubscriptionById(id)
    }
}
