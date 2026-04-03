package com.michaelflisar.toolbox.proversion

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.ui.graphics.vector.ImageVector
import com.michaelflisar.toolbox.drawables.Crown
import io.github.hyochan.kmpiap.ProductQueryType

class Product(
    val id: String,
    val isConsumable: Boolean = false,
    val type: Type = Type.InApp,
    val iosAmount: Int = 1,
    val icon: ImageVector = Crown,
) {
    enum class Type {
        InApp,
        Subscription
        ;

        internal val productQueryType: ProductQueryType
            get() = when (this) {
                InApp -> ProductQueryType.InApp
                Subscription -> ProductQueryType.Subs
            }
    }


    companion object {

        val ANDROID_TEST_PRODUCTS = listOf(
            "android.test.purchased", // Always succeeds
            "android.test.canceled",   // Always cancelled
            "android.test.refunded",   // Always refunded
            "android.test.item_unavailable" // Always unavailable
        ).map {
            Product(
                id = it,
                isConsumable = false,
                type = Type.InApp,
                icon = Icons.Default.BugReport
            )
        }
    }
}