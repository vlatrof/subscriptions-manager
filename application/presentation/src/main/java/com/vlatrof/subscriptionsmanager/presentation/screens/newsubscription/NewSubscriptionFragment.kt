package com.vlatrof.subscriptionsmanager.presentation.screens.newsubscription

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.vlatrof.subscriptionsmanager.R
import com.vlatrof.subscriptionsmanager.app.utils.getFirstKey
import com.vlatrof.subscriptionsmanager.app.utils.hideKeyboard
import com.vlatrof.subscriptionsmanager.app.utils.parseLocalDateFromUTCMilliseconds
import com.vlatrof.subscriptionsmanager.app.utils.round
import com.vlatrof.subscriptionsmanager.databinding.FragmentNewSubscriptionBinding
import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import com.vlatrof.subscriptionsmanager.presentation.screens.common.BaseViewModel
import com.vlatrof.subscriptionsmanager.presentation.utils.AlertPeriodOptions
import com.vlatrof.subscriptionsmanager.presentation.utils.RenewalPeriodOptions
import dagger.hilt.android.AndroidEntryPoint
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Currency

@AndroidEntryPoint
class NewSubscriptionFragment : Fragment(R.layout.fragment_new_subscription) {

    private val newSubscriptionViewModel: NewSubscriptionViewModel by viewModels()
    private lateinit var binding: FragmentNewSubscriptionBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewSubscriptionBinding.bind(view)
        setupGoBackButton()
        setupNameInput()
        setupCostInput()
        setupStartDateInput()
        setupCreateButton()
    }

    override fun onResume() {
        super.onResume()
        setupCurrencyInput()
        setupRenewalPeriodInput()
        setupAlertPeriodInput()
    }

    private fun setupGoBackButton() {
        binding.btnNewSubscriptionGoBack.setOnClickListener {
            hideKeyboard()
            findNavController().popBackStack()
        }
    }

    private fun setupNameInput() {
        // handle new value after text changed
        binding.tietNewSubscriptionName.doAfterTextChanged { newValue ->
            newSubscriptionViewModel.handleNewNameInputValue(
                newValue = newValue.toString()
            )
        }

        // handle new state
        newSubscriptionViewModel.nameInputState.observe(viewLifecycleOwner) { newState ->
            binding.tilNewSubscriptionName.error = getInputErrorStringByState(
                newState = newState
            )
        }
    }

    private fun setupCostInput() {
        // set initial value
        val initialValue = "0.0"
        binding.tietNewSubscriptionCost.setText(initialValue)
        newSubscriptionViewModel.handleNewCostInputValue(
            newValue = initialValue
        )

        // handle new value after text changed
        binding.tietNewSubscriptionCost.doAfterTextChanged { newValue ->
            newSubscriptionViewModel.handleNewCostInputValue(
                newValue = newValue.toString()
            )
        }

        // handle new state
        newSubscriptionViewModel.costInputState.observe(viewLifecycleOwner) { newState ->
            binding.tilNewSubscriptionCost.error = getInputErrorStringByState(
                newState = newState
            )
        }
    }

    private fun setupStartDateInput() {
        val dateField = binding.tietNewSubscriptionStartDate

        // init DatePicker and restore selection from viewmodel
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(newSubscriptionViewModel.startDateInputSelectionLiveData.value)
            .setTitleText(
                getString(R.string.subscription_e_f_til_start_date_date_picker_title_text)
            )
            .build()
            .apply {
                addOnPositiveButtonClickListener { selection ->
                    newSubscriptionViewModel.handleNewStartDateValue(newValue = selection)
                }
            }

        // handle input click
        dateField.setOnClickListener {
            datePicker.show(
                parentFragmentManager,
                getString(R.string.subscription_e_f_til_start_date_date_picker_tag)
            )
        }

        // handle new date selection
        newSubscriptionViewModel
            .startDateInputSelectionLiveData
            .observe(viewLifecycleOwner) { newSelection ->
                val newSelectedDate = parseLocalDateFromUTCMilliseconds(millis = newSelection)
                binding.tietNewSubscriptionStartDate.setText(
                    newSelectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
                )
            }
    }

    private fun setupCreateButton() {
        // handle click
        binding.btnNewSubscriptionCreate.setOnClickListener {
            val subscriptionToCreate = parseSubscription()
            newSubscriptionViewModel.insertNewSubscription(subscriptionToCreate)
            hideKeyboard()
            findNavController().popBackStack()
        }

        // handle new state
        newSubscriptionViewModel.buttonCreateState.observe(viewLifecycleOwner) { newState ->
            binding.btnNewSubscriptionCreate.apply {
                isEnabled = newState
                setTextColor(
                    if (isEnabled) ResourcesCompat.getColor(resources, R.color.green, null)
                    else ResourcesCompat.getColor(resources, R.color.gray, null)
                )
            }
        }
    }

    private fun setupCurrencyInput() {
        val currencyField = binding.actvNewSubscriptionCurrency

        // set menu items
        currencyField.setAdapter(
            ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                Currency.getAvailableCurrencies().toTypedArray()
            )
        )

        // restore value from viewmodel
        currencyField.setText(newSubscriptionViewModel.currencyInputValue, false)

        // handle new value
        currencyField.doAfterTextChanged {
            var newValue = it.toString()

            // force only capital characters
            if (newValue.isNotEmpty() && newValue != newValue.uppercase()) {
                newValue = newValue.uppercase()
                currencyField.setText(newValue, newValue.isNotEmpty())
                currencyField.setSelection(newValue.length)
            }

            newSubscriptionViewModel.handleNewCurrencyValue(newValue = newValue)
        }

        // handle new input state
        newSubscriptionViewModel.currencyInputState.observe(viewLifecycleOwner) { newState ->
            binding.tilNewSubscriptionCurrency.error = getInputErrorStringByState(
                newState = newState
            )
        }
    }

    private fun setupRenewalPeriodInput() {
        val renewalPeriodField = binding.actvNewSubscriptionRenewalPeriod

        // set menu items
        renewalPeriodField.setAdapter(
            ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                RenewalPeriodOptions(resources).options.values.toTypedArray()
            )
        )

        renewalPeriodField.setText(newSubscriptionViewModel.renewalPeriodInputValue, false)

        // handle new value
        renewalPeriodField.doAfterTextChanged { newValue ->
            newSubscriptionViewModel.handleNewRenewalPeriodValue(
                newValue = newValue.toString()
            )
        }
    }

    private fun setupAlertPeriodInput() {
        val alertField = binding.actvNewSubscriptionAlert

        // set menu items
        alertField.setAdapter(
            ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                AlertPeriodOptions(resources).options.values.toTypedArray()
            )
        )

        // restore value or set default
        alertField.setText(newSubscriptionViewModel.alertPeriodInputValue, false)

        // handle new value
        alertField.doAfterTextChanged { newValue ->
            newSubscriptionViewModel.handleNewAlertPeriodValue(newValue = newValue.toString())
        }
    }

    private fun getInputErrorStringByState(newState: BaseViewModel.InputState): String {
        return when (newState) {
            is BaseViewModel.InputState.Initial -> {
                ""
            }
            is BaseViewModel.InputState.Correct -> {
                ""
            }
            is BaseViewModel.InputState.Empty -> {
                getString(R.string.subscription_e_f_field_error_empty)
            }
            is BaseViewModel.InputState.Wrong -> {
                getString(R.string.subscription_e_f_field_error_wrong)
            }
        }
    }

    private fun parseSubscription(): com.vlatrof.subscriptionsmanager.domain.models.Subscription {
        val name = binding.tietNewSubscriptionName.text.toString().trim()

        val description = binding.tietNewSubscriptionDescription.text.toString().trim()

        val paymentCost =
            binding.tietNewSubscriptionCost.text.toString().toDouble().round(2)

        val paymentCurrency = Currency.getInstance(
            binding.actvNewSubscriptionCurrency.text.toString()
        )

        // start date
        val startDate = parseLocalDateFromUTCMilliseconds(
            newSubscriptionViewModel.startDateInputSelectionLiveData.value!!
        )

        // renewal period
        val renewalPeriod = Period.parse(
            RenewalPeriodOptions(resources).options
                .getFirstKey(binding.actvNewSubscriptionRenewalPeriod.text.toString())
        )

        // alert period
        val alertEnabled: Boolean
        val alertPeriod: Period
        val alertPeriodOptions = AlertPeriodOptions(resources)
        val alertPeriodValue = binding.actvNewSubscriptionAlert.text.toString()
        if (alertPeriodValue == alertPeriodOptions.default) {
            alertEnabled = false
            alertPeriod = Period.ZERO
        } else {
            alertEnabled = true
            alertPeriod = Period.parse(
                alertPeriodOptions.options.getFirstKey(alertPeriodValue)
            )
        }

        return com.vlatrof.subscriptionsmanager.domain.models.Subscription(
            name = name,
            description = description,
            paymentCost = paymentCost,
            paymentCurrency = paymentCurrency,
            startDate = startDate,
            renewalPeriod = renewalPeriod,
            alertEnabled = alertEnabled,
            alertPeriod = alertPeriod
        )
    }
}
