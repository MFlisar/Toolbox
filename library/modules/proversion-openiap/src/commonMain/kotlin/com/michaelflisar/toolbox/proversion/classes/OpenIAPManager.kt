package com.michaelflisar.toolbox.proversion.classes

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.features.proversion.ProState
import com.michaelflisar.toolbox.proversion.Product
import io.github.hyochan.kmpiap.KmpIAP
import io.github.hyochan.kmpiap.Purchase
import io.github.hyochan.kmpiap.fetchProducts
import io.github.hyochan.kmpiap.getCurrentPlatform
import io.github.hyochan.kmpiap.kmpIapInstance
import io.github.hyochan.kmpiap.openiap.DeepLinkOptions
import io.github.hyochan.kmpiap.openiap.ErrorCode
import io.github.hyochan.kmpiap.openiap.IapPlatform
import io.github.hyochan.kmpiap.openiap.PurchaseError
import io.github.hyochan.kmpiap.requestPurchase
import io.github.hyochan.kmpiap.toPurchaseInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import io.github.hyochan.kmpiap.Product as IAPProduct

internal class OpenIAPManager(
    private val appScope: CoroutineScope,
    private val products: List<Product>,
    val log: Boolean,
    val kmpIAP: KmpIAP,
    val deliverProduct: (productId: String) -> Unit,
) {
    private val dataState = MutableStateFlow<Result<IAPData>?>(null)

    val data = dataState.asStateFlow()

    /**
     * IAP initialisieren und Listener starten
     * + onConfigured wird aufgerufen, sobald die initiale Konfiguration abgeschlossen ist (z.B. um den Pro-Status zu überprüfen).
     */
    fun configure(onConfigured: suspend () -> Unit) {
        appScope.launch {

            // Initialize IAP connection
            kmpIAP.initConnection()

            // Produkte laden
            refreshData()

            // Check Pro version on startup
            onConfigured()
        }

        appScope.launch {
            kmpIAP.purchaseUpdatedListener.collect { purchase ->
                L.logIf { log }?.v { "Purchase successful: ${purchase.productId}" }
                val isConsumable =
                    products.find { it.id == purchase.productId }?.isConsumable ?: false
                handlePurchaseSuccess(purchase, isConsumable)
            }
        }

        appScope.launch {
            kmpIAP.purchaseErrorListener.collect { error ->
                L.logIf { log }?.v { "Purchase failed: ${error.message}" }
                handlePurchaseError(error)
            }
        }
    }

    /*
    * lädt Produkte + Käufe (ggf. neu)
     */
    suspend fun refreshData() {
        val products = loadProducts()
        val purchases = loadPurchases()
        val result = if (products.isSuccess && purchases.isSuccess) {
            Result.success(IAPData(products.getOrThrow(), purchases.getOrThrow()))
        } else {
            val error = products.exceptionOrNull() ?: purchases.exceptionOrNull()
            Result.failure(error ?: Exception("Unknown error loading IAP data"))
        }
        dataState.value = result
    }

    /*
    * Kauf anstoßen
     */
    suspend fun purchaseProduct(
        product: FullProduct,
    ) {
        kmpIAP.requestPurchase {
            apple {
                sku = product.id
                quantity = product.iosAmount
            }
            google {
                skus = listOf(product.id)
            }
        }
    }

    /*
    * prüft, ob eines der Produkte gekauft wurde
     */
    suspend fun checkIfIsPurchased(products: List<Product>): ProState {
        val purchases = getData().getOrNull()?.purchases ?: emptyList()
        if (purchases.isNotEmpty()) {
            L.logIf { log }?.v { "Found ${purchases.size} available purchases" }
        }
        val productIds = products.map { it.id }
        if (purchases.isNotEmpty() && purchases.any { it.productId in productIds }) {
            L.logIf { log }
                ?.v { "Found matching purchase for product IDs: ${productIds.joinToString(", ")}" }
            return ProState.Yes
        } else {
            L.logIf { log }?.v { "No available purchases found" }
            return ProState.No
        }
    }

    /*
    * Alle verfügbaren Käufe abrufen und Produkt liefern (z.B. nach Neuinstallation oder Gerätewechsel)
     */
    suspend fun restorePurchases() {
        try {
            // Get available purchases
            val purchases = kmpIAP.getAvailablePurchases()
            L.logIf { log }?.v { "Found ${purchases.size} available purchases" }

            // Process each restored purchase
            purchases.forEach { purchase ->
                deliverProduct(purchase.productId)
            }
        } catch (e: Exception) {
            L.logIf { log }?.e { "Error restoring purchases: $e" }
        }
    }

    suspend fun openSubscriptionManagement(product: FullProduct) {
        if (getCurrentPlatform() == IapPlatform.Android) {
            kmpIapInstance.deepLinkToSubscriptions(
                DeepLinkOptions(skuAndroid = product.id)
            )
        } else {
            kmpIapInstance.deepLinkToSubscriptions(

            )
        }
    }

    private suspend fun handlePurchaseSuccess(purchase: Purchase, isConsumable: Boolean) {
        // 1. Deliver product
        deliverProduct(purchase.productId)

        // 2. Finish transaction
        kmpIAP.finishTransaction(
            purchase = purchase.toPurchaseInput(),
            isConsumable = isConsumable
        )
    }

    private fun handlePurchaseError(error: PurchaseError) {
        when (error.code) {
            ErrorCode.NetworkError -> {
                L.logIf { log }
                    ?.d { "Network error during purchase. Suggesting retry after delay." }
            }

            ErrorCode.AlreadyOwned -> {
                L.logIf { log }?.w { "Product already owned. Attempting to restore purchases." }
                appScope.launch { restorePurchases() }
            }

            else -> {
                L.logIf { log }?.e { "Purchase error: ${error.message} (code: ${error.code})" }
            }
        }
    }

    private suspend fun loadProducts(): Result<List<FullProduct>> {
        return try {

            val loadedInAppProducts = loadSingleProductTypes(Product.Type.InApp)
            val loadedSubscriptionProducts = loadSingleProductTypes(Product.Type.Subscription)
            val loadedProducts = loadedInAppProducts + loadedSubscriptionProducts

            val result = loadedProducts.mapNotNull {
                val matchingProduct = products.find { product -> product.id == it.id }
                if (matchingProduct != null) {
                    FullProduct(
                        product = matchingProduct,
                        iapProduct = it
                    )
                } else {
                    L.logIf { log }
                        ?.w { "Loaded product with ID ${it.id} does not match any configured product" }
                    null
                }
            }
            Result.success(result)
        } catch (e: Exception) {
            L.logIf { log }?.e(e) { "Error fetching products: ${e.message}" }
            Result.failure(e)
        }
    }


    // --------------------------
    // Hilfsfunktionen
    // --------------------------

    private suspend fun getData(): Result<IAPData> {
        return dataState.value ?: dataState.filterNotNull().first()
    }

    private suspend fun loadPurchases(): Result<List<Purchase>> {
        try {
            return Result.success(kmpIAP.getAvailablePurchases())
        } catch (e: Exception) {
            L.logIf { log }?.e(e) { "Error fetching purchases: ${e.message}" }
            return Result.failure(e)
        }
    }

    private suspend fun loadSingleProductTypes(productType: Product.Type): List<IAPProduct> {
        val filtered = products.filter { it.type == productType }
        val loaded = if (filtered.isNotEmpty()) {
            kmpIAP.fetchProducts {
                skus = filtered.map { it.id }
                type = productType.productQueryType
            }
        } else emptyList()
        return loaded
    }
}