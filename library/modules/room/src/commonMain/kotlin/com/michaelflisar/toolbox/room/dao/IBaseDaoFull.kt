package com.michaelflisar.toolbox.room.dao

import androidx.room.RawQuery
import androidx.room.RoomRawQuery
import com.michaelflisar.toolbox.room.RoomQueryUtil
import com.michaelflisar.toolbox.room.RoomUtil
import com.michaelflisar.toolbox.room.interfaces.IRoomEntity
import kotlinx.coroutines.flow.Flow

// FULL - problematisch, da muss man beim annotieren alle abhängigkeiten angeben und transaktionen nutzen...

/*
interface IBaseDaoFull<ID : Number, Entity : IRoomEntity<ID, Entity>, FullEntity> :
    IBaseDao<ID, Entity> {

    // -----------------
    // flows ("private")
    // -----------------

    fun _flowListFull(query: RoomRawQuery): Flow<List<FullEntity>>
    fun _flowItemFull(query: RoomRawQuery): Flow<FullEntity>
    //abstract fun _flowItemOrNull(query: RoomRawQuery): Flow<T?>

    // ---------------
    // Flows
    // ---------------

    // load
    fun flowAllFull() = _flowListFull(RoomQueryUtil.loadAll(tableName, sortAll))
    fun flowFull(id: ID) = _flowItemFull(RoomQueryUtil.load(tableName, columnId, id))
    fun flowAllFullIn(ids: List<ID>) =
        _flowListFull(RoomQueryUtil.loadAllIn(tableName, columnId, ids))

    // -----------------
    // GET
    // -----------------

    suspend fun loadAllFull() = rawListFull(RoomQueryUtil.loadAll(tableName, sortAll))

    suspend fun loadFull(id: ID): FullEntity =
        rawItemFull(RoomQueryUtil.load(tableName, columnId, id))

    suspend fun tryLoadFull(id: ID): FullEntity? =
        rawItemFullOrNull(RoomQueryUtil.load(tableName, columnId, id))

    suspend fun _loadAllFullIn(ids: List<ID>): List<FullEntity> =
        rawListFull(RoomQueryUtil.loadAllIn(tableName, columnId, ids))

    suspend fun loadAllFullIn(ids: List<ID>): List<FullEntity> =
        RoomUtil.runQueryInChunks(ids) { _loadAllFullIn(it) }

    // -----------------
    // DELETE
    // -----------------

    // reicht für Base Item

    // -----------------
    // INSERT / UPDATE
    // -----------------

    // reicht für Base Item

    // -----------------
    // RAW
    // -----------------

    @RawQuery
    suspend fun rawListFull(query: RoomRawQuery): List<FullEntity>

    @RawQuery
    suspend fun rawItemFull(query: RoomRawQuery): FullEntity

    @RawQuery
    suspend fun rawItemFullOrNull(query: RoomRawQuery): FullEntity?
}*/