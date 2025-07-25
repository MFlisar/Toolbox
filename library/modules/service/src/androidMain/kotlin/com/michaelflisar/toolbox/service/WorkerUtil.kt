package com.michaelflisar.toolbox.service

import android.content.Context
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.UUID

object WorkerUtil {

    fun exists(context: Context, uniqueWorkName: String): Boolean {
        return WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork(uniqueWorkName)
            .get()
            .count { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING } > 0
    }

    fun exists(context: Context, id: UUID): Boolean {
        return WorkManager.getInstance(context)
            .getWorkInfoById(id)
            .get()?.takeIf { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING } != null
    }

    fun nextTime(context: Context, uniqueWorkName: String): Long? {
        return WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork(uniqueWorkName)
            .get()
            .firstOrNull { it.state == WorkInfo.State.ENQUEUED }
            ?.nextScheduleTimeMillis
    }

    fun cancel(context: Context, uniqueWorkName: String) {
        WorkManager
            .getInstance(context)
            .cancelUniqueWork(uniqueWorkName)
    }

    fun cancel(context: Context, id: UUID) {
        WorkManager
            .getInstance(context)
            .cancelWorkById(id)
    }

}