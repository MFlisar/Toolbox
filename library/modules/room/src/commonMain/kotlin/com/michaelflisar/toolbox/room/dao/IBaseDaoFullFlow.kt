package com.michaelflisar.toolbox.room.dao

import kotlinx.coroutines.flow.Flow

/**
 * optionales common interface
 */
interface IBaseDaoFullFlow<FullEntity> {

    fun flowAllFull(): Flow<List<FullEntity>>

    fun flowFull(id: Long): Flow<FullEntity>

}