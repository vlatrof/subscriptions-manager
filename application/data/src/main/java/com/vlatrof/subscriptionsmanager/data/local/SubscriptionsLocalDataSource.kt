package com.vlatrof.subscriptionsmanager.data.local

import com.vlatrof.subscriptionsmanager.data.local.room.SubscriptionsDao
import com.vlatrof.subscriptionsmanager.data.models.Subscription
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
interface SubscriptionsLocalDataSource {

    val allFlow: Flow<List<Subscription>>
    
    suspend fun getAll(): List<Subscription>
    
    suspend fun getById(id: Int): Subscription

    suspend fun insert(subscription: Subscription)
    
    suspend fun insertList(subscriptions: List<Subscription>)

    suspend fun update(subscription: Subscription)

    suspend fun deleteById(id: Int)

    suspend fun deleteAll()

    class Base @Inject constructor(

        private val subscriptionsDao: SubscriptionsDao

    ) : SubscriptionsLocalDataSource {

        override val allFlow: Flow<List<Subscription>> =
            subscriptionsDao.getAllFlow().map { subscriptionsEntitiesList ->
                mutableListOf<Subscription>().apply {
                    subscriptionsEntitiesList.forEach { subscriptionEntity ->
                        this.add(Subscription(subscriptionEntity))
                    }
                }
            }

        override suspend fun getById(id: Int): Subscription =
            Subscription(subscriptionsDao.getById(id))

        override suspend fun getAll(): List<Subscription> =
            subscriptionsDao.getAll().map { entity ->
                Subscription(entity)
            }

        override suspend fun insert(subscription: Subscription) =
            subscriptionsDao.insert(subscription.toSubscriptionEntity())

        override suspend fun insertList(subscriptions: List<Subscription>) =
            subscriptionsDao.insertList(
                subscriptions.map { dataSubscription ->
                    dataSubscription.toSubscriptionEntity()
                }
            )

        override suspend fun deleteAll() = subscriptionsDao.deleteAll()

        override suspend fun deleteById(id: Int) = subscriptionsDao.deleteById(id)

        override suspend fun update(subscription: Subscription) =
            subscriptionsDao.update(subscription.toSubscriptionEntity())
    }
}
