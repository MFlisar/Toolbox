package com.michaelflisar.toolbox.room.dao2

import androidx.room.RoomRawQuery
import kotlinx.coroutines.flow.Flow

@Deprecated("")
interface IDaoRawFlowList<T> {
    fun rawFlowList(query: RoomRawQuery): Flow<List<T>>
}