package com.vlatrof.subscriptionsmanager.data.local

import com.vlatrof.subscriptionsmanager.data.local.room.SubscriptionsDao
import com.vlatrof.subscriptionsmanager.data.models.Subscription
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
interface SubscriptionsLocalDataSource {

    val allSubscriptionsFlow: Flow<List<Subscription>>

    suspend fun getSubscriptionById(id: Int): Subscription

    suspend fun insertSubscription(subscription: Subscription)

    suspend fun deleteAllSubscriptions()

    suspend fun deleteSubscriptionById(id: Int)

    suspend fun updateSubscription(subscription: Subscription)

    class Base @Inject constructor(

        private val subscriptionsDao: SubscriptionsDao

    ) : SubscriptionsLocalDataSource {

        override val allSubscriptionsFlow: Flow<List<Subscription>> =
            subscriptionsDao.getAllFlow().map { subscriptionsEntitiesList ->
                mutableListOf<Subscription>().apply {
                    subscriptionsEntitiesList.forEach { subscriptionEntity ->
                        this.add(Subscription(subscriptionEntity))
                    }
                }
            }

        override suspend fun getSubscriptionById(id: Int): Subscription {
            return Subscription(subscriptionsDao.getById(id))
        }

        override suspend fun insertSubscription(subscription: Subscription) {
            subscriptionsDao.insert(subscription.toSubscriptionEntity())
        }

        override suspend fun deleteAllSubscriptions() {
            subscriptionsDao.deleteAll()
        }

        override suspend fun deleteSubscriptionById(id: Int) {
            subscriptionsDao.deleteById(id)
        }

        override suspend fun updateSubscription(subscription: Subscription) {
            subscriptionsDao.update(subscription.toSubscriptionEntity())
        }
    }
}