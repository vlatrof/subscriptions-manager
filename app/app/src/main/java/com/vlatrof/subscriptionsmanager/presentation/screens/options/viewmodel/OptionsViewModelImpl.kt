package com.vlatrof.subscriptionsmanager.presentation.screens.options.viewmodel

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewModelScope
import com.vlatrof.subscriptionsmanager.app.App
import com.vlatrof.subscriptionsmanager.data.local.room.database.SubscriptionsRoomDatabase
import com.vlatrof.subscriptionsmanager.data.local.room.entities.SubscriptionEntity
import java.io.FileNotFoundException
import java.io.IOException
import java.util.Scanner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class OptionsViewModelImpl(

    private val application: App,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

) : OptionsViewModel() {

    override fun getCurrentNightMode() = application.getCurrentNightMode()

    override fun applyNightMode(mode: Int) = application.applyNightMode(mode)

    override fun exportSubscriptions(directoryUri: Uri?) {
        if (directoryUri == null) {
            return // User didn't choose any directory for export
        }
        exportOnBackground(directoryUri)
    }

    override fun importSubscriptions(contentUri: Uri?) {
        if (contentUri == null) {
            // User didn't choose any file for import
            return
        }
        if (contentUri.path?.endsWith(".json") == false) {
            return
        }
        importOnBackground(contentUri)
    }

    private fun exportOnBackground(directoryUri: Uri) = viewModelScope.launch(ioDispatcher) {
        val subscriptions =
            SubscriptionsRoomDatabase.getDatabase(application).getSubscriptionsDao().getAll()
        if (subscriptions.isEmpty()) return@launch
        try {
            val fileToSave = DocumentFile.fromTreeUri(application, directoryUri)!!
                .createFile("application/json", "subscriptions_export.json")
            application.contentResolver.openOutputStream(fileToSave!!.uri).use { stream ->
                if (stream == null) return@launch
                val jsonData = Json.encodeToString(subscriptions)
                stream.write(jsonData.toByteArray(charset = Charsets.UTF_8))
            }
        } catch (exception: UnsupportedOperationException) {
            return@launch
        } catch (e: IOException) {
            return@launch
        }
    }

    private fun importOnBackground(contentUri: Uri) = viewModelScope.launch(ioDispatcher) {
        try {
            application.contentResolver.openInputStream(contentUri).use { inputStream ->
                val scanner = Scanner(inputStream).useDelimiter("\\A")
                val jsonStr = if (scanner.hasNext()) scanner.next() else ""
                if (jsonStr.isEmpty()) return@launch
                val importedSubscriptions =
                    Json.decodeFromString<List<SubscriptionEntity>>(jsonStr)
                val subscriptionsDao =
                    SubscriptionsRoomDatabase.getDatabase(application).getSubscriptionsDao()
                importedSubscriptions.forEach { subscriptionsDao.insert(it) }
            }
        } catch (exception: FileNotFoundException) {
            return@launch
        } catch (exception: SerializationException) {
            return@launch
        }
    }
}
