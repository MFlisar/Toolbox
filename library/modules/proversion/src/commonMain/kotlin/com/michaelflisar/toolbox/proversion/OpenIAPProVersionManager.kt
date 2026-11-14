package com.michaelflisar.toolbox.proversion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.michaelflisar.composestyled.ui.state.VisibilityDialogState
import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_info
import com.michaelflisar.toolbox.features.proversion.BaseAppProVersionManager
import com.michaelflisar.toolbox.features.proversion.ProState
import com.michaelflisar.toolbox.proversion.classes.OpenIAPManager
import com.michaelflisar.toolbox.proversion.dialogs.ProVersionAlreadyBoughtDialog
import com.michaelflisar.toolbox.proversion.dialogs.ProVersionPaywallDialog
import io.github.hyochan.kmpiap.Purchase
import io.github.hyochan.kmpiap.kmpIapInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.StringResource

class OpenIAPProVersionManager(
    private val appScope: CoroutineScope,
    private val products: List<Product>,
    private val forceIsProInDebug: StorageSetting<Boolean>,
    private val isDebug: Boolean,
    private val infoTextResource: StringResource = Res.string.dlg_pro_version_info,
    initialState: ProState = ProState.Unknown,
    private val log: Boolean = false,
) : BaseAppProVersionManager {

    private val _proState = MutableStateFlow(initialState)
    override val proState: StateFlow<ProState> = _proState

    private val iapManager = OpenIAPManager(appScope, products, log, kmpIapInstance) {
        _proState.update { ProState.Yes }
    }

    init {
        if (isDebug) {
            appScope.launch {
                forceIsProInDebug.flow.collect {
                    checkProVersion()
                }
            }
        }
    }

    override fun configure() {
        iapManager.configure {
            checkProVersion()
        }
    }

    override suspend fun checkProVersion(): ProState {
        val newIsPro = withContext(Dispatchers.IO) {
            L.logIf { log }?.v { "checking pro state..." }
            if (isDebug && forceIsProInDebug.read()) {
                ProState.Yes
            } else {
                val result = try {
                    iapManager.checkIfIsPurchased(products)
                } catch (e: Exception) {
                    L.logIf { log }?.e(e) { "Error checking pro version: ${e.message}" }
                    ProState.Unknown
                }
                L.logIf { log }?.v { "Pro Version: $result" }
                result
            }
        }
        L.logIf { log }?.v { "checking pro state: $newIsPro" }
        _proState.update { newIsPro }
        return newIsPro
    }

    @Composable
    override fun PaywallScreen(
        dialogState: VisibilityDialogState,
    ) {
        val proState = proState.collectAsState()
        if (proState.value == ProState.Yes) {
            ProVersionAlreadyBoughtDialog(dialogState)
        } else {
            ProVersionPaywallDialog(
                dialogState = dialogState,
                data = iapManager.data.value,
                purchaseError = iapManager.purchaseError.collectAsState().value,
                infoTextResource = infoTextResource,
                onPurchaseProduct = { product ->
                    appScope.launch {
                        iapManager.purchaseProduct(product)
                    }
                },
                onRestorePurchases = {
                    appScope.launch {
                        iapManager.restorePurchases()
                    }
                },
                onRetry = {
                    appScope.launch {
                        iapManager.refreshData()
                        checkProVersion()
                    }
                }
            )
        }
    }
}
