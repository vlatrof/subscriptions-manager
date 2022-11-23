package com.vlatrof.subscriptionsmanager.domain.repositories

import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import kotlinx.coroutines.flow.Flow

interface SubscriptionsRepository {

    val allSubscriptionsFlow: Flow<List<Subscription>>

    suspend fun getSubscriptionById(id: Int): Subscription

    suspend fun insertSubscription(subscription: Subscription)

    suspend fun deleteAllSubscriptions()

    suspend fun deleteSubscriptionById(id: Int)

    suspend fun updateSubscription(subscription: Subscription)
}
