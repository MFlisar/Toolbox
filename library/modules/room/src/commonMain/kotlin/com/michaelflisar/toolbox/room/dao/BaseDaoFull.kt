package com.michaelflisar.toolbox.room.dao

import com.michaelflisar.toolbox.room.interfaces.IDaoFull
import com.michaelflisar.toolbox.room.interfaces.IRoomEntity

abstract class BaseDaoFull<ID : Number, Entity : IRoomEntity<ID, Entity>, FullEntity>(
    id: DaoID<ID>,
    tableName: String,
    columnId: String,
    sortAll: String? = null,
) : BaseDao<ID, Entity>(id, tableName, columnId, sortAll),
    IDaoFull<ID, Entity, FullEntity>