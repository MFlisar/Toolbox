package com.michaelflisar.toolbox.form3

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.components.MyDropdown
import com.michaelflisar.toolbox.components.MyDropdownIndex
import com.michaelflisar.toolbox.components.MyInput
import com.michaelflisar.toolbox.components.MyLabeledInformationDefaults
import com.michaelflisar.toolbox.components.MyLabeledInformationHorizontal
import com.michaelflisar.toolbox.components.MyPicker
import com.michaelflisar.toolbox.components.MyText
import com.michaelflisar.toolbox.components.MyTitle
import com.michaelflisar.toolbox.extensions.Icon
import com.michaelflisar.toolbox.extensions.disabled
import com.michaelflisar.toolbox.padding

data class FormConfig(
    val labelWidth: Dp = 96.dp,
    val spacing: Dp = 8.dp,
    val showSingleDropdownsAsText: Boolean = false,
    val customLabelStyle: TextStyle? = null,
)

sealed interface IFormItem

data class FormSection(
    val title: String,
    val items: List<FormItem>,
    val enabled: Boolean = true,
    val visible: Boolean = true,
) : IFormItem

sealed interface FormItem : IFormItem {

    val label: String

    val visible: Boolean
        get() = true

    val enabled: Boolean
        get() = true

    @Composable
    fun Content(config: FormConfig, parentIsEnabled: Boolean)

    data class Info(
        override val label: String,
        val value: String,
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
        override val visible: Boolean = true,
        override val enabled: Boolean = true,
    ) : FormItem {
        @Composable
        override fun Content(config: FormConfig, parentIsEnabled: Boolean) {
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
            }

        }
    }

    data class Dropdown<T>(
        override val label: String,
        val items: List<T>,
        val mapper: @Composable (T) -> String,
        val selected: MutableState<T>,
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
    config: FormConfig = FormConfig(),
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
                        text = item.title,
                        color = MaterialTheme.colorScheme.primary
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
    MyLabeledInformationHorizontal(
        label = item.label,
        labelWidth = config.labelWidth,
        modifier = Modifier.fillMaxWidth(),
        labelStyle = config.customLabelStyle ?: MyLabeledInformationDefaults.defaultLabelStyle(),
    ) {
        item.Content(config, parentIsEnabled)
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