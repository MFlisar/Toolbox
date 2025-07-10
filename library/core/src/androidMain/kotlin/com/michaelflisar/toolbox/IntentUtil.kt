package com.michaelflisar.toolbox

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.net.toUri

object IntentUtil {

    fun getMarketUrl(context: Context, packageName: String = context.packageName): String {
        return "https://play.google.com/store/apps/details?id=$packageName"
    }

    @JvmOverloads
    fun openMarket(context: Context, packageName: String = context.packageName) {
        try {
            val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")).apply {
                    flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
                }
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
            ).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            }
            context.startActivitySafely(intent)
        }
    }

    fun supportsLanguagePicker(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    fun openLanguagePicker(context: Context) {
        val uri = Uri.fromParts("package", context.packageName, null)
        val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            data = uri
        }
        context.startActivitySafely(intent)

    }

    fun share(context: Context, subject: String?, text: String?, apply: ((intent: Intent) -> Unit)? = null) {
        val intent = Intent().apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
            action = Intent.ACTION_SEND
            subject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
            text?.let { putExtra(Intent.EXTRA_TEXT, it) }
            type = "text/plain"
            apply?.invoke(this)
        }
        context.startActivitySafely(intent)
    }

    fun getUrlIntent(url: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK // | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            data = url.toUri()
        }
        return intent
    }

    fun openUrl(context: Context, url: String): Boolean {
        val intent = getUrlIntent(url)
        return context.startActivitySafely(intent)
    }

    fun openUri(context: Context, uri: Uri, action: String = Intent.ACTION_VIEW): Boolean {
        val intent = Intent(action).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK // | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            data = uri
        }
        return context.startActivitySafely(intent)
    }
}