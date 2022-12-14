package com.vlatrof.subscriptionsmanager.presentation.screens.subscriptions

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vlatrof.subscriptionsmanager.R
import com.vlatrof.subscriptionsmanager.databinding.RvItemSubscriptionBinding
import com.vlatrof.subscriptionsmanager.domain.models.Subscription
import java.time.LocalDate
import java.time.format.DateTimeFormatter

interface SubscriptionsActionListener {
    fun onSubscriptionItemClick(subscriptionId: Int)
}

class SubscriptionsDiffUtilCallback(

    private val oldList: List<com.vlatrof.subscriptionsmanager.domain.models.Subscription>,
    private val newList: List<com.vlatrof.subscriptionsmanager.domain.models.Subscription>

) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

class SubscriptionsAdapter(

    private val context: Context,
    private val listener: SubscriptionsActionListener

) : RecyclerView.Adapter<SubscriptionsAdapter.SubscriptionViewHolder>() {
    
    var items: List<com.vlatrof.subscriptionsmanager.domain.models.Subscription> = emptyList()
        set(value) {
            value.sortedBy { it.nextRenewalDate }.let { newSortedList ->
                val diff = DiffUtil.calculateDiff(
                    SubscriptionsDiffUtilCallback(items, newSortedList)
                )
                field = newSortedList
                diff.dispatchUpdatesTo(this@SubscriptionsAdapter)
            }
        }

    override fun getItemCount(): Int = items.size

    inner class SubscriptionViewHolder(val binding: RvItemSubscriptionBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubscriptionViewHolder {
        val binding = RvItemSubscriptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        binding.root.setOnClickListener { subscriptionItemView ->
            listener.onSubscriptionItemClick(subscriptionItemView.tag as Int)
        }

        return SubscriptionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubscriptionViewHolder, position: Int) {
        val subscription = items[position]

        holder.itemView.tag = subscription.id

        holder.binding.tvSubscriptionName.text = subscription.name

        val costStr = "${subscription.paymentCost} ${subscription.paymentCurrency.currencyCode}"
        holder.binding.tvSubscriptionCost.text = costStr

        holder.binding.tvSubscriptionNextRenewalDate.text = when (subscription.nextRenewalDate) {
            LocalDate.now() -> {
                context.getString(R.string.today)
            }
            LocalDate.now().plusDays(1) -> {
                context.getString(R.string.tomorrow)
            }
            else -> {
                subscription.nextRenewalDate.format(DateTimeFormatter.ofPattern("dd MMMM"))
            }
        }
    }
}
