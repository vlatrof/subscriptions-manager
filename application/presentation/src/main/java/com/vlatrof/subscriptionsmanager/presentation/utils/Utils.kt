package com.vlatrof.subscriptionsmanager.presentation.utils

import android.content.res.Resources
import com.vlatrof.subscriptionsmanager.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class RenewalPeriodOptions(resources: Resources) {

    val options = linkedMapOf(
        "P1D" to resources.getString(R.string.option_renewal_period_daily),
        "P7D" to resources.getString(R.string.option_renewal_period_weekly),
        "P14D" to resources.getString(R.string.option_renewal_period_every_two_weeks),
        "P1M" to resources.getString(R.string.option_renewal_period_monthly),
        "P3M" to resources.getString(R.string.option_renewal_period_every_three_months),
        "P6M" to resources.getString(R.string.option_renewal_period_every_six_months),
        "P1Y" to resources.getString(R.string.option_renewal_period_yearly)
    )

    val default = options["P1M"]!!
}

class AlertPeriodOptions(resources: Resources) {

    val options = linkedMapOf(
        "NONE" to resources.getString(R.string.option_alert_period_none),
        "P0D" to resources.getString(R.string.option_alert_period_same_day),
        "P-1D" to resources.getString(R.string.option_alert_period_one_day_before),
        "P-2D" to resources.getString(R.string.option_alert_period_two_days_before),
        "P-7D" to resources.getString(R.string.option_alert_period_one_week_before)
    )

    val default = options["NONE"]!!
}

fun parseLocalDateFromUTCMilliseconds(
    millis: Long,
    timeZone: ZoneId = ZoneId.systemDefault()
): LocalDate {
    return Instant.ofEpochMilli(millis).atZone(timeZone).toLocalDate()
}
