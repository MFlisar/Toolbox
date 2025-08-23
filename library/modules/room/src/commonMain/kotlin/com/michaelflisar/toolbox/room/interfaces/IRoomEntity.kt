package com.michaelflisar.toolbox.room.interfaces

interface IRoomEntity<ID: Number, Entity : IRoomEntity<ID, Entity>> : IIDProvider<ID> {
    override val id: ID
    fun copyWithId(id: ID): Entity
}