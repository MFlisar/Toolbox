package com.michaelflisar.toolbox.managers

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.michaelflisar.feedbackmanager.Feedback
import com.michaelflisar.feedbackmanager.FeedbackFile
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.getLatestLogFile
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.toolbox.Toolbox
import com.michaelflisar.toolbox.utils.AndroidUtil
import com.michaelflisar.toolbox.utils.ExceptionUtil
import com.michaelflisar.toolbox.utils.FileUtil
import kotlinx.coroutines.runBlocking
import org.acra.ACRA
import org.acra.ReportField
import org.acra.attachment.AcraContentProvider
import org.acra.attachment.AttachmentUriProvider
import org.acra.config.ACRAConfigurationException
import org.acra.config.CoreConfiguration
import org.acra.config.dialog
import org.acra.config.mailSender
import org.acra.data.StringFormat
import org.acra.ktx.initAcra
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import java.io.File
import java.io.IOException

object AcraManager {

    class Setup(
        val enableDebugNotification: Boolean,
        val appendLogFile: Boolean,
        val appIcon: Int,
        val appName: String,
        val notificationChannelId: String,
        val notificationId: Int = 9999,
        val mail: String,
        val reportError: (() -> Boolean) = { true },
        val onErrorReportedCallback: (() -> Unit)? = null,
        val reportFileName: String = "acra.txt",
    )

    private var fileLoggerSetup: IFileLoggingSetup? = null

    internal fun init(
        app: Application,
        fileLoggingSetup: IFileLoggingSetup?,
        setup: Setup,
        crash_dialog_text: StringResource,
        crash_dialog_title: StringResource,
        buildConfigClass: Class<*>,
        isDebugBuild: Boolean
    ): Boolean {
        this.fileLoggerSetup = fileLoggingSetup
        if (setup.enableDebugNotification) {
            ACRA.DEV_LOGGING = true
        }
        try {
            app.initAcra {
                this.buildConfigClass = buildConfigClass
                reportFormat = StringFormat.KEY_VALUE_LIST
                reportContent = listOf(
                    ReportField.REPORT_ID,
                    ReportField.APP_VERSION_NAME,
                    ReportField.APP_VERSION_CODE,
                    ReportField.ANDROID_VERSION,
                    ReportField.PHONE_MODEL,
                    ReportField.PACKAGE_NAME,
                    ReportField.CRASH_CONFIGURATION,
                    ReportField.CUSTOM_DATA,
                    ReportField.STACK_TRACE,
                    ReportField.APPLICATION_LOG,
                    ReportField.BUILD
                )
                if (setup.appendLogFile && fileLoggingSetup != null) {
                    //fileLoggingSetup.getLatestLogFile()?.let {
                    //    val fileUri: Uri = AcraContentProvider.getUriForFile(app, it)
                    //    withAttachmentUris(fileUri.toString())
                    //}
                    withAttachmentUriProvider(MyAttachmentProvider::class.java)
                }
                dialog {
                    enabled = true
                    resIcon = setup.appIcon
                    text = runBlocking { getString(crash_dialog_text) }
                    title = runBlocking { getString(crash_dialog_title) }
                    positiveButtonText = app.getString(android.R.string.ok)
                    negativeButtonText = app.getString(android.R.string.cancel)
                }
                mailSender {
                    enabled = true
                    mailTo = setup.mail
                    reportFileName = setup.reportFileName
                    reportAsFile = true
                    subject = getDefaultSubject(app, setup.appName)
                }
            }

            if (!ACRA.isACRASenderServiceProcess()) {
                ACRA.errorReporter.putCustomData("AppName", setup.appName)
                ACRA.errorReporter.putCustomData(
                    "Developer",
                    AndroidUtil.isDeveloper(app, isDebugBuild).toString()
                )
            }
            if (setup.enableDebugNotification) {
                ACRA.DEV_LOGGING = false
            }
            return true
        } catch (e: ACRAConfigurationException) {
            L.e(e)
            ACRA.init(app)
            if (setup.enableDebugNotification && setup.reportError()) {
                showErrorNotification(app, e, setup)
                setup.onErrorReportedCallback?.invoke()
                ACRA.DEV_LOGGING = false
            }
        } catch (e: NullPointerException) {
            L.e(e)
            ACRA.init(app)
            if (setup.enableDebugNotification && setup.reportError()) {
                showErrorNotification(app, e, setup)
                setup.onErrorReportedCallback?.invoke()
                ACRA.DEV_LOGGING = false
            }
        }
        return false
    }

    private fun getDefaultSubject(app: Application, appName: String): String {
        return appName + " - Crash Report v" + AndroidUtil.getAppVersionName(app)
    }

    private fun showErrorNotification(
        app: Application,
        e: Exception,
        setup: Setup,
    ) {
        try {
            val content: String? = ExceptionUtil.getStackTrace(e)
            val acraFile = File.createTempFile("acra", ".txt", app.cacheDir)
            FileUtil.createFile(acraFile, content)
            Feedback(
                listOf(setup.mail),
                String.format(
                    "ACRA Exception Feedback for %1\$s (v%2\$s)",
                    setup.appName,
                    AndroidUtil.getAppVersionName(app)
                ),
                attachments = listOfNotNull(
                    fileLoggerSetup?.getLatestLogFile(),
                    acraFile
                ).map { FeedbackFile(it) }
            ).startNotification(
                app,
                "Exception Report",
                "Rare error found",
                "Please report this error by clicking this notification, thanks",
                setup.appIcon,
                setup.notificationChannelId,
                setup.notificationId
            )
        } catch (e: IOException) {
            L.e(e)
        }
    }

    fun isACRAProcess() = ACRA.isACRASenderServiceProcess()

    class MyAttachmentProvider : AttachmentUriProvider {
        override fun getAttachments(context: Context, configuration: CoreConfiguration): List<Uri> {
            val uris = configuration.attachmentUris.mapNotNull {
                try {
                    it.toUri()
                } catch (e: Exception) {
                    org.acra.log.error(e) { "Failed to parse Uri $it" }
                    null
                }
            }
            val logFileUri = fileLoggerSetup?.getLatestLogFile()?.let {
                listOf(AcraContentProvider.getUriForFile(context, it))
            } ?: emptyList()

            return uris + logFileUri
        }
    }
}