package com.vlatrof.subscriptionsmanager.data.local

import com.vlatrof.subscriptionsmanager.data.local.room.SubscriptionsDao
import com.vlatrof.subscriptionsmanager.data.models.Subscription as DataSubscription
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class SubscriptionsLocalDataSource @Inject constructor(
    
    private val subscriptionsDao: SubscriptionsDao
    
) {

    val allSubscriptionsFlow: Flow<List<DataSubscription>> =
        subscriptionsDao.getAllFlow().map { subscriptionsEntitiesList ->
            mutableListOf<DataSubscription>().apply {
                subscriptionsEntitiesList.forEach { subscriptionEntity ->
                    this.add(DataSubscription(subscriptionEntity))
                }
            }
        }

    suspend fun getSubscriptionById(id: Int): DataSubscription {
        return DataSubscription(subscriptionsDao.getById(id))
    }

    suspend fun insertSubscription(subscription: DataSubscription) {
        subscriptionsDao.insert(subscription.toSubscriptionEntity())
    }

    suspend fun deleteAllSubscriptions() {
        subscriptionsDao.deleteAll()
    }

    suspend fun deleteSubscriptionById(id: Int) {
        subscriptionsDao.deleteById(id)
    }

    suspend fun updateSubscription(subscription: DataSubscription) {
        subscriptionsDao.update(subscription.toSubscriptionEntity())
    }
}
