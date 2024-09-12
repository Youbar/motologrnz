package com.motologr.ui.ellipsis.addons

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.android.billingclient.api.ProductDetails
import com.motologr.data.billing.BillingClientHelper
import com.motologr.data.billing.BillingClientHelper.queryProductDetailsParams

class AddonsViewModel : ViewModel() {

    //region Art Pack

    private val _artPackProductDetails : ProductDetails
        get() {
            return _productDetails.first { x -> x.productId == BillingClientHelper.Constants.ART_PACK_ID }
        }

    private val _isArtPackPurchaseEnabled : Boolean
        get() {
            return _isPurchasesAvailable && !BillingClientHelper.isArtPackEnabled
        }

    var isArtPackPurchaseEnabled by mutableStateOf(false)

    private val _artPackButtonText : String
        get() {
            return if (_isArtPackPurchaseEnabled)
                _artPackProductDetails.oneTimePurchaseOfferDetails?.formattedPrice.toString()
            else if (_isPurchasesAvailable && !_isArtPackPurchaseEnabled)
                "Owned"
            else
                "Unavailable"
        }

    var artPackButtonText by mutableStateOf("Unavailable")

    private var isPurchaseRequestPending = false

    fun purchaseArtPack() {
        if (!isPurchaseRequestPending) {
            isPurchaseRequestPending = true
            BillingClientHelper.requestPurchase(_artPackProductDetails)
        }

        isPurchaseRequestPending = false
    }

    //endregion

    private var _productDetails = ArrayList<ProductDetails>()

    private val _isPurchasesAvailable : Boolean
        get() {
            if (_productDetails == null)
                return false

            return _productDetails.isNotEmpty()
        }

    private fun getPurchases() {
        BillingClientHelper.billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
                billingResult,
                productDetailsList ->
            _productDetails = ArrayList(productDetailsList)

            // Trigger update on MutableState items
            isArtPackPurchaseEnabled = _isArtPackPurchaseEnabled
            artPackButtonText = _artPackButtonText
        }
    }

    init {
        getPurchases()
    }
}