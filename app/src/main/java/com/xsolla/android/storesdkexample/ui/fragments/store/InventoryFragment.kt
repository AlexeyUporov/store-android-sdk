package com.xsolla.android.storesdkexample.ui.fragments.store

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.xsolla.android.inventory.XInventory
import com.xsolla.android.inventory.callback.ConsumeItemCallback
import com.xsolla.android.inventory.callback.GetInventoryCallback
import com.xsolla.android.inventory.callback.GetSubscriptionsCallback
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.inventory.entity.response.SubscriptionsResponse
import com.xsolla.android.storesdkexample.R
import com.xsolla.android.storesdkexample.adapter.InventoryAdapter
import com.xsolla.android.storesdkexample.listener.ConsumeListener
import com.xsolla.android.storesdkexample.ui.fragments.base.BaseFragment
import com.xsolla.android.storesdkexample.ui.vm.VmInventory
import kotlinx.android.synthetic.main.fragment_inventory.*

class InventoryFragment : BaseFragment(), ConsumeListener {

    private val viewModel: VmInventory by activityViewModels()
    private lateinit var inventoryAdapter: InventoryAdapter

    override fun getLayout() = R.layout.fragment_inventory

    override fun initUI() {
        with(recycler) {
            val linearLayoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(context, linearLayoutManager.orientation).apply {
                ContextCompat.getDrawable(context, R.drawable.item_divider)?.let { setDrawable(it) }
            })
            layoutManager = linearLayoutManager
            goToStoreButton.setOnClickListener { findNavController().navigate(R.id.nav_vi) }
        }

        inventoryAdapter = InventoryAdapter(listOf(), this)
        recycler.adapter = inventoryAdapter

        viewModel.inventory.observe(viewLifecycleOwner) {
            inventoryAdapter.items = it
            inventoryAdapter.notifyDataSetChanged()

            setupPlaceholderVisibility()
        }
        viewModel.subscriptions.observe(viewLifecycleOwner) {
            inventoryAdapter.setSubscriptions(it)

            setupPlaceholderVisibility()
        }

        viewModel.getItems { showSnack(it) }
        viewModel.getSubscriptions { showSnack(it) }
    }

    override fun onConsume(item: InventoryResponse.Item) {
        consume(item)
    }

    override fun onSuccess() {
        showSnack("Item Consumed")
    }

    override fun onFailure(errorMessage: String) {
        showSnack(errorMessage)
    }

    private fun consume(item: InventoryResponse.Item) {
        XInventory.consumeItem(item.sku!!, 1, null, object : ConsumeItemCallback {
            override fun onSuccess() {
                XInventory.getInventory(object : GetInventoryCallback {
                    override fun onSuccess(data: InventoryResponse) {
                        inventoryAdapter.items = data.items.filter { item -> item.type == InventoryResponse.Item.Type.VIRTUAL_GOOD }
                        inventoryAdapter.notifyDataSetChanged()
                        showSnack("Item consumed")
                    }

                    override fun onError(throwable: Throwable?, errorMessage: String?) {
                        showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
                    }
                })
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }
        })
    }

    private fun setupPlaceholderVisibility() {
        noItemsPlaceholder.isVisible = viewModel.inventorySize == 0
        recycler.isVisible = viewModel.inventorySize != 0
    }
}