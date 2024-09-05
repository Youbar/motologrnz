package com.motologr.ui.ellipsis.addons

import androidx.lifecycle.ViewModel
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.motologr.data.billing.BillingClientHelper
import com.motologr.data.billing.BillingClientHelper.queryProductDetailsParams

class AddonsViewModel : ViewModel() {

    init {
        getPurchases()
    }

    //region Art Pack

    private val artPackProductDetails : ProductDetails
        get() {
            return productDetails.first { x -> x.productId == BillingClientHelper.Constants.ART_PACK_ID }
        }

    val isArtPackPurchaseEnabled : Boolean
        get() {
            return isPurchasesAvailable && !BillingClientHelper.isArtPackEnabled
        }

    val artPackButtonText : String
        get() {
            return if (isArtPackPurchaseEnabled)
                artPackProductDetails.oneTimePurchaseOfferDetails?.formattedPrice.toString()
            else if (isPurchasesAvailable && !isArtPackPurchaseEnabled)
                "Owned"
            else
                "Unavailable"
        }

    fun purchaseArtPack() {
        BillingClientHelper.requestPurchase(artPackProductDetails)
    }

    //endregion

    private val isPurchasesAvailable : Boolean
        get() {
            return productDetails.size > 0
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