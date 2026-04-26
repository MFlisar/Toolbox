package com.michaelflisar.toolbox.room.dao

sealed interface DaoID<ID : Number> {
    fun fromRowId(id: kotlin.Long): ID

    data object Int : DaoID<kotlin.Int> {
        override fun fromRowId(id: kotlin.Long): kotlin.Int = id.toInt()
    }

    data object Long : DaoID<kotlin.Long> {
        override fun fromRowId(id: kotlin.Long): kotlin.Long = id
    }
}