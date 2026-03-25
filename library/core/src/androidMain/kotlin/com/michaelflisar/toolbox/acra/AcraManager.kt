package com.michaelflisar.toolbox.acra

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.core.getLatestLogFile
import com.michaelflisar.lumberjack.core.interfaces.IFileLoggingSetup
import com.michaelflisar.toolbox.utils.AndroidUtil
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
import org.acra.log.error

object AcraManager {

    internal class Setup(
        val appendLogFile: Boolean,
        val mail: String,
        val reportFileName: String = "acra.txt",
    )

    private var fileLoggerSetup: IFileLoggingSetup? = null

    internal fun init(
        app: Application,
        fileLoggingSetup: IFileLoggingSetup?,
        setup: Setup,
        acraSetup: AcraSetup,
        buildConfigClass: Class<*>,
        isDebugBuild: Boolean,
    ): Boolean {
        this.fileLoggerSetup = fileLoggingSetup
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
                    resIcon = acraSetup.appIcon
                    text = acraSetup.crashDialogText
                    title = acraSetup.crashDialogTitle
                    positiveButtonText = app.getString(android.R.string.ok)
                    negativeButtonText = app.getString(android.R.string.cancel)
                }
                mailSender {
                    enabled = true
                    mailTo = setup.mail
                    reportFileName = setup.reportFileName
                    reportAsFile = true
                    subject = getDefaultSubject(app, acraSetup.appName)
                }
            }

            if (!ACRA.isACRASenderServiceProcess()) {
                ACRA.errorReporter.putCustomData("AppName", acraSetup.appName)
                ACRA.errorReporter.putCustomData(
                    "Developer",
                    AndroidUtil.isDeveloper(app, isDebugBuild).toString()
                )
            }
            return true
        } catch (e: ACRAConfigurationException) {
            L.e(e)
            ACRA.init(app)
        } catch (e: NullPointerException) {
            L.e(e)
            ACRA.init(app)
        }
        return false
    }

    private fun getDefaultSubject(app: Application, appName: String): String {
        return appName + " - Crash Report v" + AndroidUtil.getAppVersionName(app)
    }

    fun isACRAProcess() = ACRA.isACRASenderServiceProcess()

    class MyAttachmentProvider : AttachmentUriProvider {
        override fun getAttachments(context: Context, configuration: CoreConfiguration): List<Uri> {
            val uris = configuration.attachmentUris.mapNotNull {
                try {
                    it.toUri()
                } catch (e: Exception) {
                    error(e) { "Failed to parse Uri $it" }
                    null
                }
            }
            val logFileUri = fileLoggerSetup?.getLatestLogFile()?.let {
                listOf(AcraContentProvider.Companion.getUriForFile(context, it))
            } ?: emptyList()

            return uris + logFileUri
        }
    }
}