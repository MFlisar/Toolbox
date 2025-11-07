package com.michaelflisar.toolbox.utils

import android.app.Application
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.toolbox.AppContext
import com.michaelflisar.toolbox.NotificationUtil
import com.michaelflisar.toolbox.acra.AcraManager
import com.michaelflisar.toolbox.acra.AcraSetup
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
        acraSetup: AcraSetup,
        appName: StringResource,
        fileLoggerSetup: IFileLoggingSetup?,
        reportError: (() -> Boolean) = { true },
        onErrorReportedCallback: (() -> Unit)? = null,
        buildConfigClass: Class<*>,
        isDebugBuild: Boolean,
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
                enableDebugNotification = isDebugBuild,
                appendLogFile = true,
                appName = appName,
                notificationChannelId = NOTIFICATION_CHANNEL_ERROR_ID,
                notificationId = DEFAULT_ERROR_NOTIFICATION_ID,
                mail = MAIL,
                reportError = reportError,
                onErrorReportedCallback = onErrorReportedCallback
            ),
            acraSetup = acraSetup,
            buildConfigClass = buildConfigClass,
            isDebugBuild = isDebugBuild
        )
    }

}