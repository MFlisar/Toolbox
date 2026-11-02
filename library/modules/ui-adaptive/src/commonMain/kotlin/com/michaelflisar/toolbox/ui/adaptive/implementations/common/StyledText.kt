package com.michaelflisar.toolbox.ui.adaptive.implementations.common

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledContentColor
import com.michaelflisar.toolbox.ui.adaptive.tokens.LocalStyledTextStyle

/**
 * Foundation text inheriting the current styled text style and content color.
 * Explicit parameters override [style]; color falls back to the styled content color.
 */
@Composable
fun StyledText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalStyledTextStyle.current,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = resolvedTextStyle(
            style, color, fontSize, fontStyle, fontWeight, fontFamily,
            letterSpacing, textDecoration, textAlign, lineHeight,
        ),
        onTextLayout = onTextLayout,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
    )
}

/** [StyledText] supporting styled spans, links and inline composable content. */
@Composable
fun StyledText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    inlineContent: Map<String, InlineTextContent> = emptyMap(),
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalStyledTextStyle.current,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = resolvedTextStyle(
            style, color, fontSize, fontStyle, fontWeight, fontFamily,
            letterSpacing, textDecoration, textAlign, lineHeight,
        ),
        inlineContent = inlineContent,
        onTextLayout = onTextLayout,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
    )
}

@Composable
private fun resolvedTextStyle(
    style: TextStyle,
    color: Color,
    fontSize: TextUnit,
    fontStyle: FontStyle?,
    fontWeight: FontWeight?,
    fontFamily: FontFamily?,
    letterSpacing: TextUnit,
    textDecoration: TextDecoration?,
    textAlign: TextAlign?,
    lineHeight: TextUnit,
): TextStyle {
    val resolvedColor = when {
        color != Color.Unspecified -> color
        style.color != Color.Unspecified -> style.color
        else -> LocalStyledContentColor.current
    }
    return style.merge(
        color = resolvedColor,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign ?: style.textAlign,
        lineHeight = lineHeight,
    )
}