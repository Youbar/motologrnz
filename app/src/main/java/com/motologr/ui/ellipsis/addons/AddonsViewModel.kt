package com.motologr.ui.ellipsis.addons

import androidx.lifecycle.ViewModel
import com.android.billingclient.api.ProductDetails
import com.motologr.data.billing.BillingClientHelper
import com.motologr.data.billing.BillingClientHelper.queryProductDetailsParams

class AddonsViewModel : ViewModel() {

    init {
        getPurchases()
    }

    val isArtPackPurchaseEnabled : Boolean
        get() {
            return productDetails.size > 0 && !BillingClientHelper.isArtPackEnabled
        }

    val artPackButtonText : String
        get() {
            return if (isArtPackPurchaseEnabled)
                "Purchase"
            else
                "Owned"
        }

    fun purchaseArtPack() {
        BillingClientHelper.requestPurchase(productDetails.first { x -> x.productId == BillingClientHelper.Constants.ART_PACK_ID })
    }

    private var productDetails = ArrayList<ProductDetails>()

    private fun getPurchases() {
        BillingClientHelper.billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
                billingResult,
                productDetailsList ->
            productDetails = ArrayList(productDetailsList)
        }
    }
}