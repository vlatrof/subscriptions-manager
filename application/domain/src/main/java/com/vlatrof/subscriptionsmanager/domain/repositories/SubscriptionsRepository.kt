package com.vlatrof.subscriptionsmanager.domain.repositories

import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import kotlinx.coroutines.flow.Flow

interface SubscriptionsRepository {

    val allFlow: Flow<List<Subscription>>

    suspend fun getById(id: Int): Subscription

    suspend fun getAll(): List<Subscription>

    suspend fun insert(subscription: Subscription)

    suspend fun insertList(subscriptions: List<Subscription>)

    suspend fun update(subscription: Subscription)

    suspend fun delete(id: Int)

    suspend fun deleteAll()
}
