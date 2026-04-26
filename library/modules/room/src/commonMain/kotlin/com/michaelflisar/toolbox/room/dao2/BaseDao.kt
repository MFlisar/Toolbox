package com.michaelflisar.toolbox.room.dao2

import com.michaelflisar.toolbox.room.interfaces.IRoomEntity

@Deprecated("Use BaseDaoLong from dao2 package instead")
abstract class BaseDao2Long<Entity : IRoomEntity<Long, Entity>>(
    override val tableName: String,
    override val columnId: String,
    override val sortAll: String? = null
) : IBaseDao2<Long, Entity> {
    override val classIDInstance = 0L
}

@Deprecated("Use BaseDaoInt from dao2 package instead")
abstract class BaseDao2Int<Entity : IRoomEntity<Int, Entity>>(
    override val tableName: String,
    override val columnId: String,
    override val sortAll: String? = null
) : IBaseDao2<Int, Entity> {
    override val classIDInstance = 0
}