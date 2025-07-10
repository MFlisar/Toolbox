package com.michaelflisar.toolbox.utils

import android.app.Application
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup
import com.michaelflisar.toolbox.AppContext
import com.michaelflisar.toolbox.BuildConfig
import com.michaelflisar.toolbox.NotificationUtil
import com.michaelflisar.toolbox.managers.AcraManager
import org.jetbrains.compose.resources.StringResource

object AcraUtil {

    internal const val MAIL = "mflisar.development@gmail.com"

    private const val NOTIFICATION_CHANNEL_ERROR_ID = "error_channel"
    private const val NOTIFICATION_CHANNEL_INFO_ID = "info_channel"
    private const val NOTIFICATION_CHANNEL_ERROR_NAME = "Error Notifications"
    private const val NOTIFICATION_CHANNEL_INFO_NAME = "Info Notifications"
    private const val DEFAULT_ERROR_NOTIFICATION_ID = 9999
    private const val DEFAULT_INFO_NOTIFICATION_ID = 9998

    fun initAcra(
        app: Application,
        appIcon: Int,
        appName: String,
        fileLoggerSetup: FileLoggerSetup?,
        crash_dialog_text: StringResource,
        crash_dialog_title: StringResource,
        reportError: (() -> Boolean) = { true },
        onErrorReportedCallback: (() -> Unit)? = null,
    ) {
        NotificationUtil.initChannel(
            AppContext.context(),
            NOTIFICATION_CHANNEL_ERROR_NAME,
            NOTIFICATION_CHANNEL_ERROR_ID
        )

        AcraManager.init(
            app = app,
            fileLoggingSetup = fileLoggerSetup,
            setup = AcraManager.Setup(
                enableDebugNotification = BuildConfig.DEBUG,
                appendLogFile = true,
                appIcon = appIcon,
                appName = appName,
                notificationChannelId = NOTIFICATION_CHANNEL_ERROR_ID,
                notificationId = DEFAULT_ERROR_NOTIFICATION_ID,
                mail = MAIL,
                reportError = reportError,
                onErrorReportedCallback = onErrorReportedCallback
            ),
            crash_dialog_text = crash_dialog_text,
            crash_dialog_title = crash_dialog_title
        )
    }

}