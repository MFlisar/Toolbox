package com.michaelflisar.toolbox.room.todo

import com.michaelflisar.toolbox.room.interfaces.IIDProvider
import com.michaelflisar.toolbox.room.interfaces.IRoomEntity

@Deprecated("")
interface IDefaultDao<ID: Number, Entity : IRoomEntity<ID, Entity>> :
    IBaseDao<ID, Entity>,
    IBaseLoadDao<ID, Entity>,
    IBaseDaoDelete<ID, Entity>,
    IBaseDaoInsert<ID, Entity>,
    IBaseDaoUpdate<ID, Entity>,
    IBaseDaoClear,
    IBaseFlowDao<ID, Entity>

@Deprecated("")
interface IDefaultFullDao<ID: Number, Entity : IRoomEntity<ID, Entity>, FullEntity : IIDProvider<ID>> :
    IDefaultDao<ID, Entity>,
    IBaseFullLoadDao<ID, FullEntity>,
    IBaseDaoFullEntity<ID, FullEntity>,
    IBaseFullFlowDao<ID, FullEntity>