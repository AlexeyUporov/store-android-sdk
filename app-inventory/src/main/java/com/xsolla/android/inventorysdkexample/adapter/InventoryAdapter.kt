package com.xsolla.android.inventorysdkexample.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.inventory.entity.response.SubscriptionsResponse
import com.xsolla.android.inventorysdkexample.R
import com.xsolla.android.inventorysdkexample.ui.fragments.store.ConsumeListener
import kotlinx.android.synthetic.main.item_inventory.view.*
import java.text.SimpleDateFormat
import java.util.*

class InventoryAdapter(
        var items: List<InventoryResponse.Item>,
        private val consumeListener: ConsumeListener
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    private var subscriptions: List<SubscriptionsResponse.Item>? = null

    fun setSubscriptions(subscriptions: List<SubscriptionsResponse.Item>?) {
        this.subscriptions = subscriptions
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size


    inner class ViewHolder(
            inflater: LayoutInflater,
            parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_inventory, parent, false)) {

        fun bind(item: InventoryResponse.Item) {
            Glide.with(itemView).load(item.imageUrl).into(itemView.itemIcon)
            itemView.itemName.text = item.name
            itemView.itemQuantity.text = item.quantity.toString()
            if (item.virtualItemType == InventoryResponse.Item.VirtualItemType.NON_RENEWING_SUBSCRIPTION) {
                itemView.itemQuantity.visibility = View.INVISIBLE
            } else {
                itemView.itemQuantity.visibility = View.VISIBLE
            }

            itemView.itemExpiration.text = getExpirationText(item)

            itemView.consumeButton.visibility = if (item.remainingUses == 0L) View.INVISIBLE else View.VISIBLE
            itemView.consumeButton.setOnClickListener { consumeListener.onConsume(item) }
        }

        private fun getExpirationText(item: InventoryResponse.Item): String? {
            if (item.virtualItemType != InventoryResponse.Item.VirtualItemType.NON_RENEWING_SUBSCRIPTION) return null

            subscriptions?.find { it.sku == item.sku }?.let {
                return if (it.status == SubscriptionsResponse.Item.Status.ACTIVE) {
                    val date = Date(it.expiredAt * 1000)
                    val sdf = SimpleDateFormat("MM/dd/yyyy hh:mm:ss a", Locale.US)
                    val formattedDate = sdf.format(date)
                    "Active until: $formattedDate"
                } else {
                    "Expired"
                }
            }

            return null
        }

    }
}