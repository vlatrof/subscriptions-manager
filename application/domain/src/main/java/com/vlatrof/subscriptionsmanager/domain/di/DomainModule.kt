package com.vlatrof.subscriptionsmanager.domain.di

import com.vlatrof.subscriptionsmanager.domain.usecases.DeleteSubscriptionByIdUseCase
import com.vlatrof.subscriptionsmanager.domain.usecases.GetAllSubscriptionsFlowUseCase
import com.vlatrof.subscriptionsmanager.domain.usecases.GetAllSubscriptionsUseCase
import com.vlatrof.subscriptionsmanager.domain.usecases.GetSubscriptionByIdUseCase
import com.vlatrof.subscriptionsmanager.domain.usecases.InsertSubscriptionUseCase
import com.vlatrof.subscriptionsmanager.domain.usecases.InsertSubscriptionsListUseCase
import com.vlatrof.subscriptionsmanager.domain.usecases.UpdateSubscriptionUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {

    @Binds
    abstract fun bindGetAllSubscriptionsFlowUseCase(
        implementation: GetAllSubscriptionsFlowUseCase.Base
    ): GetAllSubscriptionsFlowUseCase

    @Binds
    abstract fun bindGetAllSubscriptionsUseCase(
        implementation: GetAllSubscriptionsUseCase.Base
    ): GetAllSubscriptionsUseCase

    @Binds
    abstract fun bindGetSubscriptionByIdUseCase(
        implementation: GetSubscriptionByIdUseCase.Base
    ): GetSubscriptionByIdUseCase

    @Binds
    abstract fun bindInsertSubscriptionUseCase(
        implementation: InsertSubscriptionUseCase.Base
    ): InsertSubscriptionUseCase

    @Binds
    abstract fun InsertSubscriptionsListUseCase(
        implementation: InsertSubscriptionsListUseCase.Base
    ): InsertSubscriptionsListUseCase

    @Binds
    abstract fun bindUpdateSubscriptionUseCase(
        implementation: UpdateSubscriptionUseCase.Base
    ): UpdateSubscriptionUseCase

    @Binds
    abstract fun bindDeleteSubscriptionByIdUseCase(
        implementation: DeleteSubscriptionByIdUseCase.Base
    ): DeleteSubscriptionByIdUseCase
}

@Module
@InstallIn(SingletonComponent::class)
object CoroutineDispatchersModule {

    @DefaultDispatcher
    @Provides
    @Singleton
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @IoDispatcher
    @Provides
    @Singleton
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Provides
    @Singleton
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher
