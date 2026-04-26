package com.michaelflisar.toolbox.room.interfaces

import kotlinx.coroutines.flow.Flow

/**
 * optionales common interface
 */
interface IDaoFlow<ID : Number, Entity : IRoomEntity<ID, Entity>> {

    fun flowCount(): Flow<Int>

    fun flowAll(): Flow<List<Entity>>

    fun flow(id: Long): Flow<Entity>

}