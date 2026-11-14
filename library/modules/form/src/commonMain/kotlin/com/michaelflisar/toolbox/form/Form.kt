package com.michaelflisar.toolbox.form

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import com.michaelflisar.composestyled.StyledTheme
import com.michaelflisar.composestyled.classes.toIconComposable
import com.michaelflisar.composestyled.ui.components.StyledIconButton
import com.michaelflisar.composestyled.ui.components.StyledIconPlainButton
import com.michaelflisar.composestyled.ui.components.StyledPopupMenu
import com.michaelflisar.composestyled.ui.components.StyledPopupMenuDefaults
import com.michaelflisar.composestyled.ui.components.StyledTooltip
import com.michaelflisar.composestyled.ui.components.rememberStyledTooltipState
import com.michaelflisar.composestyled.ui.primitives.StyledText
import com.michaelflisar.toolbox.form.items.FormCheckbox
import com.michaelflisar.toolbox.form.items.FormCustom
import com.michaelflisar.toolbox.form.items.FormCustomLabeled
import com.michaelflisar.toolbox.form.items.FormDivider
import com.michaelflisar.toolbox.form.items.FormDropdown
import com.michaelflisar.toolbox.form.items.FormDropdownIndex
import com.michaelflisar.toolbox.form.items.FormInfo
import com.michaelflisar.toolbox.form.items.FormInput
import com.michaelflisar.toolbox.form.items.FormItem
import com.michaelflisar.toolbox.form.items.FormMultiDropdown
import com.michaelflisar.toolbox.form.items.FormMultiDropdownIndex
import com.michaelflisar.toolbox.form.items.FormPicker
import com.michaelflisar.toolbox.form.items.FormSection
import kotlinx.coroutines.launch

data class FormConfig(
    val labelStyle: FormLabelStyle,
    val sectionStyle: FormSectionStyle,
    val dividerStyle: FormDividerStyle,
    val spacing: Dp,
    val shape: Shape,
    val showSingleDropdownsAsText: Boolean,
    val popupWidthConfig: StyledPopupMenu.WidthConfig,
) {
    val rowPaddingStart: Dp
        @Composable get() = when (sectionStyle) {
            is FormSectionStyle.Row -> sectionStyle.contentPadding.calculateLeftPadding(
                LocalLayoutDirection.current
            )

            is FormSectionStyle.Text -> 0.dp
        }
}

data class FormDividerStyle(
    val color: Color,
    val thickness: Dp,
    val textStyle: TextStyle,
    val textColor: Color,
    val textSpacing: Dp,
    val verticalSpacing: Dp,
    val textAlignment: Alignment.Horizontal,
)

sealed class FormLabelStyle {

    abstract val textStyle: TextStyle
    abstract val subTextStyle: TextStyle
    abstract val textColor: Color
    abstract val subTextColor: Color

    data class Before(
        override val textStyle: TextStyle,
        override val subTextStyle: TextStyle,
        override val textColor: Color,
        override val subTextColor: Color,
        val width: Dp,
    ) : FormLabelStyle()

    data class Above(
        override val textStyle: TextStyle,
        override val subTextStyle: TextStyle,
        override val textColor: Color,
        override val subTextColor: Color,
    ) : FormLabelStyle()
}

sealed class FormSectionStyle {

    abstract val textStyle: TextStyle
    abstract val textColor: Color
    abstract val verticalPadding: Dp

    data class Text(
        override val textStyle: TextStyle,
        override val textColor: Color,
        override val verticalPadding: Dp,
    ) : FormSectionStyle()

    data class Row(
        val backgroundColor: Color,
        val shape: Shape,
        val contentPadding: PaddingValues,
        override val textStyle: TextStyle,
        override val textColor: Color,
        override val verticalPadding: Dp,
    ) : FormSectionStyle()
}

object FormDefaults {

    @Composable
    fun labelStyleBefore(
        textStyle: TextStyle = StyledTheme.typography.bodyMedium,
        subTextStyle: TextStyle = StyledTheme.typography.bodySmall,
        textColor: Color = Color.Unspecified,
        subTextColor: Color = Color.Unspecified,
        width: Dp = 96.dp,
    ): FormLabelStyle.Before {
        return FormLabelStyle.Before(
            textStyle = textStyle,
            subTextStyle = subTextStyle,
            textColor = textColor,
            subTextColor = subTextColor,
            width = width
        )
    }

    @Composable
    fun labelStyleAbove(
        textStyle: TextStyle = StyledTheme.typography.bodyMedium,
        subTextStyle: TextStyle = StyledTheme.typography.bodySmall,
        textColor: Color = Color.Unspecified,
        subTextColor: Color = Color.Unspecified,
    ): FormLabelStyle.Above {
        return FormLabelStyle.Above(
            textStyle = textStyle,
            subTextStyle = subTextStyle,
            textColor = textColor,
            subTextColor = subTextColor
        )
    }

    @Composable
    fun sectionStyleText(
        textStyle: TextStyle = StyledTheme.typography.titleMedium,
        textColor: Color = StyledTheme.colorScheme.primary,
        verticalPadding: Dp = StyledTheme.padding.medium,
    ): FormSectionStyle.Text {
        return FormSectionStyle.Text(
            textStyle = textStyle,
            textColor = textColor,
            verticalPadding = verticalPadding
        )
    }

    @Composable
    fun sectionStyleRow(
        backgroundColor: Color = StyledTheme.colorScheme.primary,
        shape: Shape = StyledTheme.shapes.small,
        contentPadding: PaddingValues = PaddingValues(all = StyledTheme.padding.medium),
        textStyle: TextStyle = StyledTheme.typography.titleMedium,
        textColor: Color = StyledTheme.colorScheme.onPrimary,
        verticalPadding: Dp = StyledTheme.padding.medium,
    ): FormSectionStyle.Row {
        return FormSectionStyle.Row(
            backgroundColor = backgroundColor,
            shape = shape,
            contentPadding = contentPadding,
            textStyle = textStyle,
            textColor = textColor,
            verticalPadding = verticalPadding
        )
    }

    @Composable
    fun dividerStyle(
        color: Color = StyledTheme.colorScheme.outlineVariant,
        thickness: Dp = 1.dp,
        textStyle: TextStyle = StyledTheme.typography.labelSmall,
        textColor: Color = StyledTheme.colorScheme.onSurfaceVariant,
        textSpacing: Dp = StyledTheme.spacing.small,
        verticalSpacing: Dp = StyledTheme.padding.medium,
        textAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    ) = FormDividerStyle(
        color = color,
        thickness = thickness,
        textStyle = textStyle,
        textColor = textColor,
        textSpacing = textSpacing,
        verticalSpacing = verticalSpacing,
        textAlignment = textAlignment
    )

    val multiDropdownFormatter: @Composable (selected: List<String>) -> String = { selected ->
        selected.joinToString(";")
    }


    fun simpleKeyProvider(): (FormItem) -> Any = { item ->
        when (item) {
            is FormSection -> "section:${item.title}"
            is FormCustomLabeled -> "customLabeled:${item.label}"
            is FormCustom -> "custom:${item.hashCode()}"
            is FormDivider -> "divider:${item.text ?: item.hashCode()}"
            is FormInfo -> "info:${item.label}"
            is FormPicker -> "picker:${item.label}"
            is FormInput -> "input:${item.label}"
            is FormCheckbox -> "checkbox:${item.label}"
            is FormDropdown<*> -> "dropdown:${item.label}"
            is FormDropdownIndex -> "dropdownIndex:${item.label}"
            is FormMultiDropdown -> "multiDropdown:${item.label}"
            is FormMultiDropdownIndex -> "multiDropdownIndex:${item.label}"
        }
    }
}

@Composable
fun rememberFormConfig(
    labelStyle: FormLabelStyle = FormDefaults.labelStyleBefore(),
    sectionStyle: FormSectionStyle = FormDefaults.sectionStyleText(),
    dividerStyle: FormDividerStyle = FormDefaults.dividerStyle(),
    spacing: Dp = StyledTheme.spacing.medium,
    shape: Shape = StyledTheme.shapes.small,
    showSingleDropdownsAsText: Boolean = false,
    popupWidthConfig: StyledPopupMenu.WidthConfig = StyledPopupMenuDefaults.widthConfig,
): FormConfig {
    return remember(
        labelStyle,
        sectionStyle,
        dividerStyle,
        spacing,
        shape,
        showSingleDropdownsAsText,
        labelStyle,
        popupWidthConfig
    ) {
        FormConfig(
            labelStyle = labelStyle,
            sectionStyle = sectionStyle,
            dividerStyle = dividerStyle,
            spacing = spacing,
            shape = shape,
            showSingleDropdownsAsText = showSingleDropdownsAsText,
            popupWidthConfig = popupWidthConfig
        )
    }
}

/**
 * [itemKey] must return a unique, stable ID independent of an item's value or visibility.
 * When supplied, inserted and removed items animate; removed items remain until their exit completes.
 * Without [itemKey], items retain the positional rendering behavior.
 */
@Composable
fun MyForm(
    items: List<FormItem>,
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
    config: FormConfig = rememberFormConfig(),
    itemKey: ((FormItem) -> Any)? = FormDefaults.simpleKeyProvider(),
) {
    Column(
        modifier = modifier.then(
            if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier
        )
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(config.spacing)
    ) {
        if (itemKey == null) {
            items.forEach { item ->
                RenderFormItem(
                    item = item,
                    config = config,
                    parentIsEnabled = true
                )
            }
        } else {
            AnimatedFormItems(items, config, itemKey)
        }
    }
}

@Composable
private fun AnimatedFormItems(
    items: List<FormItem>,
    config: FormConfig,
    itemKey: (FormItem) -> Any,
) {
    val keyedItems = items.map { itemKey(it) to it }
    val currentKeys = keyedItems.map { it.first }.toSet()
    require(currentKeys.size == keyedItems.size) { "MyForm item keys must be unique." }

    var retained by remember {
        mutableStateOf(keyedItems.map { (id, item) ->
            AnimatedFormEntry(id, item, MutableTransitionState(item.visible))
        })
    }
    val entries = reconcileFormEntries(retained, keyedItems)
    val latestKeys by rememberUpdatedState(currentKeys)

    SideEffect {
        retained = entries
        entries.forEach { entry ->
            entry.visibility.targetState = entry.id in currentKeys && entry.item.visible
        }
    }

    entries.forEach { entry ->
        key(entry.id) {
            val visibility = entry.visibility
            AnimatedVisibility(
                modifier = Modifier.fillMaxWidth(),
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
                visibleState = visibility
            ) {
                RenderFormItemContent(entry.item, config, true)
            }
            LaunchedEffect(
                visibility.isIdle,
                visibility.currentState,
                visibility.targetState,
                entry.id in currentKeys
            ) {
                if (visibility.isIdle && !visibility.currentState && entry.id !in latestKeys) {
                    retained = retained.filterNot { it.visibility === visibility }
                }
            }
        }
    }
}

@Composable
private fun RenderFormItem(
    item: FormItem,
    config: FormConfig,
    parentIsEnabled: Boolean,
) {
    AnimatedVisibility(
        modifier = Modifier.fillMaxWidth(),
        visible = item.visible
    ) {
        RenderFormItemContent(item, config, parentIsEnabled)
    }
}

@Composable
private fun RenderFormItemContent(
    item: FormItem,
    config: FormConfig,
    parentIsEnabled: Boolean,
) {
    when (item) {
        is FormItem.Labeled -> FormLabeledItem(
            item = item,
            config = config,
            parentIsEnabled = parentIsEnabled && item.enabled
        )

        else -> item.Content(config, parentIsEnabled && item.enabled)
    }
}

@Composable
private fun FormLabeledItem(
    item: FormItem.Labeled,
    config: FormConfig,
    parentIsEnabled: Boolean,
) {
    AnimatedVisibility(
        modifier = Modifier.fillMaxWidth(),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        visible = item.visible
    ) {

        val before = config.labelStyle is FormLabelStyle.Before || item is FormCheckbox

        val paddingStart = config.rowPaddingStart

        if (before) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = paddingStart),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                val modifierWidth = (config.labelStyle as? FormLabelStyle.Before)?.width?.let {
                    Modifier.width(it)
                } ?: Modifier.weight(1f)

                Column(
                    modifier = modifierWidth,
                    verticalArrangement = Arrangement.spacedBy(StyledTheme.spacing.small)
                ) {
                    val help = item.help
                    if (help != null) {
                        FormLabelWithHelp(
                            modifier = Modifier.fillMaxWidth(),
                            text = item.label,
                            help = help.text,
                            helpTitle = help.title,
                            style = config.labelStyle.textStyle,
                            color = config.labelStyle.textColor,
                        )
                    } else {
                        StyledText(
                            modifier = Modifier.fillMaxWidth(),
                            text = item.label,
                            style = config.labelStyle.textStyle,
                            color = config.labelStyle.textColor
                        )
                    }
                    item.subLabel?.let { subLabel ->
                        StyledText(
                            modifier = Modifier.fillMaxWidth(),
                            text = subLabel,
                            style = config.labelStyle.subTextStyle,
                            color = config.labelStyle.subTextColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(StyledTheme.spacing.medium))

                item.Content(config, parentIsEnabled)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = paddingStart),
                verticalArrangement = Arrangement.spacedBy(StyledTheme.spacing.medium)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(StyledTheme.spacing.small)
                ) {
                    StyledText(
                        text = item.label,
                        style = config.labelStyle.textStyle,
                        color = config.labelStyle.textColor
                    )
                    item.subLabel?.let { subLabel ->
                        StyledText(
                            text = subLabel,
                            style = config.labelStyle.subTextStyle,
                            color = config.labelStyle.subTextColor
                        )
                    }
                }
                item.Content(config, parentIsEnabled)
            }
        }
    }
}

@Composable
private fun FormLabelWithHelp(
    text: String,
    help: AnnotatedString,
    helpTitle: String?,
    modifier: Modifier,
    style: TextStyle,
    color: Color,
) {
    val state = rememberStyledTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()

    StyledTooltip(
        state = state,
        onDismissRequest = { state.dismiss() },
        tooltip = {
            Column(verticalArrangement = Arrangement.spacedBy(StyledTheme.spacing.small)) {
                helpTitle?.let {
                    StyledText(it, style = StyledTheme.typography.titleSmall)
                }
                StyledText(help)
            }
        },
        content = {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(StyledTheme.spacing.small),
            ) {
                StyledText(
                    text = text,
                    modifier = Modifier.weight(1f, fill = false),
                    style = style,
                    color = color,
                )
                StyledIconPlainButton(
                    onClick = {
                        if (state.isVisible) {
                            state.dismiss()
                        } else {
                            scope.launch { state.show() }
                        }
                    },
                    icon = Icons.Default.Info.toIconComposable(),
                    contentDescription = "Help",
                )
            }
        },
    )
}