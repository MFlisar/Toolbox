package com.michaelflisar.toolbox.proversion.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.classes.toIconComposable
import com.michaelflisar.composestyled.ui.components.StyledCircularProgressIndicator
import com.michaelflisar.composestyled.ui.components.StyledButton
import com.michaelflisar.composestyled.ui.components.StyledIcon
import com.michaelflisar.composestyled.ui.components.StyledOutlinedButton
import com.michaelflisar.composestyled.ui.components.StyledTextButton
import com.michaelflisar.composestyled.ui.layout.StyledColumn
import com.michaelflisar.composestyled.ui.layout.StyledDialog
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.composestyled.ui.state.VisibilityDialogState
import com.michaelflisar.toolbox.core.resources.Res
import com.michaelflisar.toolbox.core.resources.dlg_getting_pro_error_info
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_item_owned
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_purchase
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_purchase_error
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_restore_purchases
import com.michaelflisar.toolbox.core.resources.dlg_pro_version_retry_loading_data
import com.michaelflisar.toolbox.core.resources.settings_pro_version_title
import com.michaelflisar.toolbox.proversion.classes.FullProduct
import com.michaelflisar.toolbox.proversion.classes.IAPData
import io.github.hyochan.kmpiap.Purchase
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ProVersionPaywallDialog(
    dialogState: VisibilityDialogState,
    data: Result<IAPData>?,
    purchaseError: Boolean,
    infoTextResource: StringResource,
    onPurchaseProduct: (FullProduct) -> Unit,
    onRestorePurchases: () -> Unit,
    onRetry: () -> Unit,
) {
    StyledDialog(
        state = dialogState,
        title = { StyledText(stringResource(Res.string.settings_pro_version_title)) },
    ) { paddingValues ->
        StyledColumn(
            modifier = Modifier.fillMaxWidth().padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when {
                data == null -> LoadingProducts()
                data.isFailure -> {
                    LoadingProductsError(data.exceptionOrNull()?.message.orEmpty())
                    Retry(onRetry)
                }

                else -> {
                    val products = data.getOrThrow().products
                    if (products.isEmpty()) {
                        LoadingProductsError("No products found")
                    } else {
                        ProVersionHeroGraphic()
                        StyledText(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                            text = stringResource(infoTextResource),
                            color = StyledTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            style = StyledTheme.typography.bodyMedium
                        )
                        if (purchaseError) {
                            StyledText(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                text = stringResource(Res.string.dlg_pro_version_purchase_error),
                                color = StyledTheme.colorScheme.error,
                                textAlign = TextAlign.Center,
                                style = StyledTheme.typography.bodySmall
                            )
                        }
                        val purchases = data.getOrThrow().purchases
                        if (products.size == 1) {
                            val product = products.single()
                            SingleProduct(
                                product = product,
                                owned = purchases.any { it.productId == product.id },
                                onPurchaseProduct = onPurchaseProduct
                            )
                        } else {
                            Products(
                                products = products,
                                purchases = purchases,
                                onPurchaseProduct = onPurchaseProduct
                            )
                        }
                        RestorePurchase(onRestorePurchases)
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingProducts() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        StyledCircularProgressIndicator()
    }
}

@Composable
private fun LoadingProductsError(
    error: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StyledIcon(
            icon = Icons.Default.Warning.toIconComposable(),
            modifier = Modifier.size(48.dp),
            contentDescription = null
        )
        StyledText(stringResource(Res.string.dlg_getting_pro_error_info))
    }
    if (error.isNotEmpty()) {
        StyledText(
            text = error,
            color = StyledTheme.colorScheme.error
        )
    }
}

@Composable
private fun SingleProduct(
    product: FullProduct,
    owned: Boolean,
    onPurchaseProduct: (FullProduct) -> Unit,
) {
    if (owned) {
        StyledText(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            text = stringResource(Res.string.dlg_pro_version_item_owned),
            color = StyledTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            style = StyledTheme.typography.bodyMedium
        )
    } else {
        StyledButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onPurchaseProduct(product) }
        ) {
            StyledText(
                stringResource(Res.string.dlg_pro_version_purchase, product.displayPrice)
            )
        }
    }
}

@Composable
private fun Products(
    products: List<FullProduct>,
    purchases: List<Purchase>,
    onPurchaseProduct: (FullProduct) -> Unit,
) {
    products.forEach { product ->
        Product(
            product = product,
            owned = purchases.any { it.productId == product.id },
            onPurchaseProduct = onPurchaseProduct
        )
    }
}

@Composable
private fun Product(
    product: FullProduct,
    owned: Boolean,
    onPurchaseProduct: (FullProduct) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .clip(StyledTheme.shapes.small)
            .background(StyledTheme.colorScheme.surfaceVariant)
            .clickable(enabled = !owned) { onPurchaseProduct(product) }
            .padding(12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            StyledIcon(
                icon = product.icon.toIconComposable(),
                modifier = Modifier.size(24.dp),
                contentDescription = null
            )
            StyledText(
                modifier = Modifier.weight(1f),
                text = product.displayName,
                style = StyledTheme.typography.bodySmall
            )
            StyledText(
                text = product.displayPrice,
                style = StyledTheme.typography.bodyMedium
            )
        }
        if (owned) {
            StyledText(
                modifier = Modifier.padding(start = 32.dp, top = 4.dp),
                text = stringResource(Res.string.dlg_pro_version_item_owned),
                color = StyledTheme.colorScheme.onSurfaceVariant,
                style = StyledTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun RestorePurchase(
    onRestorePurchases: () -> Unit,
) {
    StyledTextButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onRestorePurchases
    ) {
        StyledText(stringResource(Res.string.dlg_pro_version_restore_purchases))
    }
}

@Composable
private fun Retry(
    onRetry: () -> Unit,
) {
    StyledOutlinedButton(
        modifier = Modifier.fillMaxWidth(),
        onClick = onRetry
    ) {
        StyledText(stringResource(Res.string.dlg_pro_version_retry_loading_data))
    }
}
