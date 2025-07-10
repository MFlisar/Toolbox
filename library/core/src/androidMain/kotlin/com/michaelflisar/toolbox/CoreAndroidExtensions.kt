package com.michaelflisar.toolbox

import android.app.Activity
import android.app.ActivityManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.util.TypedValue
import com.jakewharton.processphoenix.ProcessPhoenix
import com.michaelflisar.lumberjack.core.L
import kotlin.system.exitProcess

val Int.pxToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Int.pxToSp: Int
    get() = (this / TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        1.0f,
        Resources.getSystem().displayMetrics
    )).toInt()

//get() = (this / Resources.getSystem().displayMetrics.scaledDensity).toInt()
val Int.spToPx: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()
//get() = (this * Resources.getSystem().displayMetrics.scaledDensity).toInt()

@Suppress("DEPRECATION")
fun Context.isServiceRunning(serviceClass: Class<*>?, checkForeground: Boolean = false): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (service?.service != null && serviceClass != null && serviceClass.name == service.service.className) {
            return if (checkForeground) service.foreground else true
        }
    }
    return false
}

fun Context.isScreenLandscape(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
}

fun Context.getActivity(): Activity? {
    var context: Context? = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun Context.requireActivity(): Activity {
    var context: Context? = this
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    throw RuntimeException("Context is no activity!")
}

fun Context.startActivitySafely(intent: Intent): Boolean {
    return try {
        startActivity(intent)
        true
    } catch (e: ActivityNotFoundException) {
        L.e(e)
        false
    }
    /*
    val canBeHandled = intent.resolveActivity(packageManager) != null
    return if (canBeHandled) {
        startActivity(intent)
        true
    } else {
        false
    }*/
}

fun Context.restartApp(intent: Intent? = null) {
    if (intent == null) {
        ProcessPhoenix.triggerRebirth(this)
    } else {
        ProcessPhoenix.triggerRebirth(this, intent)
    }
}

fun Activity.killApp() {
    finishAffinity()
    // Process.killProcess(pid)
    // Runtime.getRuntime().exit(0)
    exitProcess(0)
}

fun CharSequence.to64BitHash(): Long {
    var result = -0x340d631b7bdddcdbL
    val len = this.length
    for (i in 0 until len) {
        result = result xor this[i].code.toLong()
        result *= 0x100000001b3L
    }
    return result
}

/*
 * same as https://github.com/google/accompanist/blob/a9506584939ed9c79890adaaeb58de01ed0bb823/permissions/src/main/java/com/google/accompanist/permissions/PermissionsUtil.kt#L132
 */
fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Activity not found in this context!")
}