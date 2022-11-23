package com.vlatrof.subscriptionsmanager.domain.usecases.getbyid

import com.vlatrof.subscriptionsmanager.domain.models.Subscription

interface GetSubscriptionByIdUseCase {

    suspend operator fun invoke(id: Int): Subscription
}
