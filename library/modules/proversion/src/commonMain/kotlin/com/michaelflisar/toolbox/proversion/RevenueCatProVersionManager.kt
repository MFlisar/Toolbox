package com.michaelflisar.toolbox.proversion

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.Dialog
import com.michaelflisar.composedialogs.core.DialogStateNoData
import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.features.proversion.BaseAppProVersionManager
import com.michaelflisar.toolbox.features.proversion.ProState
import com.revenuecat.purchases.kmp.Purchases
import com.revenuecat.purchases.kmp.configure
import com.revenuecat.purchases.kmp.models.CustomerInfo
import com.revenuecat.purchases.kmp.models.Package
import com.revenuecat.purchases.kmp.models.PurchasesError
import com.revenuecat.purchases.kmp.models.StoreTransaction
import com.revenuecat.purchases.kmp.ui.revenuecatui.Paywall
import com.revenuecat.purchases.kmp.ui.revenuecatui.PaywallListener
import com.revenuecat.purchases.kmp.ui.revenuecatui.PaywallOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class RevenueCatProVersionManager(
    private val appScope: CoroutineScope,
    private val apiKey: String,
    private val entitlement: Entitlement,
    private val forceIsProInDebug: StorageSetting<Boolean>,
    private val isDebug: Boolean,
    initialState: ProState = ProState.Unknown,
    private val log: Boolean = false,
) : BaseAppProVersionManager {

    private val _proState = MutableStateFlow(initialState)
    override val proState: StateFlow<ProState> = _proState

    override fun configure() {
        Purchases.configure(apiKey = apiKey)
        appScope.launch {
            forceIsProInDebug.flow.collect {
                checkProVersion()
            }
        }
    }

    override suspend fun checkProVersion(): ProState {
        val newIsPro = withContext(Dispatchers.IO) {
            L.logIf { log }?.v { "checking pro state..." }
            if (isDebug && forceIsProInDebug.read()) {
                ProState.Yes
            } else {
                val result = checkProVersionWithRevenueCat()
                L.logIf { log }?.v { "Pro Version: $result" }
                result
            }
        }
        L.logIf { log }?.v { "checking pro state: $newIsPro" }
        _proState.update { newIsPro }
        return newIsPro
    }

    suspend fun checkProVersionWithRevenueCat(): ProState =
        suspendCancellableCoroutine { continuation ->
            Purchases.sharedInstance.getCustomerInfo(
                onError = { error ->
                    continuation.resume(ProState.Unknown)
                },
                onSuccess = { customerInfo ->
                    val hasProAccess = customerInfo.entitlements.active[entitlement.id] != null
                    continuation.resume(if (hasProAccess) ProState.Yes else ProState.No)
                }
            )
        }


    @Composable
    override fun PaywallScreen(
        dialogState: DialogStateNoData,
    ) {
        if (dialogState.visible) {
            Dialog(
                onDismissRequest = {
                    dialogState.onDismiss()
                }
            ) {
                Box(
                    modifier = Modifier.clip(MaterialTheme.shapes.large)
                ) {
                    Paywall(
                        options = PaywallOptions(
                            dismissRequest = {
                                dialogState.onDismiss()
                            }
                        ) {
                            shouldDisplayDismissButton = true
                            listener = object : PaywallListener {
                                override fun onPurchaseCompleted(
                                    customerInfo: CustomerInfo,
                                    storeTransaction: StoreTransaction,
                                ) {
                                    L.logIf { log }
                                        ?.v { "onPurchaseCompleted: ${storeTransaction.transactionId}" }
                                    appScope.launch { checkProVersion() }
                                }

                                override fun onRestoreCompleted(customerInfo: CustomerInfo) {
                                    L.logIf { log }?.v { "onRestoreCompleted" }
                                    appScope.launch { checkProVersion() }
                                }

                                override fun onPurchaseCancelled() {
                                    L.logIf { log }?.v { "onPurchaseCancelled" }
                                }

                                override fun onPurchaseError(error: PurchasesError) {
                                    L.logIf { log }?.v { "onPurchaseError: ${error.message}" }
                                }

                                override fun onPurchaseStarted(rcPackage: Package) {
                                    L.logIf { log }
                                        ?.v { "onPurchaseStarted: ${rcPackage.identifier}" }
                                }

                                override fun onRestoreError(error: PurchasesError) {
                                    L.logIf { log }?.v { "onRestoreError: ${error.message}" }
                                }

                                override fun onRestoreStarted() {
                                    L.logIf { log }?.v { "onRestoreStarted" }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}