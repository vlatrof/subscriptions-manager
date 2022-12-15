package com.vlatrof.subscriptionsmanager.presentation.screens.subscriptiondetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.android.material.datepicker.MaterialDatePicker
import com.vlatrof.subscriptionsmanager.R
import com.vlatrof.subscriptionsmanager.domain.di.IoDispatcher
import com.vlatrof.subscriptionsmanager.domain.di.MainDispatcher
import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import com.vlatrof.subscriptionsmanager.domain.usecases.DeleteSubscriptionByIdUseCase
import com.vlatrof.subscriptionsmanager.domain.usecases.GetSubscriptionByIdUseCase
import com.vlatrof.subscriptionsmanager.domain.usecases.UpdateSubscriptionUseCase
import com.vlatrof.subscriptionsmanager.presentation.app.SubscriptionsManagerApp
import com.vlatrof.subscriptionsmanager.presentation.screens.common.BaseViewModel
import com.vlatrof.subscriptionsmanager.presentation.utils.RenewalPeriodOptions
import com.vlatrof.subscriptionsmanager.presentation.utils.getFirstKey
import com.vlatrof.subscriptionsmanager.presentation.utils.parseLocalDateFromUTCMilliseconds
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.NumberFormatException
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Currency
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class SubscriptionDetailsViewModel @Inject constructor(

    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val applicationContext: SubscriptionsManagerApp,
    private val getSubscriptionByIdUseCase: GetSubscriptionByIdUseCase,
    private val updateSubscriptionUseCase: UpdateSubscriptionUseCase,
    private val deleteSubscriptionByIdUseCase: DeleteSubscriptionByIdUseCase,
    savedStateHandle: SavedStateHandle

) : BaseViewModel() {

    init {
        loadSubscriptionById(
            id = SubscriptionDetailsFragmentArgs
                .fromSavedStateHandle(savedStateHandle = savedStateHandle)
                .subscriptionId
        )
    }

    private val mutableSubscriptionLiveData = MutableLiveData<Subscription?>()
    val subscriptionLiveData: LiveData<Subscription?> = mutableSubscriptionLiveData

    private val mutableNameTitleLiveData = MutableLiveData("")
    val nameTitleLiveData: LiveData<String> = mutableNameTitleLiveData

    private val mutableNextRenewalTitleLiveData = MutableLiveData("")
    val nextRenewalTitleLiveData: LiveData<String> = mutableNextRenewalTitleLiveData

    private val mutableNameInputState = MutableLiveData<InputState>(InputState.Initial)
    val nameInputState: LiveData<InputState> = mutableNameInputState

    private val mutableCostInputState = MutableLiveData<InputState>(InputState.Initial)
    val costInputState: LiveData<InputState> = mutableCostInputState

    private val mutableCurrencyInputState = MutableLiveData<InputState>(InputState.Initial)
    val currencyInputState: LiveData<InputState> = mutableCurrencyInputState

    private val mutableStartDateInputSelectionLiveData =
        MutableLiveData(MaterialDatePicker.todayInUtcMilliseconds())
    val startDateInputSelectionLiveData: LiveData<Long> = mutableStartDateInputSelectionLiveData

    private val mutableButtonSaveState = MutableLiveData(false)
    val buttonSaveState: LiveData<Boolean> = mutableButtonSaveState

    var currencyInputValue: String = ""

    var renewalPeriodInputValue: String = ""

    var alertPeriodInputValue: String = ""

    private val availableCurrencies = Currency.getAvailableCurrencies()

    private var currentRenewalPeriod: Period = Period.parse("P1D")

    private fun loadSubscriptionById(id: Int) {
        viewModelScope.launch(mainDispatcher) {
            mutableSubscriptionLiveData.value = getSubscriptionByIdUseCase(id)
        }
    }

    fun updateSubscription(subscription: Subscription) {
        viewModelScope.launch(ioDispatcher) {
            updateSubscriptionUseCase(subscription)
        }
    }
    
    fun deleteSubscriptionById(id: Int) {
        viewModelScope.launch(ioDispatcher) {
            deleteSubscriptionByIdUseCase(id)
        }
    }
    
    fun handleNewNameInputValue(newValue: String) {
        mutableNameTitleLiveData.value = newValue
        mutableNameInputState.value = validateNameValue(newValue)
        updateSaveButtonState()
    }

    fun handleNewCostInputValue(newValue: String) {
        mutableCostInputState.value = validateCostValue(newValue)
        updateSaveButtonState()
    }

    fun handleNewCurrencyValue(newValue: String) {
        currencyInputValue = newValue
        mutableCurrencyInputState.value = validateCurrencyValue(newValue)
        updateSaveButtonState()
    }

    fun handleNewStartDateValue(newValue: Long) {
        mutableStartDateInputSelectionLiveData.value = newValue
        val newStartDate = parseLocalDateFromUTCMilliseconds(newValue)
        val newNextRenewalDate = calculateNextRenewalDate(newStartDate, currentRenewalPeriod)
        mutableNextRenewalTitleLiveData.value = generateNextRenewalTitleStr(newNextRenewalDate)
    }

    fun handleNewRenewalPeriodValue(newValue: String) {
        renewalPeriodInputValue = newValue
        currentRenewalPeriod = Period.parse(
            RenewalPeriodOptions(applicationContext.resources).options.getFirstKey(newValue)
        )
        val startDate =
            parseLocalDateFromUTCMilliseconds(startDateInputSelectionLiveData.value!!)
        val newNextRenewalDate = calculateNextRenewalDate(startDate, currentRenewalPeriod)
        mutableNextRenewalTitleLiveData.value = generateNextRenewalTitleStr(newNextRenewalDate)
    }

    fun handleNewAlertPeriodValue(newValue: String) {
        alertPeriodInputValue = newValue
    }

    private fun validateNameValue(newValue: String): InputState {
        return if (newValue.isEmpty()) InputState.Empty
        else if (newValue.isBlank()) InputState.Wrong
        else InputState.Correct
    }

    private fun validateCostValue(newValue: String): InputState {
        if (newValue.isEmpty()) return InputState.Empty

        try {
            newValue.toDouble()
        } catch (nfe: NumberFormatException) {
            return InputState.Wrong
        }

        return InputState.Correct
    }

    private fun validateCurrencyValue(newValue: String): InputState {
        if (newValue.isEmpty()) {
            return InputState.Empty
        }

        try {
            if (!availableCurrencies.contains(Currency.getInstance(newValue))) {
                return InputState.Wrong
            }
        } catch (exception: IllegalArgumentException) {
            return InputState.Wrong
        }

        return InputState.Correct
    }

    private fun updateSaveButtonState() {
        mutableButtonSaveState.value =
            nameInputState.value == InputState.Correct &&
            costInputState.value == InputState.Correct &&
            currencyInputState.value == InputState.Correct
    }

    private fun calculateNextRenewalDate(startDate: LocalDate, renewalPeriod: Period): LocalDate {
        val currentDate = LocalDate.now()
        var nextRenewalDate: LocalDate = LocalDate.from(startDate)
        while (nextRenewalDate < currentDate) {
            nextRenewalDate = renewalPeriod.addTo(nextRenewalDate) as LocalDate
        }
        return nextRenewalDate
    }

    private fun generateNextRenewalTitleStr(nextRenewalDate: LocalDate): String {
        val nextRenewalTitle = applicationContext.resources.getString(
            R.string.subscription_details_tv_next_renewal_title
        )

        val nextRenewalDateFormatted = when (nextRenewalDate) {
            LocalDate.now() -> {
                applicationContext.resources.getString(R.string.today)
            }
            LocalDate.now().plusDays(1) -> {
                applicationContext.resources.getString(R.string.tomorrow)
            }
            else -> {
                nextRenewalDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            }
        }

        return "$nextRenewalTitle $nextRenewalDateFormatted"
    }
}
