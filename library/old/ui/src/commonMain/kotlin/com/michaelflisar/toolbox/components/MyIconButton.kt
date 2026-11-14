package com.michaelflisar.toolbox.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.SemanticsModifierNode
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.invalidateSemantics
import androidx.compose.ui.node.traverseAncestors
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.michaelflisar.toolbox.IconComposable
import com.michaelflisar.toolbox.theme.ComponentStyles
import com.michaelflisar.toolbox.theme.LocalComponentOptions
import com.michaelflisar.toolbox.theme.LocalComponentStyles
import com.michaelflisar.toolbox.theme.MyThemeType
import com.michaelflisar.toolbox.theme.icon

private object MyIconButton {

    @Stable
    data class Style(
        val type: BaseType,
        val shape: Shape,
        val colors: IconButtonColors,
        val border: BorderStroke?,
    )

    // alle IconButtons außer der normale delegieren intern auf Surface Variante!
    enum class BaseType {
        Default,
        Surface

    }
}

private object MyIconButtonDefaults {

    @Composable
    fun styleDefault(
        shape: Shape = LocalComponentOptions.current.iconButton.shape,
        colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    ): MyIconButton.Style {
        return MyIconButton.Style(MyIconButton.BaseType.Default, shape, colors, null)
    }

    @Composable
    fun styleOutlined(
        shape: Shape = LocalComponentOptions.current.iconButton.outlinedShape,
        colors: IconButtonColors = IconButtonDefaults.outlinedIconButtonColors(),
        border: BorderStroke? = IconButtonDefaults.outlinedIconButtonBorder(enabled = true),
    ): MyIconButton.Style {
        return MyIconButton.Style(MyIconButton.BaseType.Surface, shape, colors, border)
    }

    @Composable
    fun styleFilled(
        shape: Shape = LocalComponentOptions.current.iconButton.filledShape,
        colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
    ): MyIconButton.Style {
        return MyIconButton.Style(MyIconButton.BaseType.Surface, shape, colors, null)
    }

    @Composable
    fun styleFilledTonal(
        shape: Shape = LocalComponentOptions.current.iconButton.filledShape,
        colors: IconButtonColors = IconButtonDefaults.filledTonalIconButtonColors(),
    ): MyIconButton.Style {
        return MyIconButton.Style(MyIconButton.BaseType.Surface, shape, colors, null)
    }
}

// -------------------------
// public API
// -------------------------

@Composable
fun MyIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = LocalComponentOptions.current.iconButton.shape,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    // content
    content: @Composable () -> Unit,
) {
    MyIconButtonImpl(
        onClick = onClick,
        modifier = modifier,
        style = MyIconButtonDefaults.styleDefault(shape = shape, colors = colors),
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun MyOutlinedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = LocalComponentOptions.current.iconButton.outlinedShape,
    colors: IconButtonColors = IconButtonDefaults.outlinedIconButtonColors(),
    border: BorderStroke? = IconButtonDefaults.outlinedIconButtonBorder(enabled = true),
    // content
    content: @Composable () -> Unit,
) {
    MyIconButtonImpl(
        onClick = onClick,
        modifier = modifier,
        style = MyIconButtonDefaults.styleOutlined(
            shape = shape,
            colors = colors,
            border = border
        ),
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun MyFilledIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = LocalComponentOptions.current.iconButton.filledShape,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
    // content
    content: @Composable () -> Unit,
) {
    MyIconButtonImpl(
        onClick = onClick,
        modifier = modifier,
        style = MyIconButtonDefaults.styleFilled(
            shape = shape,
            colors = colors
        ),
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun MyFilledTonalIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = LocalComponentOptions.current.iconButton.filledShape,
    colors: IconButtonColors = IconButtonDefaults.filledTonalIconButtonColors(),
    // content
    content: @Composable () -> Unit,
) {
    MyIconButtonImpl(
        onClick = onClick,
        modifier = modifier,
        style = MyIconButtonDefaults.styleFilledTonal(
            shape = shape,
            colors = colors
        ),
        enabled = enabled,
        interactionSource = interactionSource,
        content = content
    )
}

@Composable
fun MyIconButton(
    icon: IconComposable,
    modifier: Modifier = Modifier,
    iconPaddingValues: PaddingValues = PaddingValues(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = LocalComponentOptions.current.iconButton.shape,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    // click
    onClick: () -> Unit,
) {
    MyIconButtonImpl(
        icon = icon,
        modifier = modifier,
        style = MyIconButtonDefaults.styleDefault(shape, colors),
        iconPaddingValues = iconPaddingValues,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick
    )
}

@Composable
fun MyOutlinedIconButton(
    icon: IconComposable,
    modifier: Modifier = Modifier,
    iconPaddingValues: PaddingValues = PaddingValues(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = LocalComponentOptions.current.iconButton.outlinedShape,
    colors: IconButtonColors = IconButtonDefaults.outlinedIconButtonColors(),
    border: BorderStroke? = IconButtonDefaults.outlinedIconButtonBorder(enabled = true),
    // click
    onClick: () -> Unit,
) {
    MyIconButtonImpl(
        icon = icon,
        modifier = modifier.then(Modifier.size(MaterialTheme.icon.size)),
        style = MyIconButtonDefaults.styleOutlined(
            shape = shape,
            colors = colors,
            border = border
        ),
        iconPaddingValues = iconPaddingValues,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick
    )
}

@Composable
fun MyFilledIconButton(
    icon: IconComposable,
    modifier: Modifier = Modifier,
    iconPaddingValues: PaddingValues = PaddingValues(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = LocalComponentOptions.current.iconButton.filledShape,
    colors: IconButtonColors = IconButtonDefaults.filledIconButtonColors(),
    // click
    onClick: () -> Unit,
) {
    MyIconButtonImpl(
        icon = icon,
        modifier = modifier.then(Modifier.size(MaterialTheme.icon.size)),
        style = MyIconButtonDefaults.styleFilled(
            shape = shape,
            colors = colors
        ),
        iconPaddingValues = iconPaddingValues,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick
    )
}

@Composable
fun MyFilledTonalIconButton(
    icon: IconComposable,
    modifier: Modifier = Modifier,
    iconPaddingValues: PaddingValues = PaddingValues(),
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    // style
    shape: Shape = LocalComponentOptions.current.iconButton.filledShape,
    colors: IconButtonColors = IconButtonDefaults.filledTonalIconButtonColors(),
    // click
    onClick: () -> Unit,
) {
    MyIconButtonImpl(
        icon = icon,
        modifier = modifier.then(Modifier.size(MaterialTheme.icon.size)),
        style = MyIconButtonDefaults.styleFilledTonal(
            shape = shape,
            colors = colors
        ),
        iconPaddingValues = iconPaddingValues,
        enabled = enabled,
        interactionSource = interactionSource,
        onClick = onClick
    )
}

// -------------------------
// internal API
// -------------------------

@Composable
private fun MyIconButtonImpl(
    onClick: () -> Unit,
    modifier: Modifier,
    style: MyIconButton.Style,
    enabled: Boolean,
    interactionSource: MutableInteractionSource?,
    content: @Composable () -> Unit,
) {
    MyIconButtonImpl(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        type = style.type,
        shape = style.shape,
        colors = style.colors,
        border = style.border,
        interactionSource = interactionSource
    ) {
        content()
    }
}

@Composable
private fun MyIconButtonImpl(
    icon: IconComposable,
    modifier: Modifier = Modifier,
    style: MyIconButton.Style,
    iconPaddingValues: PaddingValues,
    enabled: Boolean,
    interactionSource: MutableInteractionSource?,
    onClick: () -> Unit,
) {
    MyIconButtonImpl(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        type = style.type,
        shape = style.shape,
        colors = style.colors,
        border = style.border,
        interactionSource = interactionSource
    ) {
        com.michaelflisar.toolbox.extensions.Icon(
            icon = icon,
            modifier = Modifier
                .size(MaterialTheme.icon.size)
                .padding(iconPaddingValues)
        )
    }
}

@Composable
private fun MyIconButtonImpl(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    type: MyIconButton.BaseType,
    shape: Shape,
    colors: IconButtonColors,
    border: BorderStroke?,
    interactionSource: MutableInteractionSource?,
    content: @Composable () -> Unit,
) {
    val componentType = LocalComponentStyles.current.type
    when (type) {
        MyIconButton.BaseType.Default -> {
            when (componentType) {
                MyThemeType.Custom -> {
                    CustomIconButtonImpl(
                        modifier = modifier,
                        onClick = onClick,
                        enabled = enabled,
                        shape = shape,
                        colors = colors,
                        interactionSource = interactionSource,
                        content = content
                    )
                }

                MyThemeType.Material3 -> {
                    IconButton(
                        onClick = onClick,
                        modifier = modifier,
                        enabled = enabled,
                        colors = colors,
                        interactionSource = interactionSource,
                        shape = shape,
                        content = content
                    )
                }
            }
        }

        MyIconButton.BaseType.Surface -> {
            when (componentType) {
                MyThemeType.Custom -> {
                    CustomSurfaceIconButton(
                        modifier = modifier,
                        onClick = onClick,
                        enabled = enabled,
                        shape = shape,
                        colors = colors,
                        border = border,
                        interactionSource = interactionSource,
                        content = content
                    )
                }

                MyThemeType.Material3 -> {
                    OutlinedIconButton(
                        onClick = onClick,
                        modifier = modifier,
                        enabled = enabled,
                        colors = colors,
                        border = border,
                        interactionSource = interactionSource,
                        shape = shape,
                        content = content
                    )
                }
            }
        }
    }


}

// -------------------------
// Custom Composables
// -------------------------

@Composable
private fun CustomIconButtonImpl(
    modifier: Modifier,
    onClick: () -> Unit,
    enabled: Boolean,
    shape: Shape,
    colors: IconButtonColors,
    interactionSource: MutableInteractionSource?,
    content: @Composable () -> Unit,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    Box(
        modifier =
            modifier
                .minimumInteractiveComponentSize()
                .size(LocalComponentStyles.current.iconButton.size)
                .clip(shape)
                .background(
                    color = if (enabled) colors.containerColor else colors.disabledContainerColor,
                    shape = shape
                )
                .clickable(
                    onClick = onClick,
                    enabled = enabled,
                    role = Role.Button,
                    interactionSource = interactionSource,
                    indication = ripple(),
                )
                .childSemantics(),
        contentAlignment = Alignment.Center,
    ) {
        val contentColor = if (enabled) colors.contentColor else colors.disabledContentColor
        CompositionLocalProvider(LocalContentColor provides contentColor, content = content)
    }
}

@Composable
private fun CustomSurfaceIconButton(
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    shape: Shape,
    colors: IconButtonColors,
    border: BorderStroke?,
    interactionSource: MutableInteractionSource?,
    content: @Composable () -> Unit,
) =
    Surface(
        onClick = onClick,
        modifier = modifier.semantics { role = Role.Button },
        enabled = enabled,
        shape = shape,
        color = if (enabled) colors.containerColor else colors.disabledContainerColor,
        contentColor = if (enabled) colors.contentColor else colors.disabledContentColor,
        border = border,
        interactionSource = interactionSource,
    ) {
        Box(
            modifier = Modifier.size(MaterialTheme.icon.size),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }

private fun Modifier.childSemantics(properties: SemanticsPropertyReceiver.() -> Unit = {}) =
    this then ChildSemanticsNodeElement(properties)

private class ChildSemanticsNodeElement(val properties: SemanticsPropertyReceiver.() -> Unit) :
    ModifierNodeElement<ChildSemanticsNode>() {
    override fun create(): ChildSemanticsNode = ChildSemanticsNode(properties)

    override fun update(node: ChildSemanticsNode) {
        node.properties = properties
        node.invalidateSemantics()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "childSemantics"
        this@inspectableProperties.properties["properties"] =
            this@ChildSemanticsNodeElement.properties
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChildSemanticsNodeElement) return false

        return properties === other.properties
    }

    override fun hashCode(): Int {
        return properties.hashCode()
    }
}

internal class ParentSemanticsNodeElement(val properties: SemanticsPropertyReceiver.() -> Unit) :
    ModifierNodeElement<ParentSemanticsNode>() {
    override fun create(): ParentSemanticsNode = ParentSemanticsNode(properties)

    override fun update(node: ParentSemanticsNode) {
        node.properties = properties
        node.invalidateSemantics()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "parentSemantics"
        this@inspectableProperties.properties["properties"] =
            this@ParentSemanticsNodeElement.properties
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ParentSemanticsNodeElement) return false

        return properties === other.properties
    }

    override fun hashCode(): Int {
        return properties.hashCode()
    }
}

internal class ChildSemanticsNode(var properties: SemanticsPropertyReceiver.() -> Unit) :
    Modifier.Node(), SemanticsModifierNode {

    override fun SemanticsPropertyReceiver.applySemantics() {
        traverseAncestors(ParentSemanticsNodeKey) { node ->
            with(node as ParentSemanticsNode) {
                obtainSemantics()
                false
            }
        }
        properties()
    }

    override fun onDetach() {
        super.onDetach()
        traverseAncestors(ParentSemanticsNodeKey) { node ->
            (node as ParentSemanticsNode)
            node.releaseSemantics()
            false
        }
    }
}

internal class ParentSemanticsNode(var properties: SemanticsPropertyReceiver.() -> Unit) :
    Modifier.Node(), TraversableNode, SemanticsModifierNode {

    private var semanticsConsumed: Boolean = false

    override val shouldMergeDescendantSemantics: Boolean
        get() = true

    override val traverseKey: Any = ParentSemanticsNodeKey

    override fun SemanticsPropertyReceiver.applySemantics() {
        if (!semanticsConsumed) {
            properties()
        }
    }

    fun SemanticsPropertyReceiver.obtainSemantics() {
        semanticsConsumed = true
        properties()
        invalidateSemantics()
    }

    fun releaseSemantics() {
        semanticsConsumed = false
        invalidateSemantics()
    }
}

internal object ParentSemanticsNodeKey