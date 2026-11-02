package com.michaelflisar.toolbox.ui.adaptive.runtime

@Suppress("ExperimentalAnnotationRetention")
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message =
        "This is internal API meant to be used by theme implementations only and may change without further notice.",
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
)
@Retention(AnnotationRetention.BINARY)
annotation class InternalComposeStyledApi