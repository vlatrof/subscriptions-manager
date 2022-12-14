package com.vlatrof.subscriptionsmanager.data.local.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionsDao {

    @Query("SELECT * FROM subscriptions")
    suspend fun getAll(): List<SubscriptionEntity>

    @Query("SELECT * FROM subscriptions")
    fun getAllFlow(): Flow<List<SubscriptionEntity>>

    @Query("SELECT * FROM subscriptions WHERE id=:id ")
    suspend fun getById(id: Int): SubscriptionEntity

    @Query("DELETE FROM subscriptions")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(subscription: SubscriptionEntity)

    @Query("DELETE FROM subscriptions WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Update
    suspend fun update(subscription: SubscriptionEntity)
}
