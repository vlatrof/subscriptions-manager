package com.vlatrof.subscriptionsmanager.domain.usecases.update

import com.vlatrof.subscriptionsmanager.domain.models.Subscription

interface UpdateSubscriptionUseCase {

    suspend operator fun invoke(subscription: Subscription)
}
