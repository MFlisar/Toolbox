package com.michaelflisar.toolbox.excel

import com.michaelflisar.lumberjack.core.L
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object Excel {

    class Sheet(
        val file: File,
        val sheet: String,
        val columns: List<String>,
        val firstDataRow: Int
    ) {
        suspend fun read(): List<List<String>>? {
            val result = try {
                val data = readRows()
                L.d { "  - Data (${sheet}) - Werte: ${data.size}" }
                L.d { "  - Data (${sheet}) - Wert 1 = ${data.firstOrNull()}" }
                L.d { "  - Data (${sheet}) - Wert N = ${data.lastOrNull()}" }
                data
            } catch (e: Exception) {
                L.e(e)
                null
            }
            return result
        }

        private suspend fun readRows(): List<List<String>> {
            return readFile(file, sheet, firstDataRow, columns)
        }
    }

    fun createColumnRangeList(startCol: String, endCol: String): List<String> {
        val startNum = CellReference.convertColStringToIndex(startCol)
        val endNum = CellReference.convertColStringToIndex(endCol)
        val columns = mutableListOf<String>()
        for (num in startNum..endNum) {
            columns.add(CellReference.convertNumToColString(num))
        }
        return columns
    }

    suspend fun readFile(
        file: File,
        sheetName: String,
        firstLine: Int,
        columns: List<String>
    ): List<List<String>> {
        return withContext(Dispatchers.IO) {

            val items = ArrayList<ArrayList<String>>()

            val fis = FileInputStream(file)
            val workbook: Workbook = XSSFWorkbook(fis)
            val sheet: org.apache.poi.ss.usermodel.Sheet = workbook.getSheet(sheetName)

            val firstRowIndex = firstLine - 1
            val rows = sheet.lastRowNum
            for (r in firstRowIndex..rows) {
                val line = ArrayList<String>()
                for (c in columns) {
                    val row = sheet.getRow(r)
                    val cell = row.getCell(CellReference.convertColStringToIndex(c))
                    val value = cell?.let { readCellValue(it) } ?: ""
                    line.add(value)
                }
                items.add(line)
            }

            workbook.close()
            fis.close()

            items
        }
    }

    suspend fun readLine(
        file: String,
        sheetName: String,
        line: Int,
        trim: Boolean = true,
        columns: List<String>
    ): List<String> {
        return withContext(Dispatchers.IO) {
            val items = ArrayList<String>()

            val fis = FileInputStream(file)
            val workbook = XSSFWorkbook(fis)
            val sheet = workbook.getSheet(sheetName)

            val row = sheet.getRow(line - 1)
            var r = 0
            for (c in columns) {
                val cell = row.getCell(CellReference.convertColStringToIndex(c))
                val value = cell?.let { readCellValue(it) } ?: ""
                val v = if (trim) value.trim() else value
                items.add(v)
            }

            workbook.close()
            fis.close()

            items
        }
    }

    private fun readCellValue(cell: Cell): String {
        var type = cell.cellType
        if (type == CellType.FORMULA)
            type = cell.cachedFormulaResultType
        val value = when (type) {
            CellType.STRING -> cell.stringCellValue
            CellType.NUMERIC -> cell.numericCellValue.toString()
            CellType.BLANK,
            CellType.ERROR,
            null -> ""

            else -> throw RuntimeException("Type not handled! ($type)")
        }
        return value
    }

    suspend fun updateExcel(
        file: String,
        sheetName: String,
        update: (sheet: org.apache.poi.ss.usermodel.Sheet) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val fis = FileInputStream(file)
            val workbook = XSSFWorkbook(fis)

            val sheet = workbook.getSheet(sheetName)
            update(sheet)

            fis.close()
            val os = FileOutputStream(file)
            workbook.write(os)
            workbook.close()
        }
    }

    /*
    fun findRow(sheet: org.apache.poi.ss.usermodel.Sheet, column: Int, value: String, ignoreCase: Boolean = true): RowNumber {
        val rows = sheet.lastRowNum
        var r = 0
        while (r <= rows) {
            val cell = getCell(sheet, r, column)
            val cellValue = readCellValue(cell)
            if (value.equals(cellValue, ignoreCase = ignoreCase))
                return RowNumber(r, false)
            r++
        }
        return RowNumber(r, true)
    }

    class RowNumber(
        val row: Int,
        val new: Boolean
    )*/
}