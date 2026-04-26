package com.michaelflisar.toolbox.room.dao2

import androidx.room.RoomRawQuery
import kotlinx.coroutines.flow.Flow

@Deprecated("")
interface IDaoRawFlowSingle<T> {
    fun rawFlowSingle(query: RoomRawQuery): Flow<T>
    fun rawFlowSingleNullable(query: RoomRawQuery): Flow<T?>
}