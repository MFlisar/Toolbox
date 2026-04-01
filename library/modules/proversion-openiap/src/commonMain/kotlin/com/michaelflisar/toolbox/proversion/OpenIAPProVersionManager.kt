package com.michaelflisar.toolbox.proversion

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.michaelflisar.composedialogs.core.Dialog
import com.michaelflisar.composedialogs.core.DialogStateNoData
import com.michaelflisar.composedialogs.dialogs.info.DialogInfo
import com.michaelflisar.kotpreferences.core.interfaces.StorageSetting
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.dlg_getting_pro_error_info
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_info
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_info_already_bought
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_item_owned
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_restore_purchases
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_retry_loading_data
import com.michaelflisar.toolbox.core.resources.settings_pro_version_pro_version_title
import com.michaelflisar.toolbox.features.proversion.BaseAppProVersionManager
import com.michaelflisar.toolbox.features.proversion.ProState
import com.michaelflisar.toolbox.proversion.classes.FullProduct
import com.michaelflisar.toolbox.proversion.classes.OpenIAPManager
import io.github.hyochan.kmpiap.KmpIAP
import io.github.hyochan.kmpiap.KmpInAppPurchase
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
import org.jetbrains.compose.resources.stringResource

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
        dialogState: DialogStateNoData,
    ) {
        val proState = proState.collectAsState()
        if (proState.value == ProState.Yes) {
            DialogInfo(
                state = dialogState,
                title = { Text(stringResource(Res.string.settings_pro_version_pro_version_title)) },
                info = stringResource(Res.string.dlg_pro_version_info_already_bought)
            )
        } else {
            Dialog(
                state = dialogState,
                title = { Text(stringResource(Res.string.settings_pro_version_pro_version_title)) },
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val data = iapManager.data.value
                    if (data == null) {
                        LoadingProducts()
                    } else {
                        val p = data.getOrNull()
                        if (data.isFailure || p == null) {
                            LoadingProductsError(data.exceptionOrNull()?.message ?: "")
                            Retry()
                        } else {
                            if (p.products.isEmpty()) {
                                // sollte nie passieren!
                                LoadingProductsError("No products found")
                            } else {
                                Text(stringResource(infoTextResource))
                                Products(p.products, p.purchases)
                                RestorePurchase()
                            }
                        }
                    }
                }
            }
        }
    }


    @Composable
    private fun LoadingProducts() {
        CircularProgressIndicator()
    }

    @Composable
    private fun LoadingProductsError(
        error: String,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                modifier = Modifier.size(48.dp),
                contentDescription = null
            )
            Text(stringResource(Res.string.dlg_getting_pro_error_info))
        }
        if (error.isNotEmpty()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }
    }

    @Composable
    private fun Products(
        products: List<FullProduct>,
        purchases: List<Purchase>,
    ) {
        Text(stringResource(Res.string.dlg_pro_version_info))
        products.forEach { product ->
            Product(
                product = product,
                owned = purchases.find { it.productId == product.id } != null)
        }
    }

    @Composable
    private fun Product(
        product: FullProduct,
        owned: Boolean,
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .clickable(enabled = !owned) {
                        appScope.launch {
                            iapManager.purchaseProduct(product)
                        }
                    }
            ) {
                Icon(
                    imageVector = product.icon,
                    modifier = Modifier.size(24.dp),
                    contentDescription = null
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = product.displayName,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = product.displayPrice,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (owned) {
                Text(stringResource(Res.string.dlg_pro_version_item_owned))
            }
        }
    }

    @Composable
    private fun RestorePurchase() {
        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                appScope.launch { iapManager.restorePurchases() }

            }
        ) {
            Text(stringResource(Res.string.dlg_pro_version_restore_purchases))
        }
    }

    @Composable
    private fun Retry() {
        OutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                appScope.launch {
                    iapManager.refreshData()
                    checkProVersion()
                }
            }
        ) {
            Text(stringResource(Res.string.dlg_pro_version_retry_loading_data))
        }
    }
}

