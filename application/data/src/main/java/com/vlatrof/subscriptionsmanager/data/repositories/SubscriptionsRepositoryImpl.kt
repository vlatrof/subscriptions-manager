package com.vlatrof.subscriptionsmanager.data.repositories

import com.vlatrof.subscriptionsmanager.data.local.SubscriptionsLocalDataSource
import com.vlatrof.subscriptionsmanager.data.models.Subscription as DataSubscription
import com.vlatrof.subscriptionsmanager.domain.models.Subscription as DomainSubscription
import com.vlatrof.subscriptionsmanager.domain.repositories.SubscriptionsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class SubscriptionsRepositoryImpl @Inject constructor(

    private val localDataSource: SubscriptionsLocalDataSource

) : SubscriptionsRepository {

    override val allFlow: Flow<List<DomainSubscription>> =
        localDataSource.allFlow.map { dataSubscriptionsList ->
            mutableListOf<DomainSubscription>().apply {
                dataSubscriptionsList.forEach { dataSubscription ->
                    this.add(dataSubscription.toDomainSubscription())
                }
            }
        }

    override suspend fun getById(id: Int): DomainSubscription =
        localDataSource.getById(id).toDomainSubscription()

    override suspend fun getAll(): List<DomainSubscription> =
        localDataSource.getAll().map { dataSubscription ->
            dataSubscription.toDomainSubscription()
        }

    override suspend fun insert(subscription: DomainSubscription) =
        localDataSource.insert(DataSubscription(subscription))

    override suspend fun insertList(subscriptions: List<DomainSubscription>) =
        localDataSource.insertList(
            subscriptions.map { domainSubscription ->
                DataSubscription(domainSubscription)
            }
        )

    override suspend fun deleteAll() = localDataSource.deleteAll()

    override suspend fun delete(id: Int) = localDataSource.deleteById(id)

    override suspend fun update(subscription: DomainSubscription) =
        localDataSource.update(DataSubscription(subscription))

}
