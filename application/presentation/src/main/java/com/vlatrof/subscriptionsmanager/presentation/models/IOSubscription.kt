package com.vlatrof.subscriptionsmanager.presentation.models

import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import java.time.LocalDate
import java.time.Period
import java.util.Currency
import kotlinx.serialization.Serializable

@Serializable
data class IOSubscription(

    val name: String = "",
    val description: String = "",
    val paymentCost: Double = 0.0,
    val paymentCurrencyCode: String = "",
    val startDate: String = "",
    val renewalPeriod: String = "",
    val alertEnabled: Boolean = false,
    val alertPeriod: String = ""
    
) {

    constructor(domainSubscription: Subscription) : this (

        name = domainSubscription.name,
        description = domainSubscription.description,
        paymentCost = domainSubscription.paymentCost,
        paymentCurrencyCode = domainSubscription.paymentCurrency.currencyCode,
        startDate = domainSubscription.startDate.toString(),
        renewalPeriod = domainSubscription.renewalPeriod.toString(),
        alertEnabled = domainSubscription.alertEnabled,
        alertPeriod = domainSubscription.alertPeriod.toString()

    )

    fun toDomainSubscription(): Subscription {
        // calculate next renewal date
        val currentDate = LocalDate.now()
        val startDate = LocalDate.parse(this.startDate)
        val renewalPeriod = Period.parse(this.renewalPeriod)
        var nextRenewalDate: LocalDate = LocalDate.from(startDate)
        while (nextRenewalDate < currentDate) {
            nextRenewalDate = renewalPeriod.addTo(nextRenewalDate) as LocalDate
        }

        return Subscription(
            name = name,
            description = description,
            paymentCost = paymentCost,
            paymentCurrency = Currency.getInstance(paymentCurrencyCode),
            startDate = startDate,
            renewalPeriod = renewalPeriod,
            nextRenewalDate = nextRenewalDate,
            alertEnabled = alertEnabled,
            alertPeriod = Period.parse(alertPeriod)
        )
    }
}
