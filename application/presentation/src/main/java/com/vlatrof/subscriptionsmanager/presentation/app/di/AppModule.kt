package com.vlatrof.subscriptionsmanager.presentation.app.di

import android.content.Context
import com.vlatrof.subscriptionsmanager.presentation.app.SubscriptionsManagerApp
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
    ): SubscriptionsManagerApp = applicationContext as SubscriptionsManagerApp
}
