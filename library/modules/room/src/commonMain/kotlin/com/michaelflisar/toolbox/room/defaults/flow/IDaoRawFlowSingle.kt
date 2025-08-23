package com.michaelflisar.toolbox.room.defaults.flow

import androidx.room.RoomRawQuery
import kotlinx.coroutines.flow.Flow

interface IDaoRawFlowSingle<T> {
    fun rawFlowSingle(query: RoomRawQuery): Flow<T>
    fun rawFlowSingleNullable(query: RoomRawQuery): Flow<T?>
}