package com.michaelflisar.toolbox.room.defaults.flow

import androidx.room.RoomRawQuery
import kotlinx.coroutines.flow.Flow

interface IDaoRawFlowList<T> {
    fun rawFlowList(query: RoomRawQuery): Flow<List<T>>
}