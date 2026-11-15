package com.michaelflisar.toolbox.app.features.debug

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.value
import platform.darwin.KERN_SUCCESS
import platform.darwin.MACH_TASK_BASIC_INFO
import platform.darwin.mach_task_basic_info_data_t
import platform.darwin.mach_task_self_
import platform.darwin.mach_msg_type_number_tVar
import platform.darwin.natural_tVar
import platform.darwin.task_info

@OptIn(ExperimentalForeignApi::class)
internal actual fun processMemoryUsageBytes(): Long? = memScoped {
    val info = alloc<mach_task_basic_info_data_t>()
    val count = alloc<mach_msg_type_number_tVar>()
    count.value = (sizeOf<mach_task_basic_info_data_t>() / sizeOf<natural_tVar>()).toUInt()

    val result = task_info(
        mach_task_self_,
        MACH_TASK_BASIC_INFO.toUInt(),
        info.ptr.reinterpret(),
        count.ptr,
    )
    if (result == KERN_SUCCESS) info.resident_size.toLong() else null
}
