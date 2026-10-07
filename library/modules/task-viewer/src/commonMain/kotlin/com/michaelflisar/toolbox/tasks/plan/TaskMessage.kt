package com.michaelflisar.toolbox.tasks.plan

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.michaelflisar.toolbox.extensions.isDark
import com.michaelflisar.toolbox.tasks.ui.TaskViewerConfig

@Composable
fun rememberTaskMessageColors(
    colorSuccessOnDark: Color = Color(0xFF81C784), // Green 300
    colorSuccessOnLight: Color = Color(0xFF388E3C), // Green 700
    colorWarningOnDark: Color = Color(0xFFFFB74D), // Orange 300
    colorWarningOnLight: Color = Color(0xFFF57C00), // Orange 700
    colorErrorOnDark: Color = Color(0xFFE57373), // Red 300
    colorErrorOnLight: Color = Color(0xFFD32F2F), // Red 700
): TaskMessageColors {
    val isBackgroundDark = MaterialTheme.colorScheme.background.isDark()
    return remember(
        isBackgroundDark,
        colorSuccessOnDark,
        colorSuccessOnLight,
        colorWarningOnDark,
        colorWarningOnLight,
        colorErrorOnDark,
        colorErrorOnLight
    ) {
        TaskMessageColors(
            isBackgroundDark = isBackgroundDark,
            colorSuccessOnDark = colorSuccessOnDark,
            colorSuccessOnLight = colorSuccessOnLight,
            colorWarningOnDark = colorWarningOnDark,
            colorWarningOnLight = colorWarningOnLight,
            colorErrorOnDark = colorErrorOnDark,
            colorErrorOnLight = colorErrorOnLight,
        )
    }
}

@Stable
data class TaskMessageColors(
    val isBackgroundDark: Boolean,
    private val colorSuccessOnDark: Color,
    private val colorSuccessOnLight: Color,
    private val colorWarningOnDark: Color,
    private val colorWarningOnLight: Color,
    private val colorErrorOnDark: Color,
    private val colorErrorOnLight: Color,
) {
    fun colorSuccess() = if (isBackgroundDark) colorSuccessOnDark else colorSuccessOnLight
    fun colorWarning() = if (isBackgroundDark) colorWarningOnDark else colorWarningOnLight
    fun colorError() = if (isBackgroundDark) colorErrorOnDark else colorErrorOnLight
    fun colorSuccess(background: Color) = if (background.isDark()) colorSuccessOnDark else colorSuccessOnLight
    fun colorWarning(background: Color) = if (background.isDark()) colorWarningOnDark else colorWarningOnLight
    fun colorError(background: Color) = if (background.isDark()) colorErrorOnDark else colorErrorOnLight
}

sealed interface TaskMessage {

    data class Text(
        val text: String,
        val type: TaskMessageType = TaskMessageType.Info,
    ) : TaskMessage {

        @Composable
        fun annotated(
            config: TaskViewerConfig,
            background: Color
        ): AnnotatedString {
            return buildAnnotatedString {

                when (type) {
                    TaskMessageType.Warning -> {
                        withStyle(
                            SpanStyle(color = config.taskMessageColors.colorWarning(background))
                        ) {
                            append(config.prefixWarning)
                        }
                    }

                    TaskMessageType.Error -> {
                        withStyle(
                            SpanStyle(color = config.taskMessageColors.colorError(background))
                        ) {
                            append(config.prefixError)
                        }
                    }

                    TaskMessageType.Info -> Unit
                }

                append(text)
            }
        }
    }

    data class Rich(
        val text: AnnotatedString,
    ) : TaskMessage
}


enum class TaskMessageType {
    Info,
    Warning,
    Error
}