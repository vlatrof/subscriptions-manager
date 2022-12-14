package com.vlatrof.subscriptionsmanager.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.vlatrof.subscriptionsmanager.R.layout.activity_main
import com.vlatrof.subscriptionsmanager.app.notification.SubscriptionsAlertsHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContentView(activity_main)

        // relaunch subscriptions renewal alerts worker
        SubscriptionsAlertsHelper(applicationContext).launchAlertsWorker()
    }
}
