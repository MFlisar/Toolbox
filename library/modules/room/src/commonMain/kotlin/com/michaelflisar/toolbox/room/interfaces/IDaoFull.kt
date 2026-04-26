package com.michaelflisar.toolbox.room.interfaces

/**
 * optionales common interface
 */
interface IDaoFull<ID : Number, Entity : IRoomEntity<ID, Entity>, FullEntity> {

    // -----------------
    // GET
    // -----------------

    suspend fun loadAllFull(): List<FullEntity>

    suspend fun tryLoadFull(id: ID): FullEntity?

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