package com.michaelflisar.toolbox.excel

import com.michaelflisar.lumberjack.core.L
import java.io.File

class ExcelSheet(
    val file: File,
    val sheet: String,
    val columns: List<String>,
    val firstDataRow: Int
) {
    suspend fun read(log: Boolean = true): List<List<String>>? {
        val result = try {
            val data = readRows()
            if (log) {
                L.d { "  - Data (${sheet}) - Werte: ${data.size}" }
                L.d { "  - Data (${sheet}) - Wert 1 = ${data.firstOrNull()}" }
                L.d { "  - Data (${sheet}) - Wert N = ${data.lastOrNull()}" }
            }
            data
        } catch (e: Exception) {
            L.e(e)
            null
        }
        return result
    }

    private suspend fun readRows(): List<List<String>> {
        return Excel.readFile(file, sheet, firstDataRow, columns)
    }
}