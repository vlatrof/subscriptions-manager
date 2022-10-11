package com.vlatrof.subscriptionsmanager.presentation.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.vlatrof.subscriptionsmanager.R
import com.vlatrof.subscriptionsmanager.databinding.FragmentSubscriptionsBinding
import com.vlatrof.subscriptionsmanager.presentation.adapters.SubscriptionsAdapter
import com.vlatrof.subscriptionsmanager.presentation.viewmodels.SubscriptionsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SubscriptionsFragment : Fragment(R.layout.fragment_subscriptions) {

    private val subscriptionsViewModel by viewModel<SubscriptionsViewModel>()
    private lateinit var binding: FragmentSubscriptionsBinding
    private lateinit var subscriptionsAdapter: SubscriptionsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSubscriptionsBinding.bind(view)
        setupSubscriptionsRVAdapter()
        startToObserveSubscriptionsLiveData()
        setupNewSubscriptionButton()
        setupCleanByClickOnTitle() // todo: for testing

    }

    private fun setupSubscriptionsRVAdapter() {
        subscriptionsAdapter = SubscriptionsAdapter()
        binding.rvSubscriptionsList.adapter = subscriptionsAdapter
    }

    private fun startToObserveSubscriptionsLiveData() {
        subscriptionsViewModel.subscriptionsLiveData.observe(viewLifecycleOwner) {
            subscriptionsAdapter.setData(newSubscriptionsList = it)
        }
    }

    private fun setupNewSubscriptionButton() {
        binding.btnNewSubscription.setOnClickListener{
            findNavController().navigate(
                R.id.action_fragment_subscriptions_list_to_fragment_new_subscription
            )
        }
    }

    private fun setupCleanByClickOnTitle() {
        binding.tvSubscriptionsTitle.setOnClickListener{
            subscriptionsViewModel.deleteAllSubscriptions()
        }
    }

}