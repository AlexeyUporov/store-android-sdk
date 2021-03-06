package com.xsolla.android.storesdkexample.listener

import com.xsolla.android.inventory.entity.response.InventoryResponse

interface ConsumeListener {
    fun onConsume(item: InventoryResponse.Item)
    fun onSuccess()
    fun onFailure(errorMessage: String)
}