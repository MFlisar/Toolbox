package com.michaelflisar.toolbox.csv

abstract class CSVManager<T>(
    val tableName: String,
    val singleTableName: String,
    val headers: List<String>
) {
    abstract fun create(data: List<String>): T
    abstract fun toList(item: T) : List<String>
}