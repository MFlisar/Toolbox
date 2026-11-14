package com.michaelflisar.toolbox.app.features.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEvent
import com.michaelflisar.composestyled.adaptive.StyledAdaptiveMasterDetailMode
import com.michaelflisar.composestyled.adaptive.StyledAdaptiveInfo
import com.michaelflisar.composestyled.ui.state.StyledExpandableTopBarScrollBehavior

object NavScreenDefaults {

    fun <T : Any> crossfadeTransitionSpec(
        durationMillis: Int = 300,
    ): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
        fadeIn(animationSpec = tween(durationMillis)) togetherWith
                fadeOut(animationSpec = tween(durationMillis))
    }

    fun <T : Any> slideTransitionSpec(
        durationMillis: Int = 300,
    ): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
        slideInHorizontally(
            animationSpec = tween(durationMillis),
            initialOffsetX = { it },
        ) togetherWith slideOutHorizontally(
            animationSpec = tween(durationMillis),
            targetOffsetX = { -it },
        )
    }

    fun <T : Any> slidePopTransitionSpec(
        durationMillis: Int = 300,
    ): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform = {
        slideInHorizontally(
            animationSpec = tween(durationMillis),
            initialOffsetX = { -it },
        ) togetherWith slideOutHorizontally(
            animationSpec = tween(durationMillis),
            targetOffsetX = { it },
        )
    }

    fun <T : Any> masterDetailTransitionSpec(
        masterDetailMode: StyledAdaptiveMasterDetailMode,
        durationMillis: Int = 300,
    ): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform =
        if (masterDetailMode == StyledAdaptiveMasterDetailMode.SideBySide) {
            crossfadeTransitionSpec(durationMillis)
        } else {
            slideTransitionSpec(durationMillis)
        }

    fun <T : Any> masterDetailPopTransitionSpec(
        masterDetailMode: StyledAdaptiveMasterDetailMode,
        durationMillis: Int = 300,
    ): AnimatedContentTransitionScope<Scene<T>>.() -> ContentTransform =
        if (masterDetailMode == StyledAdaptiveMasterDetailMode.SideBySide) {
            crossfadeTransitionSpec(durationMillis)
        } else {
            slidePopTransitionSpec(durationMillis)
        }

    fun <T : Any> crossfadePredictivePopTransitionSpec(
        durationMillis: Int = 300,
    ): AnimatedContentTransitionScope<Scene<T>>.(Int) -> ContentTransform = {
        fadeIn(animationSpec = tween(durationMillis)) togetherWith
                fadeOut(animationSpec = tween(durationMillis))
    }

    fun <T : Any> masterDetailPredictivePopTransitionSpec(
        masterDetailMode: StyledAdaptiveMasterDetailMode,
        durationMillis: Int = 300,
    ): AnimatedContentTransitionScope<Scene<T>>.(Int) -> ContentTransform =
        if (masterDetailMode == StyledAdaptiveMasterDetailMode.SideBySide) {
            crossfadePredictivePopTransitionSpec(durationMillis)
        } else {
            slidePredictivePopTransitionSpecForNavDisplay(durationMillis)
        }

    fun slidePredictivePopTransitionSpec(
        durationMillis: Int = 300,
    ): AnimatedContentTransitionScope<Scene<*>>.(Int) -> ContentTransform = { swipeEdge ->
        slidePredictivePopTransform(swipeEdge, durationMillis)
    }

    fun <T : Any> slidePredictivePopTransitionSpecForNavDisplay(
        durationMillis: Int = 300,
    ): AnimatedContentTransitionScope<Scene<T>>.(Int) -> ContentTransform = { swipeEdge ->
        slidePredictivePopTransform(swipeEdge, durationMillis)
    }

    val slideTransitionMetadata: Map<String, Any> =
        NavDisplay.transitionSpec {
            slideInHorizontally(
                animationSpec = tween(300),
                initialOffsetX = { it },
            ) togetherWith slideOutHorizontally(
                animationSpec = tween(300),
                targetOffsetX = { -it },
            )
        } + NavDisplay.popTransitionSpec {
            slideInHorizontally(
                animationSpec = tween(300),
                initialOffsetX = { -it },
            ) togetherWith slideOutHorizontally(
                animationSpec = tween(300),
                targetOffsetX = { it },
            )
        } + NavDisplay.predictivePopTransitionSpec(slidePredictivePopTransitionSpec())

    private fun slidePredictivePopTransform(swipeEdge: Int, durationMillis: Int): ContentTransform {
        val isRightEdge = swipeEdge == NavigationEvent.EDGE_RIGHT
        return slideInHorizontally(
            animationSpec = tween(durationMillis),
            initialOffsetX = { width -> if (isRightEdge) width else -width },
        ) togetherWith slideOutHorizontally(
            animationSpec = tween(durationMillis),
            targetOffsetX = { width -> if (isRightEdge) -width else width },
        )
    }

}

/**
 * A Nav3 destination key that also provides the destination's UI.
 *
 * Each concrete implementation must be serializable to support saving and restoring the back
 * stack. Keep only route data in its constructor; composable content and scaffold slots are
 * provided by methods and are not part of the serialized key.
 */
interface NavScreen : NavKey {

    /** Nav3 metadata applied to this destination's entry. */
    val navigationMetadata: Map<String, Any>
        get() = emptyMap()

    @Composable
    fun Content(
        paddingValues: PaddingValues,
        adaptiveInfo: State<StyledAdaptiveInfo>,
    )

    /**
     * Every screen explicitly declares its toolbar configuration. Return null when the screen has
     * no toolbar; the scaffold keeps the shared toolbar and decides how to present changes.
     */
    @Composable
    fun Toolbar(): ToolbarConfig?

    /**
     * Optional contextual content for an active selection. The scaffold chooses its placement.
     */
    @Composable
    fun SelectionContent(): (@Composable () -> Unit)? = null
}

internal val LocalNav3ActiveScreen = compositionLocalOf<NavScreen?> { null }

/**
 * Values for the shared scaffold toolbar. The page can opt into expandable scrolling by providing
 * a behavior; null keeps the toolbar non-expandable. Navigation and platform presentation remain
 * scaffold-owned.
 */
data class ToolbarConfig(
    val title: String,
    val subtitle: String? = null,
    val actions: @Composable RowScope.() -> Unit = {},
    val scrollBehavior: StyledExpandableTopBarScrollBehavior? = null,
)
