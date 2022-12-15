package com.vlatrof.subscriptionsmanager.presentation.app

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

// Implementing Configuration.Provider interface for WorkManager Injecting with Hilt
@HiltAndroidApp
class SubscriptionsManagerApp : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        Configuration.Builder().setWorkerFactory(workerFactory).build()

    override fun onCreate() {
        super.onCreate()

        // apply current night mode on app start
        applyNightMode(
            getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
                .getInt(NIGHT_MODE, DEFAULT_NIGHT_MODE)
        )
    }

    fun applyNightMode(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
    }

    fun saveNightMode(mode: Int) {
        getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putInt(NIGHT_MODE, mode)
            .apply()
    }

    fun getCurrentNightMode(): Int {
        return getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
            .getInt(NIGHT_MODE, -1)
    }

    fun saveLastCurrencyCode(currencyCode: String) {
        getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
            .edit()
            .putString(LAST_CURRENCY_CODE, currencyCode)
            .apply()
    }

    fun getLastUsedCurrencyCode(): String {
        return getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
            .getString(LAST_CURRENCY_CODE, DEFAULT_CURRENCY_CODE)!!
    }

    companion object {

        // shared preferences keys
        const val APP_PREFERENCES = "APP_PREFERENCES"
        const val NIGHT_MODE = "NIGHT_MODE"
        const val LAST_CURRENCY_CODE = "LAST_CURRENCY_CODE"

        // shared preferences default values
        const val DEFAULT_NIGHT_MODE = MODE_NIGHT_FOLLOW_SYSTEM
        const val DEFAULT_CURRENCY_CODE = "USD"
    }
}
