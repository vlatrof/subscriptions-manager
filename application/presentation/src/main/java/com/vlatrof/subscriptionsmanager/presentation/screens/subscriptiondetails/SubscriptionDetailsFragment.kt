package com.vlatrof.subscriptionsmanager.presentation.screens.subscriptiondetails

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vlatrof.subscriptionsmanager.R
import com.vlatrof.subscriptionsmanager.databinding.FragmentSubscriptionDetailsBinding
import com.vlatrof.subscriptionsmanager.presentation.screens.common.BaseViewModel
import com.vlatrof.subscriptionsmanager.presentation.utils.AlertPeriodOptions
import com.vlatrof.subscriptionsmanager.presentation.utils.RenewalPeriodOptions
import com.vlatrof.subscriptionsmanager.presentation.utils.getFirstKey
import com.vlatrof.subscriptionsmanager.presentation.utils.hideKeyboard
import com.vlatrof.subscriptionsmanager.presentation.utils.parseLocalDateFromUTCMilliseconds
import com.vlatrof.subscriptionsmanager.presentation.utils.round
import com.vlatrof.subscriptionsmanager.presentation.utils.showToast
import com.vlatrof.subscriptionsmanager.presentation.utils.toUTCMilliseconds
import dagger.hilt.android.AndroidEntryPoint
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Currency

@AndroidEntryPoint
class SubscriptionDetailsFragment : Fragment(R.layout.fragment_subscription_details) {

    private val args: SubscriptionDetailsFragmentArgs by navArgs()
    private val subscriptionDetailsViewModel: SubscriptionDetailsViewModel by viewModels()
    private lateinit var binding: FragmentSubscriptionDetailsBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSubscriptionDetailsBinding.bind(view)
        observeSubscriptionLiveData()
        setupGoBackButton()
        setupNameTitle()
        setupNextRenewalTitle()
        setupNameInput()
        setupCostInput()
        setupStartDateInput()
        setupSaveButton()
        setupDeleteButton()
    }

    override fun onResume() {
        super.onResume()
        setupCurrencyInput()
        setupRenewalPeriodInput()
        setupAlertPeriodInput()
    }

    private fun observeSubscriptionLiveData() {
        subscriptionDetailsViewModel.subscriptionLiveData.observe(viewLifecycleOwner) {
            if (it == null) {
                showToast(getString(R.string.toast_error_message_something_went_wrong))
                findNavController().popBackStack()
                return@observe
            }

            populateUi(subscription = it)
        }
    }

    private fun setupGoBackButton() {
        binding.btnSubscriptionDetailsGoBack.setOnClickListener {
            hideKeyboard()
            findNavController().popBackStack()
        }
    }

    private fun setupNameTitle() {
        subscriptionDetailsViewModel.nameTitleLiveData.observe(viewLifecycleOwner) {
            binding.tvSubscriptionDetailsNameTitle.text = it
        }
    }

    private fun setupNextRenewalTitle() {
        subscriptionDetailsViewModel.nextRenewalTitleLiveData.observe(viewLifecycleOwner) {
            binding.tvSubscriptionDetailsNextRenewalTitle.text = it
        }
    }

    private fun setupNameInput() {
        // handle new value after text changed
        binding.tietSubscriptionDetailsName.doAfterTextChanged { newValue ->
            subscriptionDetailsViewModel.handleNewNameInputValue(newValue = newValue.toString())
        }

        // handle new state
        subscriptionDetailsViewModel.nameInputState.observe(viewLifecycleOwner) { newState ->
            binding.tilSubscriptionDetailsName.error = getInputErrorStringByState(
                newState = newState
            )
        }
    }

    private fun setupCostInput() {
        // handle new value after text changed
        binding.tietSubscriptionDetailsCost.doAfterTextChanged { newValue ->
            subscriptionDetailsViewModel.handleNewCostInputValue(newValue = newValue.toString())
        }

        // handle new state
        subscriptionDetailsViewModel.costInputState.observe(viewLifecycleOwner) { newState ->
            binding.tilSubscriptionDetailsCost.error = getInputErrorStringByState(
                newState = newState
            )
        }
    }

    private fun setupStartDateInput() {
        val dateField = binding.tietSubscriptionDetailsStartDate

        // init DatePicker and restore selection from viewmodel
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(subscriptionDetailsViewModel.startDateInputSelectionLiveData.value)
            .setTitleText(
                getString(R.string.subscription_e_f_til_start_date_date_picker_title_text)
            )
            .build()
            .apply {
                addOnPositiveButtonClickListener { selection ->
                    subscriptionDetailsViewModel.handleNewStartDateValue(newValue = selection)
                }
            }

        // handle click on field
        dateField.setOnClickListener {
            datePicker.show(
                parentFragmentManager,
                getString(R.string.subscription_e_f_til_start_date_date_picker_tag)
            )
        }

        // handle new date selection
        subscriptionDetailsViewModel.startDateInputSelectionLiveData.observe(viewLifecycleOwner) {
                newSelection ->
            val newSelectedDate = parseLocalDateFromUTCMilliseconds(millis = newSelection)
            binding.tietSubscriptionDetailsStartDate.setText(
                newSelectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            )
        }
    }

    private fun setupSaveButton() {
        // handle click
        binding.btnSubscriptionDetailsSave.setOnClickListener {
            subscriptionDetailsViewModel.updateSubscription(parseSubscription())
            hideKeyboard()
            findNavController().popBackStack()
        }

        // handle new state
        subscriptionDetailsViewModel.buttonSaveState.observe(viewLifecycleOwner) { newState ->
            binding.btnSubscriptionDetailsSave.apply {
                isEnabled = newState
                setTextColor(
                    if (isEnabled) ResourcesCompat.getColor(resources, R.color.green, null)
                    else ResourcesCompat.getColor(resources, R.color.gray, null)
                )
            }
        }
    }

    private fun setupDeleteButton() {
        binding.btnSubscriptionDetailsDelete.setOnClickListener {
            MaterialAlertDialogBuilder(requireActivity(), R.style.Alert_dialog_delete_subscription)
                .setTitle(R.string.subscription_details_delete_dialog_title)
                .setMessage(R.string.subscription_details_delete_dialog_message)
                .setPositiveButton(R.string.subscription_details_delete_dialog_btn_positive) { _, _ -> onPositiveActionDialogDelete() }
                .setNegativeButton(R.string.subscription_details_delete_dialog_btn_negative) { _, _ -> }
                .show()
        }
    }

    private fun setupCurrencyInput() {
        val currencyField = binding.actvSubscriptionDetailsCurrency

        // set menu items
        currencyField.setAdapter(
            ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                Currency.getAvailableCurrencies().toTypedArray()
            )
        )

        // restore value from viewmodel
        currencyField.setText(subscriptionDetailsViewModel.currencyInputValue, false)

        // handle new value
        currencyField.doAfterTextChanged { newEditable ->
            var newValue = newEditable.toString()

            // force only capital characters
            if (newValue.isNotEmpty() && newValue != newValue.uppercase()) {
                newValue = newValue.uppercase()
                currencyField.setText(newValue, newValue.isNotEmpty())
                currencyField.setSelection(newValue.length)
            }

            subscriptionDetailsViewModel.handleNewCurrencyValue(newValue = newValue)
        }

        // handle new input state
        subscriptionDetailsViewModel.currencyInputState.observe(viewLifecycleOwner) { newState ->
            binding.tilSubscriptionDetailsCurrency.error = getInputErrorStringByState(
                newState = newState
            )
        }
    }

    private fun setupRenewalPeriodInput() {
        val renewalPeriodField = binding.actvSubscriptionDetailsRenewalPeriod

        // set menu items
        renewalPeriodField.setAdapter(
            ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                RenewalPeriodOptions(resources).options.values.toTypedArray()
            )
        )

        // restore value from viewmodel
        val restoredValue = subscriptionDetailsViewModel.renewalPeriodInputValue
        renewalPeriodField.setText(restoredValue, false)

        // handle new value
        renewalPeriodField.doAfterTextChanged { newValue ->
            subscriptionDetailsViewModel.handleNewRenewalPeriodValue(
                newValue = newValue.toString()
            )
        }
    }

    private fun setupAlertPeriodInput() {
        val alertField = binding.actvSubscriptionDetailsAlert

        // set menu items
        alertField.setAdapter(
            ArrayAdapter(
                requireActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                AlertPeriodOptions(resources).options.values.toTypedArray()
            )
        )

        // restore value from viewmodel
        alertField.setText(subscriptionDetailsViewModel.alertPeriodInputValue, false)

        // handle new value
        alertField.doAfterTextChanged { newValue ->
            subscriptionDetailsViewModel.handleNewAlertPeriodValue(newValue = newValue.toString())
        }
    }

    private fun getInputErrorStringByState(newState: BaseViewModel.InputState): String {
        return when (newState) {
            is BaseViewModel.InputState.Initial -> { "" }
            is BaseViewModel.InputState.Correct -> { "" }
            is BaseViewModel.InputState.Empty -> {
                getString(R.string.subscription_e_f_field_error_empty)
            }
            is BaseViewModel.InputState.Wrong -> {
                getString(R.string.subscription_e_f_field_error_wrong)
            }
        }
    }

    private fun populateUi(subscription: com.vlatrof.subscriptionsmanager.domain.models.Subscription) {
        // name input
        binding.tietSubscriptionDetailsName.setText(subscription.name)
        subscriptionDetailsViewModel.handleNewNameInputValue(subscription.name)

        // description input
        binding.tietSubscriptionDetailsDescription.setText(subscription.description)

        // cost input
        val costStr = subscription.paymentCost.toString()
        binding.tietSubscriptionDetailsCost.setText(costStr)
        subscriptionDetailsViewModel.handleNewCostInputValue(costStr)

        // currency input
        val currencyStr = subscription.paymentCurrency.currencyCode
        binding.actvSubscriptionDetailsCurrency.setText(currencyStr, false)
        subscriptionDetailsViewModel.handleNewCurrencyValue(currencyStr)

        // start date input
        subscriptionDetailsViewModel.handleNewStartDateValue(
            subscription.startDate.toUTCMilliseconds()
        )

        // renewal period input
        val renewalPeriodStr = RenewalPeriodOptions(resources)
            .options[subscription.renewalPeriod.toString()]
        binding.actvSubscriptionDetailsRenewalPeriod.setText(renewalPeriodStr, false)
        subscriptionDetailsViewModel.handleNewRenewalPeriodValue(renewalPeriodStr!!)

        // alert period input
        val alertPeriodOptions = AlertPeriodOptions(resources)
        val alertPeriodStr = if (subscription.alertEnabled) {
            alertPeriodOptions.options[subscription.alertPeriod.toString()]!!
        } else {
            alertPeriodOptions.default
        }
        binding.actvSubscriptionDetailsAlert.setText(alertPeriodStr, false)
        subscriptionDetailsViewModel.handleNewAlertPeriodValue(alertPeriodStr)
    }

    private fun parseSubscription(): com.vlatrof.subscriptionsmanager.domain.models.Subscription {
        val id = args.subscriptionId

        val name = binding.tietSubscriptionDetailsName.text.toString().trim()

        val description = binding.tietSubscriptionDetailsDescription.text.toString().trim()

        val paymentCost =
            binding.tietSubscriptionDetailsCost.text.toString().toDouble().round(2)

        val paymentCurrency = Currency.getInstance(
            binding.actvSubscriptionDetailsCurrency.text.toString()
        )

        val startDate = parseLocalDateFromUTCMilliseconds(
            subscriptionDetailsViewModel.startDateInputSelectionLiveData.value!!
        )

        // renewal period
        val renewalPeriod = Period.parse(
            RenewalPeriodOptions(resources).options
                .getFirstKey(binding.actvSubscriptionDetailsRenewalPeriod.text.toString())
        )

        // alert period
        val alertEnabled: Boolean
        val alertPeriod: Period
        val alertPeriodOptions = AlertPeriodOptions(resources)
        val alertPeriodValue = binding.actvSubscriptionDetailsAlert.text.toString()
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
            id = id,
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

    private fun onPositiveActionDialogDelete() {
        subscriptionDetailsViewModel.deleteSubscriptionById(args.subscriptionId)
        hideKeyboard()
        findNavController().popBackStack()
    }
}
