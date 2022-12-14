package com.vlatrof.subscriptionsmanager.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Annotates class to be a Room Database with a table (entity) of the Subscription class
@Database(entities = [SubscriptionEntity::class], version = 1, exportSchema = false)
abstract class SubscriptionsRoomDatabase : RoomDatabase() {

    abstract val subscriptionsDao: SubscriptionsDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: SubscriptionsRoomDatabase? = null

        fun getDatabase(context: Context): SubscriptionsRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SubscriptionsRoomDatabase::class.java,
                    "subscriptions_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
