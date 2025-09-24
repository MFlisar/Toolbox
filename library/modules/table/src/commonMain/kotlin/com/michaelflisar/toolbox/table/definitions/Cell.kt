package com.michaelflisar.toolbox.table.definitions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign

sealed class Cell<CellValue> {

    abstract val value: CellValue
    abstract val verticalCellAlignment: Alignment.Vertical

    abstract fun sort(): Comparable<*>

    abstract fun displayValue(): String

    @Composable
    abstract fun render(modifier: Modifier)

    class Text(
        override val value: String,
        val color: Color = Color.Unspecified,
        val textStyle: TextStyle? = null,
        val fontWeight: FontWeight? = null,
        val textAlign: TextAlign = TextAlign.Start,
        val maxLines: Int = Int.MAX_VALUE,
        val minLines: Int = 1,
        override val verticalCellAlignment: Alignment.Vertical = Alignment.Top,
    ) : Cell<String>() {

        override fun sort() = value

        override fun displayValue() = value

        @Composable
        override fun render(modifier: Modifier) {
            Text(
                modifier = modifier,
                text = value,
                style = textStyle ?: LocalTextStyle.current,
                fontWeight = fontWeight,
                color = color,
                textAlign = textAlign,
                maxLines = maxLines,
                minLines = minLines
            )
        }
    }

    class Custom<T>(
        override val value: T,
        val valueToText: (T) -> String = { it.toString() },
        override val verticalCellAlignment: Alignment.Vertical = Alignment.Top,
        val cell: @Composable (modifier: Modifier) -> Unit,
    ) : Cell<T>() {

        override fun sort() = valueToText(value)

        override fun displayValue() = valueToText(value)

        @Composable
        override fun render(modifier: Modifier) {
            cell(modifier)
        }
    }

    class Data<T>(
        override val value: T,
        val valueToText: (T) -> String,
        val color: Color = Color.Unspecified,
        val textStyle: TextStyle? = null,
        val textAlign: TextAlign = TextAlign.Start,
        val fontWeight: FontWeight? = null,
        val maxLines: Int = Int.MAX_VALUE,
        val minLines: Int = 1,
        override val verticalCellAlignment: Alignment.Vertical = Alignment.Top,
    ) : Cell<T>() {

        override fun sort() = valueToText(value)

        override fun displayValue() = valueToText(value)

        @Composable
        override fun render(modifier: Modifier) {
            Text(
                modifier = modifier,
                text = valueToText(value),
                style = textStyle ?: LocalTextStyle.current,
                fontWeight = fontWeight,
                color = color,
                textAlign = textAlign,
                minLines = minLines,
                maxLines = maxLines
            )
        }
    }

    class Enum<T : kotlin.Enum<T>>(
        override val value: T,
        val color: Color = Color.Unspecified,
        val textStyle: TextStyle? = null,
        val textAlign: TextAlign = TextAlign.Start,
        val fontWeight: FontWeight? = null,
        val maxLines: Int = Int.MAX_VALUE,
        val minLines: Int = 1,
        override val verticalCellAlignment: Alignment.Vertical = Alignment.Top,
        val valueToString: (T) -> String = { it.name }
    ) : Cell<T>() {

        override fun sort() = valueToString()

        override fun displayValue() = valueToString()

        @Composable
        override fun render(modifier: Modifier) {
            Text(
                modifier = modifier,
                text = valueToString(),
                style = textStyle ?: LocalTextStyle.current,
                fontWeight = fontWeight,
                color = color,
                textAlign = textAlign,
                minLines = minLines,
                maxLines = maxLines
            )
        }
    }

    class Number<T>(
        override val value: T,
        val color: Color = Color.Unspecified,
        val textStyle: TextStyle? = null,
        val textAlign: TextAlign = TextAlign.Start,
        val fontWeight: FontWeight? = null,
        val maxLines: Int = Int.MAX_VALUE,
        val minLines: Int = 1,
        override val verticalCellAlignment: Alignment.Vertical = Alignment.Top,
    ) : Cell<T>() where T : kotlin.Number, T : Comparable<T> {

        override fun sort() = value

        override fun displayValue() = value.toString()

        @Composable
        override fun render(modifier: Modifier) {
            Text(
                modifier = modifier,
                text = value.toString(),
                style = textStyle ?: LocalTextStyle.current,
                fontWeight = fontWeight,
                color = color,
                textAlign = textAlign,
                minLines = minLines,
                maxLines = maxLines
            )
        }
    }

    class Checkmark(
        override val value: Boolean,
        val color: Color? = null,
        val horizontalAlignment: Alignment.Horizontal = Alignment.Start,
        override val verticalCellAlignment: Alignment.Vertical = Alignment.Top,
    ) : Cell<Boolean>() {

        override fun sort() = if (value) 1 else 0

        override fun displayValue() = if (value) "Checked" else "Unchecked"

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