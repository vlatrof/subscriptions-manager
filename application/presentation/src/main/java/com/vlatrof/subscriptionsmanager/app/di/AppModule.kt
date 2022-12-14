package com.vlatrof.subscriptionsmanager.app.di

import android.content.Context
import com.vlatrof.subscriptionsmanager.app.SubscriptionsManagerApplication
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideSubscriptionsManagerApplication(
        @ApplicationContext applicationContext: Context
    ): SubscriptionsManagerApplication = applicationContext as SubscriptionsManagerApplication
}
