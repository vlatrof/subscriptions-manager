package com.vlatrof.subscriptionsmanager.presentation.screens.options.viewmodel

import android.net.Uri
import com.vlatrof.subscriptionsmanager.presentation.screens.common.BaseViewModel

abstract class OptionsViewModel : BaseViewModel() {

    abstract fun getCurrentNightMode(): Int

    abstract fun applyNightMode(mode: Int)

    abstract fun exportSubscriptions(directoryUri: Uri?)

    abstract fun importSubscriptions(contentUri: Uri?)
}
