package com.michaelflisar.toolbox.proversion.classes

import androidx.compose.ui.graphics.vector.ImageVector
import com.michaelflisar.toolbox.proversion.Product
import io.github.hyochan.kmpiap.Product as IAPProduct

class FullProduct(
    private val product: Product,
    private val iapProduct: IAPProduct,
) {
    val id: String
        get() = product.id
    val iosAmount: Int
        get() = product.iosAmount
    val displayName: String
        get() = iapProduct.displayName ?: iapProduct.title
    val displayPrice: String
        get() = iapProduct.displayPrice
    val icon: ImageVector
        get() = product.icon
}