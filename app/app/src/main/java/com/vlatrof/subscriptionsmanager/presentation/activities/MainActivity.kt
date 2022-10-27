package com.vlatrof.subscriptionsmanager.presentation.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.vlatrof.subscriptionsmanager.R.layout.activity_main
import com.vlatrof.subscriptionsmanager.presentation.utils.notification.SubscriptionsAlertsHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(activity_main)

        // launch subscriptions renewal alerts worker
        SubscriptionsAlertsHelper(applicationContext).launchAlertsWorker()
    }

}