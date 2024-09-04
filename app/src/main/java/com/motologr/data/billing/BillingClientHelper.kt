package com.motologr.data.billing

import android.widget.Toast
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ConsumeResponseListener
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.motologr.BuildConfig
import com.motologr.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object BillingClientHelper {
    lateinit var activity: MainActivity

    fun initBillingHelper(activity: MainActivity) {
        BillingClientHelper.activity = activity

        initBillingClient()

        Thread {
            val addonEntities = MainActivity.getDatabase()!!.addonDao().getAll()

            for (addonEntity in addonEntities)
                addons.add(addonEntity.convertToAddonObject())
        }.start()
    }

    var addons = ArrayList<BillingClientData>()

    private const val ART_PACK_ID = "art_pack_addon_1"
    val isArtPackEnabled : Boolean get() = addons.filter { x -> x.productId == ART_PACK_ID && !x.isRefunded }.isNotEmpty()

    private val consumeResponseListener =
        ConsumeResponseListener { billingResult, _ ->

        }

    private fun checkForRefundedAddons(purchases : List<Purchase>) {
        // Try and match to item in addon
        for (addon in addons) {
            val matchingPurchase = purchases.filter { x -> x.products.contains(addon.productId) && x.purchaseToken == addon.purchaseToken }.isNotEmpty()
            if (matchingPurchase)
                continue
            else {
                Thread {
                    MainActivity.getDatabase()!!.addonDao()
                        .setRefunded(addon.productId, addon.purchaseToken)
                }.start()

                setRefunded(addon.productId, addon.purchaseToken)
            }
        }
    }

    private fun purchaseNotInAddons(purchase: Purchase) : Boolean {
        if (addons.isEmpty())
            return true

        for (product in purchase.products) {
            if (addons.none { x -> x.purchaseToken == purchase.purchaseToken && x.productId == product })
                return true
        }

        return false
    }

    private val purchasesResponseListener =
        PurchasesResponseListener { billingResult, purchases ->
            if (billingResult.responseCode != BillingClient.BillingResponseCode.OK)
                return@PurchasesResponseListener

            for (purchase in purchases) {
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build(),
                            acknowledgePurchaseResponseListener)
                } else if (purchaseNotInAddons(purchase)) {
                    recordPurchase(purchase)
                }
                else {
                    acknowledgePurchase(purchase)
                    checkPurchaseRefundState(purchase)
                }
            }

            checkForRefundedAddons(purchases)

            if (!BuildConfig.DEBUG)
                return@PurchasesResponseListener

            // Set IAPs to be consumed
/*
            val params = ConsumeParams.newBuilder()
                .setPurchaseToken(purchases.first().purchaseToken)
            billingClient.consumeAsync(params.build(), consumeResponseListener)
*/
        }

    fun validatePurchases() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)

        // uses queryPurchasesAsync Kotlin extension function
        billingClient.queryPurchasesAsync(params.build(), purchasesResponseListener)
    }

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    CoroutineScope(Dispatchers.Main).launch {
                        handlePurchase(purchase)
                    }
                }
                Toast.makeText(activity, "Purchase completed.", Toast.LENGTH_LONG).show()
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
                Toast.makeText(activity, "Purchase cancelled.", Toast.LENGTH_LONG).show()
            } else {
                // Handle any other error codes.
                Toast.makeText(activity, "Error. Please try again later.", Toast.LENGTH_LONG).show()
            }
        }

    lateinit var billingClient : BillingClient

    private fun initBillingClient() {
        billingClient = BillingClient.newBuilder(activity)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    validatePurchases()
                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    val queryProductDetailsParams = QueryProductDetailsParams.newBuilder().setProductList(
        listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("art_pack_addon_1")
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )
    ).build()

    fun requestPurchase(productDetails : ProductDetails) {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        // Launch the billing flow
        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    val acknowledgePurchaseResponseListener: AcknowledgePurchaseResponseListener =
        AcknowledgePurchaseResponseListener {
            billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Do something
            }
    }

    private fun recordPurchase(purchase: Purchase) {
        for (product in purchase.products) {
            val billingClientData = BillingClientData(product, purchase.isAcknowledged, false, purchase.purchaseToken)

            if (!addons.contains(billingClientData)) {
                addons.add(billingClientData)

                Thread {
                    MainActivity.getDatabase()!!.addonDao()
                        .insert(billingClientData.convertToAddonEntity())
                }.start()
            }
        }
    }

    private fun alreadyAcknowledged(productId: String, purchaseToken: String) :Boolean {
        if (addons.isEmpty())
            return true

        val matchingAddons = addons.filter { x -> x.productId == productId && x.purchaseToken == purchaseToken }

        if (matchingAddons.isEmpty())
            return true

        if (matchingAddons.first().isAcknowledged)
            return true

        return false
    }

    private fun setAcknowledged(productId: String, purchaseToken: String) {
        val matchingAddons = addons.filter { x -> x.productId == productId && x.purchaseToken == purchaseToken }

        if (matchingAddons.isEmpty())
            return

        matchingAddons.first().isAcknowledged = true
    }

    fun acknowledgePurchase(purchase: Purchase) {
        if (purchase.products.isEmpty())
            return

        for (product in purchase.products) {
            if (!alreadyAcknowledged(product, purchase.purchaseToken)) {
                Thread {
                    MainActivity.getDatabase()!!.addonDao()
                        .acknowledgePurchase(product)
                }.start()

                setAcknowledged(product, purchase.purchaseToken)
            }
        }
    }

    private fun isRefunded(productId: String, purchaseToken: String) :Boolean {
        if (addons.isEmpty())
            return false

        val matchingAddons = addons.filter { x -> x.purchaseToken == purchaseToken && x.productId == productId }

        return matchingAddons.isEmpty()
    }

    private fun setRefunded(productId: String, purchaseToken: String) {
        val matchingAddons = addons.filter { x -> x.purchaseToken == purchaseToken && x.productId == productId }

        if (matchingAddons.isEmpty())
            return

        matchingAddons.first().isRefunded = true
    }

    fun checkPurchaseRefundState(purchase: Purchase) {
        if (purchase.products.isEmpty())
            return

        for (product in purchase.products) {
            if (isRefunded(product, purchase.purchaseToken)) {
                Thread {
                    MainActivity.getDatabase()!!.addonDao()
                        .setRefunded(product, purchase.purchaseToken)
                }.start()

                setRefunded(product, purchase.purchaseToken)
            }
        }
    }

    suspend fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            recordPurchase(purchase)

            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                withContext(Dispatchers.IO) {
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build(), acknowledgePurchaseResponseListener)
                }
            }
        }
    }
}