package com.vlatrof.subscriptionsmanager.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vlatrof.subscriptionsmanager.data.local.room.entities.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionsDao {

    @Query("SELECT * FROM subscriptions")
    fun getAll(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE id=:id ")
    fun getById(id: Int): SubscriptionEntity
//    fun getById(id: Int): Flow<SubscriptionEntity>

    @Query("DELETE FROM subscriptions")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(subscription: SubscriptionEntity)

}