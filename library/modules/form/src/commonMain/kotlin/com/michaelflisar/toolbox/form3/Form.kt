package com.michaelflisar.toolbox.form3

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.components.MyDropdown
import com.michaelflisar.toolbox.components.MyDropdownIndex
import com.michaelflisar.toolbox.components.MyDropdownMenu
import com.michaelflisar.toolbox.components.MyInput
import com.michaelflisar.toolbox.components.MyMultiDropdown
import com.michaelflisar.toolbox.components.MyMultiDropdownIndex
import com.michaelflisar.toolbox.components.MyPicker
import com.michaelflisar.toolbox.components.MyText
import com.michaelflisar.toolbox.components.MyTextWithHelp
import com.michaelflisar.toolbox.components.MyTextWithHelpDefaults
import com.michaelflisar.toolbox.components.MyTitle
import com.michaelflisar.toolbox.extensions.Icon
import com.michaelflisar.toolbox.padding
import com.michaelflisar.toolbox.spacing

data class FormConfig(
    val labelStyle: FormLabelStyle,
    val sectionStyle: FormSectionStyle,
    val dividerStyle: FormDividerStyle,
    val spacing: Dp,
    val shape: Shape,
    val showSingleDropdownsAsText: Boolean,
    val popupWidthConfig: MyDropdownMenu.WidthConfig,
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
        textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
        subTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
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
        textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
        subTextStyle: TextStyle = MaterialTheme.typography.bodySmall,
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
        textStyle: TextStyle = MaterialTheme.typography.titleMedium,
        textColor: Color = MaterialTheme.colorScheme.primary,
        verticalPadding: Dp = MaterialTheme.padding.default,
    ): FormSectionStyle.Text {
        return FormSectionStyle.Text(
            textStyle = textStyle,
            textColor = textColor,
            verticalPadding = verticalPadding
        )
    }

    @Composable
    fun sectionStyleRow(
        backgroundColor: Color = MaterialTheme.colorScheme.primary,
        shape: Shape = MaterialTheme.shapes.small,
        contentPadding: PaddingValues = PaddingValues(all = MaterialTheme.padding.default),
        textStyle: TextStyle = MaterialTheme.typography.titleMedium,
        textColor: Color = MaterialTheme.colorScheme.onPrimary,
        verticalPadding: Dp = MaterialTheme.padding.default,
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
        color: Color = MaterialTheme.colorScheme.outlineVariant,
        thickness: Dp = 1.dp,
        textStyle: TextStyle = MaterialTheme.typography.labelSmall,
        textColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
        textSpacing: Dp = MaterialTheme.spacing.small,
        verticalSpacing: Dp = MaterialTheme.padding.default,
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

    val popupWidthConfig = MyDropdownMenu.WidthConfig.MatchParent()

    val multiDropdownFormatter: @Composable (selected: List<String>) -> String =
        MyMultiDropdown.formatter
}

@Composable
fun rememberFormConfig(
    labelStyle: FormLabelStyle = FormDefaults.labelStyleBefore(),
    sectionStyle: FormSectionStyle = FormDefaults.sectionStyleText(),
    dividerStyle: FormDividerStyle = FormDefaults.dividerStyle(),
    spacing: Dp = 8.dp,
    shape: Shape = MaterialTheme.shapes.small,
    showSingleDropdownsAsText: Boolean = false,
    popupWidthConfig: MyDropdownMenu.WidthConfig = FormDefaults.popupWidthConfig,
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

data class FormHelp(
    val text: AnnotatedString,
    val title: String? = null,
) {
    constructor(
        text: String,
        title: String? = null,
    ) : this(AnnotatedString(text), title)
}

sealed interface FormItem {

    val visible: Boolean
        get() = true

    val enabled: Boolean
        get() = true

    @Composable
    fun Content(config: FormConfig, parentIsEnabled: Boolean)

    sealed interface Labeled : FormItem {
        val label: String
        val subLabel: String?
        val help: FormHelp?
    }

    data class Section(
        val title: String,
        val items: List<FormItem>,
        override val enabled: Boolean = true,
        override val visible: Boolean = true,
    ) : FormItem {

        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
            Column(
                verticalArrangement = Arrangement.spacedBy(config.spacing)
            ) {
                FormRow(
                    verticalPadding = config.sectionStyle.verticalPadding,
                    shape = when (config.sectionStyle) {
                        is FormSectionStyle.Row -> config.sectionStyle.shape
                        is FormSectionStyle.Text -> RectangleShape
                    },
                    backgroundColor = when (config.sectionStyle) {
                        is FormSectionStyle.Row -> config.sectionStyle.backgroundColor
                        is FormSectionStyle.Text -> Color.Unspecified
                    },
                    contentPadding = when (config.sectionStyle) {
                        is FormSectionStyle.Row -> config.sectionStyle.contentPadding
                        is FormSectionStyle.Text -> PaddingValues()
                    }
                ) {
                    MyTitle(
                        modifier = Modifier.fillMaxWidth(),
                        text = title,
                        style = config.sectionStyle.textStyle,
                        color = config.sectionStyle.textColor,
                    )
                }
                items.forEach { item ->
                    RenderFormItem(
                        item = item,
                        config = config,
                        parentIsEnabled = parentIsEnabled && enabled
                    )
                }
            }
        }
    }

    data class CustomLabeled(
        override val label: String,
        val content: @Composable (FormConfig, Boolean) -> Unit,
        override val subLabel: String? = null,
        override val help: FormHelp? = null,
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
    ) : Labeled {

        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = config.rowPaddingStart)
            ) {
                content(config, parentIsEnabled && enabled)
            }
        }
    }

    data class Custom(
        val content: @Composable (FormConfig, Boolean) -> Unit,
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
    ) : FormItem {

        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = config.rowPaddingStart)
            ) {
                content(config, parentIsEnabled && enabled)
            }

        }
    }

    data class Divider(
        val text: String? = null,
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
    ) : FormItem {

        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
            val style = config.dividerStyle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = config.rowPaddingStart)
                    .padding(vertical = style.verticalSpacing),
                horizontalArrangement = Arrangement.spacedBy(style.textSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (style.textAlignment != Alignment.Start) {
                    if (style.thickness > 0.dp) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = style.color,
                            thickness = style.thickness
                        )
                    } else {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
                if (text?.isNotEmpty() == true) {
                    Text(
                        text = text,
                        style = style.textStyle,
                        color = style.textColor
                    )
                }
                if (style.textAlignment != Alignment.End) {
                    if (style.thickness > 0.dp) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = style.color,
                            thickness = style.thickness
                        )
                    } else {
                        Box(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

    data class Info(
        override val label: String,
        val info: String,
        override val subLabel: String? = null,
        override val help: FormHelp? = null,
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
    ) : Labeled {

        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
            Info(
                modifier = Modifier.padding(vertical = MaterialTheme.spacing.small),
                text = info
            )
        }
    }

    data class Picker(
        override val label: String,
        val value: String,
        val icon: IconComposable,
        override val subLabel: String? = null,
        override val help: FormHelp? = null,
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
        val onClick: () -> Unit,
    ) : Labeled {

        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
            MyPicker(
                value = value,
                icon = { Icon(icon) },
                onStartPicker = onClick,
                enabled = enabled && parentIsEnabled
            )
        }
    }

    data class Input(
        override val label: String,
        val value: String,
        val onValueChanged: (value: String) -> Unit,
        val minLines: Int = 1,
        val maxLines: Int = 1,
        override val subLabel: String? = null,
        override val help: FormHelp? = null,
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
    ) : Labeled {

        constructor(
            label: String,
            value: MutableState<String>,
            minLines: Int = 1,
            maxLines: Int = 1,
            subLabel: String? = null,
            help: FormHelp? = null,
            visible: Boolean = true,
            enabled: Boolean = true,
        ) : this(
            label = label,
            value = value.value,
            onValueChanged = { value.value = it },
            minLines = minLines,
            maxLines = maxLines,
            subLabel = subLabel,
            help = help,
            visible = visible,
            enabled = enabled
        )

        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
            MyInput(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                onValueChanged = onValueChanged,
                enabled = enabled && parentIsEnabled,
                minLines = minLines,
                maxLines = maxLines,
                shape = config.shape
            )
        }
    }

    data class Checkbox(
        override val label: String,
        val checked: Boolean,
        val onCheckedChange: (Boolean) -> Unit,
        override val subLabel: String? = null,
        override val help: FormHelp? = null,
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
    ) : Labeled {

        constructor(
            label: String,
            checked: MutableState<Boolean>,
            subLabel: String? = null,
            help: FormHelp? = null,
            visible: Boolean = true,
            enabled: Boolean = true,
        ) : this(
            label = label,
            checked = checked.value,
            onCheckedChange = { checked.value = it },
            subLabel = subLabel,
            help = help,
            visible = visible,
            enabled = enabled
        )

        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
            androidx.compose.material3.Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled && parentIsEnabled
            )
        }
    }

    data class Dropdown<T>(
        override val label: String,
        val items: List<T>,
        val mapper: @Composable (T) -> String,
        val selected: T,
        val onSelectionChanged: (T) -> Unit,
        override val subLabel: String? = null,
        override val help: FormHelp? = null,
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
    ) : Labeled {

        constructor(
            label: String,
            items: List<T>,
            mapper: @Composable (T) -> String,
            selected: MutableState<T>,
            subLabel: String? = null,
            help: FormHelp? = null,
            visible: Boolean = true,
            enabled: Boolean = true,
        ) : this(
            label = label,
            items = items,
            mapper = mapper,
            selected = selected.value,
            onSelectionChanged = { selected.value = it },
            subLabel = subLabel,
            help = help,
            visible = visible,
            enabled = enabled
        )

        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
            if (config.showSingleDropdownsAsText && items.size == 1 && selected == items.first()) {
                Info(text = mapper(items.first()))
                return
            }
            MyDropdown(
                title = "",
                modifier = Modifier.fillMaxWidth(),
                items = items,
                selected = selected,
                onSelectionChanged = onSelectionChanged,
                mapper = mapper,
                enabled = enabled && parentIsEnabled,
                shape = config.shape,
                popupWidthConfig = config.popupWidthConfig
            )
        }
    }

    data class DropdownIndex(
        override val label: String,
        val items: List<String>,
        val selectedIndex: Int,
        val onSelectionChanged: (Int) -> Unit,
        override val subLabel: String? = null,
        override val help: FormHelp? = null,
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
    ) : Labeled {

        constructor(
            label: String,
            items: List<String>,
            selectedIndex: MutableState<Int>,
            subLabel: String? = null,
            help: FormHelp? = null,
            visible: Boolean = true,
            enabled: Boolean = true,
        ) : this(
            label = label,
            items = items,
            selectedIndex = selectedIndex.value,
            onSelectionChanged = { selectedIndex.value = it },
            subLabel = subLabel,
            help = help,
            visible = visible,
            enabled = enabled
        )

        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
            if (config.showSingleDropdownsAsText && items.size == 1 && selectedIndex == 0) {
                Info(text = items.first())
                return
            }
            MyDropdownIndex(
                title = "",
                modifier = Modifier.fillMaxWidth(),
                items = items,
                selectedIndex = selectedIndex,
                onSelectionChanged = onSelectionChanged,
                enabled = enabled && parentIsEnabled,
                shape = config.shape,
                popupWidthConfig = config.popupWidthConfig
            )
        }
    }

    data class MultiDropdown(
        override val label: String,
        val items: List<String>,
        val selected: List<String>,
        val onSelectionChanged: (List<String>) -> Unit,
        val sortSelectionByItems: Boolean = true,
        val formatter: @Composable (selected: List<String>) -> String = FormDefaults.multiDropdownFormatter,
        override val subLabel: String? = null,
        override val help: FormHelp? = null,
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
    ) : Labeled {

        constructor(
            label: String,
            items: List<String>,
            selected: MutableState<List<String>>,
            sortSelectionByItems: Boolean = true,
            formatter: @Composable (selected: List<String>) -> String = FormDefaults.multiDropdownFormatter,
            subLabel: String? = null,
            help: FormHelp? = null,
            visible: Boolean = true,
            enabled: Boolean = true,
        ) : this(
            label = label,
            items = items,
            selected = selected.value,
            onSelectionChanged = { selected.value = it },
            formatter = formatter,
            sortSelectionByItems = sortSelectionByItems,
            subLabel = subLabel,
            help = help,
            visible = visible,
            enabled = enabled
        )

        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
            MyMultiDropdown(
                title = "",
                modifier = Modifier.fillMaxWidth(),
                items = items,
                selected = selected,
                onSelectionChanged = onSelectionChanged,
                formatter = formatter,
                mapper = { it },
                enabled = enabled && parentIsEnabled,
                shape = config.shape,
                popupWidthConfig = config.popupWidthConfig,
                sortSelectionByItems = sortSelectionByItems
            )
        }
    }

    data class MultiDropdownIndex(
        override val label: String,
        val items: List<String>,
        val selectedIndices: List<Int>,
        val onSelectionChanged: (List<Int>) -> Unit,
        val sortSelectionByItems: Boolean = true,
        val formatter: @Composable (selected: List<String>) -> String = FormDefaults.multiDropdownFormatter,
        override val subLabel: String? = null,
        override val help: FormHelp? = null,
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
    ) : Labeled {

        constructor(
            label: String,
            items: List<String>,
            selectedIndices: MutableState<List<Int>>,
            sortSelectionByItems: Boolean = true,
            formatter: @Composable (selected: List<String>) -> String = FormDefaults.multiDropdownFormatter,
            subLabel: String? = null,
            help: FormHelp? = null,
            visible: Boolean = true,
            enabled: Boolean = true,
        ) : this(
            label = label,
            items = items,
            selectedIndices = selectedIndices.value,
            onSelectionChanged = { selectedIndices.value = it },
            formatter = formatter,
            sortSelectionByItems = sortSelectionByItems,
            subLabel = subLabel,
            help = help,
            visible = visible,
            enabled = enabled
        )


        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
            MyMultiDropdownIndex(
                title = "",
                modifier = Modifier.fillMaxWidth(),
                items = items,
                selectedIndices = selectedIndices,
                onSelectionChanged = onSelectionChanged,
                formatter = formatter,
                mapper = { it },
                enabled = enabled && parentIsEnabled,
                shape = config.shape,
                popupWidthConfig = config.popupWidthConfig,
                sortSelectionByItems = sortSelectionByItems
            )
        }
    }
}

@Composable
fun MyForm(
    items: List<FormItem>,
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
    config: FormConfig = rememberFormConfig(),
) {
    Column(
        modifier = modifier.then(
            if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier
        )
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(config.spacing)
    ) {
        items.forEach { item ->
            RenderFormItem(
                item = item,
                config = config,
                parentIsEnabled = true
            )
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
        when (item) {
            is FormItem.Labeled -> FormItem(
                item = item,
                config = config,
                parentIsEnabled = parentIsEnabled && item.enabled
            )

            else -> item.Content(config, parentIsEnabled && item.enabled)
        }
    }
}

@Composable
private fun FormRow(
    verticalPadding: Dp = 0.dp,
    shape: Shape = RectangleShape,
    backgroundColor: Color = Color.Unspecified,
    contentPadding: PaddingValues = PaddingValues(),
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = verticalPadding)
            .clip(shape)
            .background(backgroundColor)
            .padding(contentPadding)
    ) {
        content()
    }
}

@Composable
private fun FormItem(
    item: FormItem.Labeled,
    config: FormConfig,
    parentIsEnabled: Boolean,
) {
    AnimatedVisibility(
        modifier = Modifier.fillMaxWidth(),
        visible = item.visible
    ) {

        val before = config.labelStyle is FormLabelStyle.Before || item is FormItem.Checkbox

        val paddingStart = config.rowPaddingStart

        if (before) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = paddingStart),
                horizontalArrangement = Arrangement.SpaceBetween,//spacedBy(MaterialTheme.spacing.default),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val modifierWidth = (config.labelStyle as? FormLabelStyle.Before)?.width?.let {
                    Modifier.width(it)
                } ?: Modifier.weight(1f)

                Column(
                    modifier = modifierWidth,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    val help = item.help
                    if (help != null) {
                        MyTextWithHelp(
                            modifier = Modifier.fillMaxWidth(),
                            text = item.label,
                            help = help.text,
                            helpTitle = help.title,
                            style = config.labelStyle.textStyle,
                            color = config.labelStyle.textColor,
                            //iconVerticalAlignment = Alignment.Top,
                            popupConfig = MyTextWithHelpDefaults.popupConfig(
                                alignment = Alignment.TopCenter,
                                yOffset = 12.dp
                            )
                        )
                    } else {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = item.label,
                            style = config.labelStyle.textStyle,
                            color = config.labelStyle.textColor
                        )
                    }
                    item.subLabel?.let { subLabel ->
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = subLabel,
                            style = config.labelStyle.subTextStyle,
                            color = config.labelStyle.subTextColor
                        )
                    }
                }

                Spacer(modifier = Modifier.width(MaterialTheme.spacing.default))

                item.Content(config, parentIsEnabled)
            }
        } else {
            Column(
                modifier = Modifier.fillMaxWidth().padding(start = paddingStart),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.default)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
                ) {
                    Text(
                        text = item.label,
                        style = config.labelStyle.textStyle,
                        color = config.labelStyle.textColor
                    )
                    item.subLabel?.let { subLabel ->
                        Text(
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
private fun Info(
    text: String,
    modifier: Modifier = Modifier,
) {
    MyText(
        text = text,
        modifier = Modifier.fillMaxWidth().then(modifier),
        color = LocalContentColor.current
    )
}