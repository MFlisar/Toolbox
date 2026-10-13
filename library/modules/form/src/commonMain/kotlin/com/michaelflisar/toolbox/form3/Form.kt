package com.michaelflisar.toolbox.form3

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.components.MyDropdown
import com.michaelflisar.toolbox.components.MyDropdownIndex
import com.michaelflisar.toolbox.components.MyInput
import com.michaelflisar.toolbox.components.MyPicker
import com.michaelflisar.toolbox.components.MyText
import com.michaelflisar.toolbox.components.MyTitle
import com.michaelflisar.toolbox.extensions.Icon
import com.michaelflisar.toolbox.extensions.disabled
import com.michaelflisar.toolbox.padding
import com.michaelflisar.toolbox.spacing

data class FormConfig(
    val labelStyle: FormLabelStyle,
    val sectionStyle: FormSectionStyle,
    val spacing: Dp,
    val showSingleDropdownsAsText: Boolean,
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
        backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
        shape: Shape = MaterialTheme.shapes.small,
        contentPadding: PaddingValues = PaddingValues(all = MaterialTheme.padding.default),
        textStyle: TextStyle = MaterialTheme.typography.titleMedium,
        textColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
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
}

@Composable
fun rememberFormConfig(
    labelStyle: FormLabelStyle = FormDefaults.labelStyleBefore(),
    sectionStyle: FormSectionStyle = FormDefaults.sectionStyleText(),
    spacing: Dp = 8.dp,
    showSingleDropdownsAsText: Boolean = false,
): FormConfig {
    return remember(
        labelStyle,
        sectionStyle,
        spacing,
        showSingleDropdownsAsText,
        labelStyle
    ) {
        FormConfig(
            labelStyle = labelStyle,
            sectionStyle = sectionStyle,
            spacing = spacing,
            showSingleDropdownsAsText = showSingleDropdownsAsText
        )
    }
}

sealed interface IFormItem

data class FormSection(
    val title: String,
    val items: List<FormItem>,
    val enabled: Boolean = true,
    val visible: Boolean = true,
) : IFormItem

sealed interface FormItem : IFormItem {

    val label: String

    val subLabel: String?

    val visible: Boolean
        get() = true

    val enabled: Boolean
        get() = true

    @Composable
    fun Content(config: FormConfig, parentIsEnabled: Boolean)

    data class Info(
        override val label: String,
        val value: String,
        override val subLabel: String? = null,
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
    ) : FormItem {
        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
            Info(text = value)
        }
    }

    data class Picker(
        override val label: String,
        val value: String,
        val icon: IconComposable,
        override val subLabel: String? = null,
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
        val onClick: () -> Unit,
    ) : FormItem {
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
        val value: MutableState<String>,
        val minLines: Int = 1,
        val maxLines: Int = 1,
        override val subLabel: String? = null,
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
    ) : FormItem {
        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
            MyInput(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                enabled = enabled && parentIsEnabled,
                minLines = minLines,
                maxLines = maxLines
            )
        }
    }

    data class Checkbox(
        override val label: String,
        val checked: MutableState<Boolean>,
        override val subLabel: String? = null,
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
    ) : FormItem {

        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {

            androidx.compose.material3.Checkbox(
                checked = checked.value,
                onCheckedChange = { checked.value = it },
                enabled = enabled && parentIsEnabled
            )

            /*
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.extraSmall)
                    .clickable(enabled = enabled && parentIsEnabled) {
                        if (enabled && parentIsEnabled)
                            checked.value = !checked.value
                    }
                    .padding(MaterialTheme.padding.default),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                androidx.compose.material3.Checkbox(
                    checked = checked.value,
                    onCheckedChange = null
                )
            }*/
        }
    }

    data class Dropdown<T>(
        override val label: String,
        val items: List<T>,
        val mapper: @Composable (T) -> String,
        val selected: MutableState<T>,
        override val subLabel: String? = null,
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
    ) : FormItem {
        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
            if (config.showSingleDropdownsAsText && items.size == 1 && selected.value == items.first()) {
                Info(text = mapper(items.first()))
                return
            }
            MyDropdown(
                title = "",
                modifier = Modifier.fillMaxWidth(),
                items = items,
                selected = selected,
                mapper = mapper,
                enabled = enabled && parentIsEnabled
            )
        }
    }

    data class DropdownIndex(
        override val label: String,
        val items: List<String>,
        val selectedIndex: MutableState<Int>,
        override val subLabel: String? = null,
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
    ) : FormItem {
        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
            if (config.showSingleDropdownsAsText && items.size == 1 && selectedIndex.value == 0) {
                Info(text = items.first())
                return
            }
            MyDropdownIndex(
                title = "",
                modifier = Modifier.fillMaxWidth(),
                items = items,
                selectedIndex = selectedIndex,
                enabled = enabled && parentIsEnabled
            )
        }
    }
}

@Composable
fun MyForm(
    items: List<IFormItem>,
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
    config: FormConfig = rememberFormConfig(),
) {
    Column(
        modifier = (if (scrollable) modifier.verticalScroll(rememberScrollState()) else modifier)
            .animateContentSize(),
        verticalArrangement = Arrangement.spacedBy(config.spacing)
    ) {
        items.forEach { item ->
            when (item) {
                is FormItem -> {
                    FormItem(
                        item = item,
                        config = config,
                        parentIsEnabled = true
                    )
                }

                is FormSection -> {
                    if (!item.visible)
                        return@forEach
                    MyTitle(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = config.sectionStyle.verticalPadding)
                            .then(
                                when (config.sectionStyle) {
                                    is FormSectionStyle.Row -> Modifier
                                        .clip(config.sectionStyle.shape)
                                        .background(config.sectionStyle.backgroundColor)
                                        .padding(config.sectionStyle.contentPadding)

                                    is FormSectionStyle.Text -> Modifier
                                }
                            ),
                        text = item.title,
                        style = config.sectionStyle.textStyle,
                        color = config.sectionStyle.textColor,
                    )
                    item.items.forEach {
                        FormItem(
                            item = it,
                            config = config,
                            parentIsEnabled = item.enabled
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FormItem(
    item: FormItem,
    config: FormConfig,
    parentIsEnabled: Boolean,
) {
    if (!item.visible)
        return

    val before = config.labelStyle is FormLabelStyle.Before || item is FormItem.Checkbox

    if (before) {
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = item.label,
                    style = config.labelStyle.textStyle,
                    color = config.labelStyle.textColor
                )
                item.subLabel?.let { subLabel ->
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = subLabel,
                        style = config.labelStyle.subTextStyle,
                        color = config.labelStyle.subTextColor
                    )
                }
            }

            item.Content(config, parentIsEnabled)
        }
    } else {
        Column(
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

@Composable
private fun Info(
    text: String,
) {
    MyText(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraSmall)
            //.border(1.dp, MaterialTheme.colorScheme.outline.disabled(), MaterialTheme.shapes.extraSmall)
            .padding(all = MaterialTheme.padding.default)
        //.minimumInteractiveComponentSize()
        ,
        //style = LocalTextStyle.current.copy(fontStyle = FontStyle.Italic),
        color = LocalContentColor.current.disabled()
    )
}