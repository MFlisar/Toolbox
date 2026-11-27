package com.michaelflisar.toolbox.app.features.update

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.toolbox.ToolboxLogging
import com.michaelflisar.toolbox.debug
import com.michaelflisar.toolbox.error
import java.util.WeakHashMap

internal object AndroidInAppUpdate {

    private val initializedActivities = WeakHashMap<ComponentActivity, Unit>()

    fun initialize(activity: ComponentActivity, behavior: InAppUpdate) {
        if (behavior == InAppUpdate.Disabled) return
        if (initializedActivities.put(activity, Unit) != null) return

        val updateType = when (behavior) {
            InAppUpdate.Disabled -> return
            InAppUpdate.Dialog -> AppUpdateType.FLEXIBLE
            InAppUpdate.Force -> AppUpdateType.IMMEDIATE
        }
        val manager = AppUpdateManagerFactory.create(activity)
        lateinit var launcher: ActivityResultLauncher<IntentSenderRequest>
        launcher = activity.registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                if (behavior == InAppUpdate.Force) {
                    L.error(ToolboxLogging.Tag.AppUpdate) {
                        "Required in-app update flow ended with result ${result.resultCode}; retrying"
                    }
                    checkForUpdate(manager, launcher, behavior, updateType)
                } else {
                    L.debug(ToolboxLogging.Tag.AppUpdate) {
                        "Optional in-app update flow ended with result ${result.resultCode}"
                    }
                }
            }
        }

        val installStateListener = if (behavior == InAppUpdate.Dialog) {
            InstallStateUpdatedListener { state ->
                if (state.installStatus() == InstallStatus.DOWNLOADED) {
                    manager.completeUpdate().addOnFailureListener { error ->
                        L.error(ToolboxLogging.Tag.AppUpdate, t = error) {
                            "Failed to complete the downloaded in-app update"
                        }
                    }
                }
            }.also(manager::registerListener)
        } else {
            null
        }

        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                installStateListener?.let(manager::unregisterListener)
                activity.lifecycle.removeObserver(this)
            }
        })

        checkForUpdate(manager, launcher, behavior, updateType)
    }

    private fun checkForUpdate(
        manager: AppUpdateManager,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
        behavior: InAppUpdate,
        updateType: Int,
    ) {
        manager.appUpdateInfo
            .addOnSuccessListener { info ->
                val availability = info.updateAvailability()
                val isNewUpdateAvailable = availability == UpdateAvailability.UPDATE_AVAILABLE
                val isRequiredUpdateInProgress =
                    behavior == InAppUpdate.Force &&
                        availability == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS

                if (!isNewUpdateAvailable && !isRequiredUpdateInProgress) return@addOnSuccessListener
                if (isNewUpdateAvailable && !info.isUpdateTypeAllowed(updateType)) {
                    val message = "In-app update type $updateType is not allowed for this update"
                    if (behavior == InAppUpdate.Force) {
                        L.error(ToolboxLogging.Tag.AppUpdate) { message }
                    } else {
                        L.debug(ToolboxLogging.Tag.AppUpdate) { message }
                    }
                    return@addOnSuccessListener
                }

                val flowStarted = manager.startUpdateFlowForResult(
                    info,
                    launcher,
                    AppUpdateOptions.newBuilder(updateType).build(),
                )
                if (!flowStarted) {
                    L.error(ToolboxLogging.Tag.AppUpdate) {
                        "Google Play did not start the in-app update flow"
                    }
                }
            }
            .addOnFailureListener { error ->
                L.error(ToolboxLogging.Tag.AppUpdate, t = error) {
                    "Failed to check for in-app updates"
                }
            }
    }
}
