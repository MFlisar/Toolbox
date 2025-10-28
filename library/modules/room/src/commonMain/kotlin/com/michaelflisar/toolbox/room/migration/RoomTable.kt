package com.michaelflisar.toolbox.room.migration

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import androidx.sqlite.execSQL
import kotlin.reflect.KClass

class RoomTable<ID : Number>(
    val idClass: KClass<ID>,
    val tableName: String,
    val columnNameId: String,
) {
    companion object {

        fun createInt(
            tableName: String,
            columnNameId: String,
        ) = RoomTable(
            idClass = Int::class,
            tableName = tableName,
            columnNameId = columnNameId,
        )

        fun createLong(
            tableName: String,
            columnNameId: String,
        ) = RoomTable(
            idClass = Long::class,
            tableName = tableName,
            columnNameId = columnNameId,
        )
    }

    @Suppress("UNCHECKED_CAST")
    fun <DataOld, DataNew> convertColumn(
        connection: SQLiteConnection,
        columnNameData: String,
        newColumnDefinition: String,
        readValue: (statement: SQLiteStatement, index: Int) -> DataOld?,
        convertData: (oldValue: DataOld?) -> DataNew?,
    ) {
        connection.convertColumn<ID, DataOld, DataNew>(
            tableName = tableName,
            columnNameId = columnNameId,
            columnNameData = columnNameData,
            newColumnDefinition = newColumnDefinition,
            readId = { statement, index ->
                when (idClass) {
                    Int::class -> statement.getInt(index)
                    Long::class -> statement.getLong(index)
                    else -> error("Unsupported index type: $idClass")
                } as ID
            },
            readValue = readValue,
            convertData = convertData
        )
    }

    /**
     * @param index e.g. index_w_day_date
     * @param columns e.g. listOf("date")
     */
    fun createIndex(connection: SQLiteConnection, index: String, vararg columns: String) {
        val indexColumns = columns.joinToString(", ")
        connection.execSQL("CREATE INDEX IF NOT EXISTS $index ON $tableName ($indexColumns)")
    }
}