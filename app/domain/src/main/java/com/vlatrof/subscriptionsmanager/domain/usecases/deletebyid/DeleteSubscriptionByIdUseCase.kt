package com.vlatrof.subscriptionsmanager.domain.usecases.deletebyid

interface DeleteSubscriptionByIdUseCase {

    suspend operator fun invoke(id: Int)
}
