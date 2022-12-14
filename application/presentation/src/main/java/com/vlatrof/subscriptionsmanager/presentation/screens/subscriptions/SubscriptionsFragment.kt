package com.vlatrof.subscriptionsmanager.presentation.screens.subscriptions

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.vlatrof.subscriptionsmanager.R
import com.vlatrof.subscriptionsmanager.databinding.FragmentSubscriptionsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubscriptionsFragment : Fragment(R.layout.fragment_subscriptions) {

    private val subscriptionsViewModel: SubscriptionsViewModel by viewModels()
    private lateinit var binding: FragmentSubscriptionsBinding
    private lateinit var subscriptionsAdapter: SubscriptionsAdapter
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSubscriptionsBinding.bind(view)
        setupSubscriptionsRVAdapter()
        observeSubscriptionsLiveData()
        setupOpenOptionsButton()
        setupNewSubscriptionButton()
        setupNoSubscriptionsLayout()
    }
    
    private fun setupNoSubscriptionsLayout() {
        binding.btnLlSubscriptionsNotFoundAddNew.setOnClickListener {
            findNavController().navigate(
                R.id.action_fragment_subscriptions_to_fragment_new_subscription
            )
        }
    }

    private fun setupSubscriptionsRVAdapter() {
        val listener = object : SubscriptionsActionListener {
            override fun onSubscriptionItemClick(subscriptionId: Int) {
                openSubscriptionDetailsScreen(subscriptionId)
            }
        }
        subscriptionsAdapter = SubscriptionsAdapter(requireActivity(), listener)
        binding.rvSubscriptionsList.adapter = subscriptionsAdapter
    }

    private fun observeSubscriptionsLiveData() {
        subscriptionsViewModel.subscriptionsLiveData.observe(viewLifecycleOwner) { newList ->

            if (newList.isEmpty()) {
                binding.rvSubscriptionsList.visibility = View.GONE
                binding.llSubscriptionsNotFound.visibility = View.VISIBLE
            } else {
                binding.llSubscriptionsNotFound.visibility = View.GONE
                binding.rvSubscriptionsList.visibility = View.VISIBLE
            }

            subscriptionsAdapter.items = newList
        }
    }

    private fun setupNewSubscriptionButton() {
        binding.btnSubscriptionsAddNew.setOnClickListener {
            findNavController().navigate(
                R.id.action_fragment_subscriptions_to_fragment_new_subscription
            )
        }
    }

    private fun openSubscriptionDetailsScreen(subscriptionId: Int) {
        findNavController().navigate(
            SubscriptionsFragmentDirections
                .actionFragmentSubscriptionsToFragmentSubscriptionDetails(
                    subscriptionId = subscriptionId
                )
        )
    }

    private fun setupOpenOptionsButton() {
        binding.btnOpenOptions.setOnClickListener {
            findNavController().navigate(
                R.id.action_fragment_subscriptions_to_fragment_options
            )
        }
    }
}
