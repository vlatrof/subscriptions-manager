package com.vlatrof.subscriptionsmanager.data.di

import android.content.Context
import com.vlatrof.subscriptionsmanager.data.local.SubscriptionsLocalDataSource
import com.vlatrof.subscriptionsmanager.data.local.room.SubscriptionsDao
import com.vlatrof.subscriptionsmanager.data.local.room.SubscriptionsRoomDatabase
import com.vlatrof.subscriptionsmanager.data.repositories.SubscriptionsRepositoryImpl
import com.vlatrof.subscriptionsmanager.domain.repositories.SubscriptionsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {
    
    @Provides
    @Singleton
    fun provideSubscriptionRoomDatabase(
        @ApplicationContext context: Context
    ): SubscriptionsRoomDatabase = SubscriptionsRoomDatabase.getDatabase(context = context)
    
    @Provides
    @Singleton
    fun provideSubscriptionsDao(
        database: SubscriptionsRoomDatabase
    ): SubscriptionsDao = database.subscriptionsDao
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoriesModule {

    @Binds
    @Singleton
    abstract fun bindSubscriptionsRepository(
        implementation: SubscriptionsRepositoryImpl
    ): com.vlatrof.subscriptionsmanager.domain.repositories.SubscriptionsRepository
}

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourcesModule {

    @Binds
    @Singleton
    abstract fun bindSubscriptionsLocalDataSource(
        implementation: SubscriptionsLocalDataSource.Base
    ): SubscriptionsLocalDataSource
}