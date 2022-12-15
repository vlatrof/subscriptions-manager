package com.vlatrof.subscriptionsmanager.presentation.screens.options

import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.viewModelScope
import com.vlatrof.subscriptionsmanager.domain.di.IoDispatcher
import com.vlatrof.subscriptionsmanager.domain.usecases.GetAllSubscriptionsUseCase
import com.vlatrof.subscriptionsmanager.domain.usecases.InsertSubscriptionsListUseCase
import com.vlatrof.subscriptionsmanager.presentation.app.SubscriptionsManagerApp
import com.vlatrof.subscriptionsmanager.presentation.models.IOSubscription
import com.vlatrof.subscriptionsmanager.presentation.screens.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.FileNotFoundException
import java.io.IOException
import java.util.Scanner
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@HiltViewModel
class OptionsViewModel @Inject constructor(

    private val application: SubscriptionsManagerApp,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val getAllSubscriptionsUseCase: GetAllSubscriptionsUseCase,
    private val insertSubscriptionsListUseCase: InsertSubscriptionsListUseCase

) : BaseViewModel() {

    fun getCurrentNightMode() = application.getCurrentNightMode()

    fun applyNightMode(mode: Int) {
        application.applyNightMode(mode)
        application.saveNightMode(mode)
    }

    fun exportSubscriptions(directoryUri: Uri?) {
        if (directoryUri == null) {
            return // User didn't choose any directory for export
        }
        exportOnBackground(directoryUri)
    }

    fun importSubscriptions(contentUri: Uri?) {
        if (contentUri == null) {
            // User didn't choose any file for import
            return
        }
        // TODO: temporary solution with path.contains(".json") -> fix
        if (contentUri.path?.contains(".json") == false) {
            return
        }
        importOnBackground(contentUri)
    }

    // TODO: refactor
    private fun exportOnBackground(directoryUri: Uri) = viewModelScope.launch(ioDispatcher) {
        val ioSubscriptions = getAllSubscriptionsUseCase().map { domainSubscription ->
            IOSubscription(domainSubscription = domainSubscription)
        }

        if (ioSubscriptions.isEmpty()) return@launch
        try {
            val fileToSave = DocumentFile.fromTreeUri(application, directoryUri)!!
                .createFile("application/json", "subscriptions_export.json")
             
            application.contentResolver.openOutputStream(fileToSave!!.uri).use { stream ->
                if (stream == null) return@launch
                val jsonData = Json.encodeToString(ioSubscriptions)
                stream.write(jsonData.toByteArray(charset = Charsets.UTF_8))
            }
        } catch (exception: UnsupportedOperationException) {
            return@launch
        } catch (e: IOException) {
            return@launch
        }
    }

    // TODO: refactor
    private fun importOnBackground(contentUri: Uri) = viewModelScope.launch(ioDispatcher) {
        try {
            application.contentResolver.openInputStream(contentUri).use { inputStream ->
                val scanner = Scanner(inputStream).useDelimiter("\\A")
                val jsonStr = if (scanner.hasNext()) scanner.next() else ""
                
                if (jsonStr.isEmpty()) return@launch

                val importedSubscriptions =
                    Json.decodeFromString<List<IOSubscription>>(jsonStr)
                
                val domainSubscriptions = importedSubscriptions.map {
                        importedSubscription ->
                    importedSubscription.toDomainSubscription()
                }
                
                insertSubscriptionsListUseCase(domainSubscriptions)
            }
        } catch (exception: FileNotFoundException) {
            return@launch
        } catch (exception: SerializationException) {
            return@launch
        }
    }
}
