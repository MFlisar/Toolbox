package com.michaelflisar.toolbox.room.dao

import com.michaelflisar.toolbox.room.interfaces.IRoomEntity

abstract class BaseDaoCrossRefLong<T : Any>(
    tableName: String,
    columnLeftId: String,
    columnRightId: String,
) : BaseDaoCrossRef<Long, T>(tableName, columnLeftId, columnRightId)

abstract class BaseDaoCrossRefInt<T : Any>(
    tableName: String,
    columnLeftId: String,
    columnRightId: String,
) : BaseDaoCrossRef<Int, T>(tableName, columnLeftId, columnRightId)

abstract class BaseDaoLong<Entity : IRoomEntity<Long, Entity>>(
    tableName: String,
    columnId: String,
    sortAll: String? = null,
) : BaseDao<Long, Entity>(
    BaseDaoData(
        classIDInstance = 0L,
        tableName = tableName,
        columnId = columnId,
        sortAll = sortAll
    )
)

abstract class BaseDaoInt<Entity : IRoomEntity<Int, Entity>>(
    tableName: String,
    columnId: String,
    sortAll: String? = null,
) : BaseDao<Int, Entity>(
    BaseDaoData(
        classIDInstance = 0,
        tableName = tableName,
        columnId = columnId,
        sortAll = sortAll
    )
)

abstract class BaseDaoFullLong<Entity : IRoomEntity<Long, Entity>, FullEntity>(
    tableName: String,
    columnId: String,
    sortAll: String? = null,
) : BaseDaoFull<Long, Entity, FullEntity>(
    BaseDaoData(
        classIDInstance = 0L,
        tableName = tableName,
        columnId = columnId,
        sortAll = sortAll
    )
)

abstract class BaseDaoFullInt<Entity : IRoomEntity<Int, Entity>, FullEntity>(
    tableName: String,
    columnId: String,
    sortAll: String? = null,
) : BaseDaoFull<Int, Entity, FullEntity>(
    BaseDaoData(
        classIDInstance = 0,
        tableName = tableName,
        columnId = columnId,
        sortAll = sortAll
    )
)

interface IBaseDaoFlowLong<Entity : IRoomEntity<Long, Entity>> : IBaseDaoFlow<Long, Entity>

interface IBaseDaoFlowInt<Entity : IRoomEntity<Int, Entity>> : IBaseDaoFlow<Int, Entity>