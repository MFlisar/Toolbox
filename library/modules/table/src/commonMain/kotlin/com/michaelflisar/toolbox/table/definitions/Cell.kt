package com.michaelflisar.toolbox.table.definitions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

sealed class Cell<CellValue> {

    abstract val value: CellValue
    abstract val verticalCellAlignment: Alignment.Vertical

    //abstract fun filter(text: String): Boolean
    abstract fun sort(): Comparable<*>

    @Composable
    abstract fun render(modifier: Modifier)

    class Text(
        override val value: String,
        val color: Color = Color.Unspecified,
        val textStyle: TextStyle? = null,
        val fontWeight: FontWeight? = null,
        val textAlign: TextAlign = TextAlign.Start,
        override val verticalCellAlignment: Alignment.Vertical = Alignment.Top
    ) : Cell<String>() {

        //override fun filter(text: String) =
        //    text.isEmpty() || value.lowercase().contains(text, true)

        override fun sort() = value

        @Composable
        override fun render(modifier: Modifier) {
            Text(
                modifier = modifier,
                text = value,
                style = textStyle ?: LocalTextStyle.current,
                fontWeight = fontWeight,
                color = color,
                textAlign = textAlign
            )
        }
    }

    class Data<T>(
        override val value: T,
        val valueToText: (T) -> String,
        val color: Color = Color.Unspecified,
        val textStyle: TextStyle? = null,
        val textAlign: TextAlign = TextAlign.Start,
        val fontWeight: FontWeight? = null,
        override val verticalCellAlignment: Alignment.Vertical = Alignment.Top
    ) : Cell<T>() {

        //override fun filter(text: String) =
        //    text.isEmpty() || (value != null && value.toString().contains(text, true))

        override fun sort() = valueToText(value)

        @Composable
        override fun render(modifier: Modifier) {
            Text(
                modifier = modifier,
                text = valueToText(value),
                style = textStyle ?: LocalTextStyle.current,
                fontWeight = fontWeight,
                color = color,
                textAlign = textAlign
            )
        }
    }

    class Number<T>(
        override val value: T,
        val color: Color = Color.Unspecified,
        val textStyle: TextStyle? = null,
        val textAlign: TextAlign = TextAlign.Start,
        val fontWeight: FontWeight? = null,
        override val verticalCellAlignment: Alignment.Vertical = Alignment.Top
    ) : Cell<T>() where T : kotlin.Number, T : Comparable<T> {

        //override fun filter(text: String) =
        //    text.isEmpty() || (value != null && value.toString().contains(text, true))

        override fun sort() = value

        @Composable
        override fun render(modifier: Modifier) {
            Text(
                modifier = modifier,
                text = value.toString(),
                style = textStyle ?: LocalTextStyle.current,
                fontWeight = fontWeight,
                color = color,
                textAlign = textAlign
            )
        }
    }

    class Checkmark(
        override val value: Boolean,
        val color: Color? = null,
        val horizontalAlignment: Alignment.Horizontal = Alignment.Start,
        override val verticalCellAlignment: Alignment.Vertical = Alignment.Top
    ) : Cell<Boolean>() {

        //override fun filter(text: String) = text.isEmpty() ||
        //        (checked && (text.lowercase() == "j" || text.lowercase() == "y") || text.lowercase() == "1") ||
        //        (!checked && (text.lowercase() == "n" || text.lowercase() == "0"))

        override fun sort() = if (value) 1 else 0

        @Composable
        override fun render(modifier: Modifier) {
            if (value) {
                Box(
                    modifier = modifier
                ) {
                    Icon(
                        modifier = Modifier.align(
                            when (horizontalAlignment) {
                                Alignment.End -> Alignment.CenterEnd
                                Alignment.CenterHorizontally -> Alignment.Center
                                else -> Alignment.CenterStart
                            }
                        ),
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = color
                            ?: LocalContentColor.current//.copy(alpha = LocalContentAlpha.current)
                    )
                }
            } else Spacer(modifier)
        }
    }
}