package com.michaelflisar.toolbox.room.interfaces

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

interface IBaseDao<ID: Number, Entity : IRoomEntity<ID, Entity>> {
    val entityClass: KClass<Entity>
}

interface IBaseDaoClear {

    @Delete
    suspend fun clear()
}

interface IBaseDaoDelete<ID: Number, Entity : IRoomEntity<ID, Entity>> {

    @Delete
    suspend fun delete(item: Entity): Int

    @Delete
    suspend fun delete(items: List<Entity>): Int
}

interface IBaseDaoInsert<ID: Number, Entity : IRoomEntity<ID, Entity>> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun internalInsert(items: Entity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun internalInsert(items: List<Entity>): List<Long>

    suspend fun insert(item: Entity): Entity {
        return internalInsert(item)
            .let {
                val id = when (item.id::class) {
                    Int::class -> it.toInt()
                    Long::class -> it
                    else -> throw RuntimeException("ID class not supported: ${item.id::class}")
                } as ID
                item.copyWithId(id)
            }
    }

    suspend fun insert(items: List<Entity>): List<Entity> {
        val firstId = items.firstOrNull()?.id
        return internalInsert(items)
            .zip(items)
            .map {
                val id = when (firstId!!::class) {
                    Int::class -> it.first.toInt()
                    Long::class -> it.first
                    else -> throw RuntimeException("ID class not supported: ${firstId::class}")
                } as ID
                it.second.copyWithId(id)
            }
    }
}

interface IBaseDaoUpdate<ID: Number, Entity : IRoomEntity<ID, Entity>> {

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(item: Entity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(items: List<Entity>)

}

interface IBaseLoadDao<ID: Number, Entity : IRoomEntity<ID, Entity>> {
    suspend fun loadAll(): List<Entity>
    suspend fun loadSingle(id: Long): Entity
    suspend fun find(id: Long): Entity?
}

interface IBaseFlowDao<ID: Number, Entity : IRoomEntity<ID, Entity>> {
    fun flow(): Flow<List<Entity>>
    fun flowSingle(id: Long): Flow<Entity>
}

// -------------------------
// Full Entity
// -------------------------

interface IBaseDaoFullEntity<ID: Number, Item: IIDProvider<ID>> {
    val fullEntityClass: KClass<Item>
}

interface IBaseFullLoadDao<ID: Number, Item: IIDProvider<ID>> {
    suspend fun loadAllFull(): List<Item>
    suspend fun loadSingleFull(id: Long): Item
    suspend fun findFull(id: Long): Item?
}

interface IBaseFullFlowDao<ID: Number, Item: IIDProvider<ID>> {
    fun flowFull(): Flow<List<Item>>
    fun flowFullSingle(id: Long): Flow<Item>
}