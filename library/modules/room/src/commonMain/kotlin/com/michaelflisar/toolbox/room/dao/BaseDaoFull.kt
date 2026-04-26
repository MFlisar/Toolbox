package com.michaelflisar.toolbox.room.dao

import com.michaelflisar.toolbox.room.interfaces.IRoomEntity

abstract class BaseDaoFull<ID : Number, Entity : IRoomEntity<ID, Entity>, FullEntity>(
    data: BaseDaoData<ID, Entity>,
) : BaseDao<ID, Entity>(data) {

    // -----------------
    // GET
    // -----------------

    abstract suspend fun loadAllFull(): List<FullEntity>

    abstract suspend fun tryLoadFull(id: ID): FullEntity?

    suspend fun loadFull(id: ID): FullEntity {
        return tryLoadFull(id) ?: throw IllegalArgumentException("Entity with id $id not found")
    }

    // -----------------
    // DELETE
    // -----------------

    // reicht für Base Item

    // -----------------
    // INSERT / UPDATE
    // -----------------

    // reicht für Base Item
}