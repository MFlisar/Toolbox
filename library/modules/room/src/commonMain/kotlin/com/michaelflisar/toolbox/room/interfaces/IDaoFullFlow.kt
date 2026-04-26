package com.michaelflisar.toolbox.room.interfaces

import kotlinx.coroutines.flow.Flow

/**
 * optionales common interface
 */
interface IDaoFullFlow<FullEntity> {

    fun flowAllFull(): Flow<List<FullEntity>>

    fun flowFull(id: Long): Flow<FullEntity>

}