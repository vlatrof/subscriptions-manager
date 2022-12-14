package com.vlatrof.subscriptionsmanager.app.utils

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun parseLocalDateFromUTCMilliseconds(
    millis: Long,
    timeZone: ZoneId = ZoneId.systemDefault()
): LocalDate {
    return Instant.ofEpochMilli(millis).atZone(timeZone).toLocalDate()
}
