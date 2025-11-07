package com.michaelflisar.toolbox.room.dao

import com.michaelflisar.toolbox.room.interfaces.IRoomEntity

abstract class BaseDaoLong<Entity : IRoomEntity<Long, Entity>>(
    override val tableName: String,
    override val columnId: String,
    override val sortAll: String? = null
) : IBaseDao<Long, Entity> {
    override val classIDInstance = 0L
}
/*
abstract class BaseDaoFullLong<Entity : IRoomEntity<Long, Entity>, FullEntity>(
    tableName: String,
    columnId: String,
    sortAll: String? = null
) : BaseDaoLong<Entity>(tableName, columnId, sortAll), IBaseDaoFull<Long, Entity, FullEntity>
*/
abstract class BaseDaoInt<Entity : IRoomEntity<Int, Entity>>(
    override val tableName: String,
    override val columnId: String,
    override val sortAll: String? = null
) : IBaseDao<Int, Entity> {
    override val classIDInstance = 0
}
/*
abstract class BaseDaoFullInt<Entity : IRoomEntity<Int, Entity>, FullEntity>(
    tableName: String,
    columnId: String,
    sortAll: String? = null
) : BaseDaoInt<Entity>(tableName, columnId, sortAll), IBaseDaoFull<Int, Entity, FullEntity>*/