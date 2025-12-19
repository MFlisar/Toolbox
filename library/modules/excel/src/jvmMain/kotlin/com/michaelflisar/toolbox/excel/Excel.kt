package com.michaelflisar.toolbox.excel

import com.michaelflisar.toolbox.excel.classes.ExcelFoundRow
import com.michaelflisar.toolbox.excel.classes.ExcelSheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.ss.util.CellReference
import org.apache.poi.ss.util.SheetUtil.getCell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object Excel {

    fun columnString(index: Int): String {
        return CellReference.convertNumToColString(index)
    }

    fun columnIndex(column: String): Int {
        return CellReference.convertColStringToIndex(column)
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
        file: File,
        sheetName: String,
        update: (sheet: ExcelSheet) -> Boolean
    ) {
        updateExcel(
            file = file.absolutePath,
            sheetName = sheetName,
            update = update
        )
    }

    suspend fun updateExcel(
        file: String,
        sheetName: String,
        update: (sheet: ExcelSheet) -> Boolean
    ) {
        withContext(Dispatchers.IO) {

            val fis = FileInputStream(file)
            val workbook = XSSFWorkbook(fis)
            val sheet = workbook.getSheet(sheetName)
            val updated = update(ExcelSheet(sheet))
            fis.close()

            if (updated) {
                val os = FileOutputStream(file)
                workbook.write(os)
                os.close()
            }

            workbook.close()
        }
    }

    fun findRow(
        sheet: ExcelSheet,
        column: String,
        matcher: (cellValue: String) -> Boolean,
    ): ExcelFoundRow {
        val sheet = sheet.sheet as org.apache.poi.ss.usermodel.Sheet
        val rows = sheet.lastRowNum
        var r = 0
        val c = columnIndex(column)
        while (r <= rows) {
            val cell = getCell(sheet, r, c)
            if (cell != null) {
                val cellValue = readCellValue(cell)
                if (matcher(cellValue))
                    return ExcelFoundRow(r, false)
            }
            r++
        }
        return ExcelFoundRow(r, true)
    }

    fun updateCell(
        sheet: ExcelSheet,
        rowIndex: Int,
        column: String,
        value: String
    ) {
        val sheet = sheet.sheet as org.apache.poi.ss.usermodel.Sheet
        val c = columnIndex(column)
        val r = sheet.getRow(rowIndex) ?: sheet.createRow(rowIndex)
        val cell = r.getCell(c) ?: r.createCell(c)
        cell.setCellValue(value)
    }

}