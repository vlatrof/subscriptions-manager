package com.vlatrof.subscriptionsmanager.domain.di

import com.vlatrof.subscriptionsmanager.domain.usecases.DeleteSubscriptionByIdUseCase
import com.vlatrof.subscriptionsmanager.domain.usecases.GetAllSubscriptionsFlowUseCase
import com.vlatrof.subscriptionsmanager.domain.usecases.GetSubscriptionByIdUseCase
import com.vlatrof.subscriptionsmanager.domain.usecases.InsertSubscriptionUseCase
import com.vlatrof.subscriptionsmanager.domain.usecases.UpdateSubscriptionUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCasesModule {

    @Binds
    abstract fun bindDeleteSubscriptionByIdUseCase(
        implementation: DeleteSubscriptionByIdUseCase.Base
    ): DeleteSubscriptionByIdUseCase

    @Binds
    abstract fun bindGetAllSubscriptionsFlowUseCase(
        implementation: GetAllSubscriptionsFlowUseCase.Base
    ): GetAllSubscriptionsFlowUseCase

    @Binds
    abstract fun bindGetSubscriptionByIdUseCase(
        implementation: GetSubscriptionByIdUseCase.Base
    ): GetSubscriptionByIdUseCase

    @Binds
    abstract fun bindInsertSubscriptionUseCase(
        implementation: InsertSubscriptionUseCase.Base
    ): InsertSubscriptionUseCase

    @Binds
    abstract fun bindUpdateSubscriptionUseCase(
        implementation: UpdateSubscriptionUseCase.Base
    ): UpdateSubscriptionUseCase
}
