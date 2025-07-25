package com.michaelflisar.toolbox.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.michaelflisar.lumberjack.core.L
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale
import kotlin.collections.indices
import kotlin.text.toByteArray
import kotlin.text.uppercase

object AndroidUtil {

    // ID meines Handys
    const val TEST_DEVICE_ID = "6DCFD83105F36CF80B2D265D68258AAF"

    fun isDeveloper(context: Context, isDebugBuild: Boolean): Boolean {
        val developerId = TEST_DEVICE_ID
        val deviceId = getDeviceId(context)
        val id = md5(deviceId).uppercase(Locale.getDefault())
        val isDeveloper = developerId == id
        if (isDebugBuild && !isDeveloper) {
            L.v { "DEVICE ID: $id (!= $developerId)" }
        }
        return isDeveloper
    }

    fun getAppVersionName(context: Context): String {
        return try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            info.versionName ?: ""
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
        }
    }

    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        var id: String? = null
        try {
            id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        } catch (_: SecurityException) {
        }
        if (id == null) {
            id = "NULL"
        }
        return id
    }

    private fun md5(s: String): String {
        try {
            // Create MD5 Hash
            val digest = MessageDigest.getInstance("MD5")
            digest.update(s.toByteArray())
            val messageDigest = digest.digest()

            // Create Hex String
            val hexString = StringBuffer()
            for (i in messageDigest.indices) {
                var h = Integer.toHexString(
                    0xFF and messageDigest[i]
                        .toInt()
                )
                while (h.length < 2) {
                    h = "0$h"
                }
                hexString.append(h)
            }
            return hexString.toString()
        } catch (e: NoSuchAlgorithmException) {
        }
        return ""
    }

    @Suppress("DEPRECATION")
    fun getAppVersionCode(context: Context): Long {
        return try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode
            } else {
                info.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            L.e(e)
            -1L
        }
    }

    fun getCDATAHtml(context: Context, res: Int, vararg formatArgs: Any?): Spanned {
        return HtmlCompat.fromHtml(
            context.getString(res, *formatArgs),
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
    }
}