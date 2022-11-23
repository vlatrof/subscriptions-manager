package com.vlatrof.subscriptionsmanager.domain.usecases.insertnew

import com.vlatrof.subscriptionsmanager.domain.models.Subscription

interface InsertNewSubscriptionUseCase {

    suspend operator fun invoke(subscription: Subscription)
}
