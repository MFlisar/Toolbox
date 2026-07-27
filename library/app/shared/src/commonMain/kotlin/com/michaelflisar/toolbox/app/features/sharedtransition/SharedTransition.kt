package com.michaelflisar.toolbox.app.features.sharedtransition

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier

val LocalSharedTransitionData =
    compositionLocalOf<SharedTransitionData?> { null }

@OptIn(ExperimentalSharedTransitionApi::class)
@Stable
class SharedTransitionData(
    val animatedContentScope: AnimatedContentScope,
    val sharedTransitionScope: SharedTransitionScope,
) {

    @Composable
    fun modifier(key: String): Modifier {
        return with(sharedTransitionScope) {
            Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(key = key),
                animatedVisibilityScope = animatedContentScope
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun <T> SharedTransitionLayoutWithLocal(
    targetState: T,
    modifier: Modifier = Modifier,
    transitionSpec: AnimatedContentTransitionScope<T>.() -> ContentTransform = {
        (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)))
            .togetherWith(fadeOut(animationSpec = tween(90)))
    },
    content: @Composable (T) -> Unit,
) {
    SharedTransitionLayout(modifier = modifier) {
        AnimatedContent(
            targetState = targetState,
            transitionSpec = transitionSpec
        ) {
            CompositionLocalProvider(
                LocalSharedTransitionData provides SharedTransitionData(
                    this@AnimatedContent,
                    this@SharedTransitionLayout
                )
            ) {
                content(it)
            }
        }
    }
}