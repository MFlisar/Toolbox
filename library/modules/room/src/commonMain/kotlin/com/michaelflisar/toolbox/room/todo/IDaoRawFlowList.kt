package com.michaelflisar.toolbox.room.todo

import androidx.room.RoomRawQuery
import kotlinx.coroutines.flow.Flow

@Deprecated("")
interface IDaoRawFlowList<T> {
    fun rawFlowList(query: RoomRawQuery): Flow<List<T>>
}