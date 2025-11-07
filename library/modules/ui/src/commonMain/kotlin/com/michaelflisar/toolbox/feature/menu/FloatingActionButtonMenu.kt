package com.michaelflisar.toolbox.feature.menu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.michaelflisar.toolbox.ComposeUtil
import com.michaelflisar.toolbox.LocalTheme
import com.michaelflisar.toolbox.extensions.isScrollingUp

object FloatingActionButtonMenu {

    enum class Position(val alignment: Alignment) {
        BottomEnd( Alignment.BottomEnd),
        BottomStart( Alignment.BottomStart)
    }

    @Stable
    class Setup(
        val fabSize: Dp,
        val fabIconSize: Dp,
        val maxExpandedWidth: Dp,
        val shape: Shape,
    )
}

@Composable
fun rememberFloatingActionButtonMenuSetup(
    fabSize: Dp = FloatingActionButtonMenuDefaults.fabSize,
    fabIconSize: Dp = FloatingActionButtonMenuDefaults.fabIconSize,
    maxExpandedWidth: Dp = FloatingActionButtonMenuDefaults.maxExpandedWidth,
    shape: Shape = FloatingActionButtonDefaults.shape,
): FloatingActionButtonMenu.Setup {
    return FloatingActionButtonMenu.Setup(
        fabSize = fabSize,
        fabIconSize = fabIconSize,
        maxExpandedWidth = maxExpandedWidth,
        shape = shape
    )
}

object FloatingActionButtonMenuDefaults {
    val fabSize = 56.dp
    val fabIconSize = 24.dp
    val maxExpandedWidth = 128.dp
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BoxScope.FloatingActionButtonMenu(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    listState: LazyListState? = null,
    position: FloatingActionButtonMenu.Position = FloatingActionButtonMenu.Position.BottomEnd,
    setup: FloatingActionButtonMenu.Setup = rememberFloatingActionButtonMenuSetup(),
    menu: @Composable MenuScope.() -> Unit = { },
) {
    val modifier = modifier
        .align(position.alignment)
        .padding(
            if (position == FloatingActionButtonMenu.Position.BottomStart) {
                PaddingValues(
                    bottom = LocalTheme.current.padding.content,
                    start = LocalTheme.current.padding.content
                )
            } else {
                PaddingValues(
                    bottom = LocalTheme.current.padding.content,
                    end = LocalTheme.current.padding.content
                )
            }
        )

    val isScrollingUp = listState?.isScrollingUp()
    var isButtonExpanded by remember { mutableStateOf(false) }
    var isMenuOpened by remember { mutableStateOf(false) }
    LaunchedEffect(isScrollingUp) {
        isButtonExpanded = isScrollingUp == true
        //println("isScrollingUp: $isScrollingUp")
    }

    BackHandler(enabled = isMenuOpened) {
        isMenuOpened = false
    }
    if (isMenuOpened) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type == PointerEventType.Release) {
                                isMenuOpened = false
                            }
                        }
                    }
                }
        )
    }

    val animation = tween<Dp>(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )
    val animationContent = tween<Float>(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )
    val animationContentAlpha = tween<Float>(
        durationMillis = if (isMenuOpened) 300 else 100,
        delayMillis = if (isMenuOpened) 100 else 0,
        easing = EaseIn
    )
    val animationContentAlphaValue by animateFloatAsState(
        targetValue = if (isMenuOpened) 1f else 0f,
        animationSpec = animationContentAlpha
    )
    val animationContentSize = tween<IntSize>(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )
    val animationFabTextAlpha = tween<Float>(
        durationMillis = if (isMenuOpened) 300 else 100,
        delayMillis = if (isMenuOpened) 100 else 0,
        easing = EaseIn
    )

    val density = LocalDensity.current

    //val fabElevation by animateDpAsState(
    //    targetValue = if (isExpanded) 0.dp else 6.dp,
    //    animationSpec = animation
    //)

    val contentWidth = remember { mutableStateOf(0) }
    val fabContentWidth =
        remember { mutableStateOf(0) } // TODO: messen auch wenn es nicht im layout ist
    val fabSizePx = with(density) { setup.fabSize.roundToPx() }

    val currentFabWidth by animateDpAsState(
        targetValue = if (isMenuOpened) {
            with(density) { contentWidth.value.toDp() }
        } else if (isButtonExpanded) {
            with(density) { fabContentWidth.value.toDp() }
        } else setup.fabSize,
        animationSpec = animation
    )

    //LaunchedEffect(isButtonExpanded, currentFabWidth.value, fabContentWidth.value) {
    //    println("isButtonExpanded: $isButtonExpanded | currentFabWidth = ${currentFabWidth.value} | fabContentWidth = ${fabContentWidth.value}")
    //}

    ComposeUtil.MeasureSize(
        composable = {
            FabText(
                setup = setup,
                text = text,
                animationTextAlpha = null,
                isMenuOpened = false,
                isButtonExpanded = true,
            )
        },
        onMeasured = { width, height ->
            fabContentWidth.value = fabSizePx + width
        }
    )

    Column(
        modifier = modifier
            .widthIn(max = setup.maxExpandedWidth)
            .width(IntrinsicSize.Min)
            .onSizeChanged { contentWidth.value = it.width }
    ) {
        // ExpandedBox over the FAB
        AnimatedVisibility(
            visible = isMenuOpened,
            enter = fadeIn(
                animationSpec = animationContent
            ) + expandIn(
                animationSpec = animationContentSize,
                expandFrom = when(position) {
                    FloatingActionButtonMenu.Position.BottomEnd -> Alignment.TopStart
                    FloatingActionButtonMenu.Position.BottomStart -> Alignment.TopEnd
                },
                initialSize = {
                    IntSize(
                        if (isButtonExpanded) fabContentWidth.value else fabSizePx,
                        0
                    )
                }
            ),
            exit = fadeOut(
                animationSpec = animationContent,
            ) + shrinkOut(
                animationSpec = animationContentSize,
                shrinkTowards = when(position) {
                    FloatingActionButtonMenu.Position.BottomEnd -> Alignment.TopStart
                    FloatingActionButtonMenu.Position.BottomStart -> Alignment.TopEnd
                },
                targetSize = {
                    IntSize(
                        if (isButtonExpanded) fabContentWidth.value else fabSizePx,
                        0
                    )
                }
            ),
            modifier = Modifier.align(Alignment.End)
        )
        {
            Column(
                modifier = Modifier
                    .padding(bottom = Menu.itemPadding)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainer,
                        shape = setup.shape
                    )
                    .padding(vertical = Menu.itemPadding)
                    .alpha(animationContentAlphaValue),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val state = rememberMenuState(show = true)
                val setup = rememberMenuSetup()
                LaunchedEffect(state.isShowing) {
                    if (!state.isShowing) {
                        isMenuOpened = false
                    }
                }
                ProvideMenuLocals(
                    state = state,
                    setup = setup,
                    level = -1
                ) {
                    ProvideUpdatedMenuLocals(emptyList(), 0) {
                        with(MenuScopeInstance) {
                            menu()
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { isMenuOpened = !isMenuOpened },
            modifier = Modifier
                .height(setup.fabSize)
                .width(IntrinsicSize.Min)
                .align(Alignment.End),
            shape = setup.shape
        ) {
            Row(
                modifier = Modifier
                    .width(currentFabWidth),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(setup.fabSize)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }

                FabText(
                    setup = setup,
                    text = text,
                    animationTextAlpha = animationFabTextAlpha,
                    isMenuOpened = isMenuOpened,
                    isButtonExpanded = isButtonExpanded,
                )

            }
        }
    }
}

@Composable
private fun FabText(
    setup: FloatingActionButtonMenu.Setup,
    text: String,
    animationTextAlpha: TweenSpec<Float>?,
    isMenuOpened: Boolean,
    isButtonExpanded: Boolean,
) {
    Text(
        text = text,
        softWrap = false,
        maxLines = 1,
        modifier = Modifier
            .padding(end = setup.fabSize - setup.fabIconSize)
            .then(
                if (animationTextAlpha == null)
                    Modifier
                else
                    Modifier.alpha(
                        animateFloatAsState(
                            targetValue = if (isMenuOpened || isButtonExpanded) 1f else 0f,
                            animationSpec = animationTextAlpha
                        ).value
                    )
            )

    )
}