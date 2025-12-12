package com.michaelflisar.toolbox.csv

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.File
import java.nio.charset.Charset

object CSV {

    fun <T> readFile(
        file: File,
        skipLines: Int = 1,
        skipComments: Boolean = true,
        ignoreSourroundingSpaces: Boolean = true,
        ignoreEmptyLines: Boolean = true,
        delimiter: Char = ';',
        charset: Charset = Charsets.UTF_8,
        mapper: (record: CSVRecord) -> T?,
    ): List<T> {
        return CSVFormat.Builder.create(CSVFormat.DEFAULT)
            .apply {
                setIgnoreSurroundingSpaces(ignoreSourroundingSpaces)
                setIgnoreEmptyLines(ignoreEmptyLines)
                setDelimiter(delimiter)
            }
            .get()
            .parse(file.reader(charset))
            .drop(skipLines)
            .mapNotNull { parts ->
                if (skipComments && parts[0].startsWith("//")) {
                    // überspringen
                    null
                } else {
                    mapper(parts)
                }
            }
    }

    fun <T> writeFile(
        file: File,
        headers: List<String>,
        data: List<T>,
        converter: (item: T) -> List<String>,
        delimiter: Char = ';',
        charset: Charset = Charsets.UTF_8,
    ) {
        val csvFormat = CSVFormat.Builder.create(CSVFormat.DEFAULT)
            .setDelimiter(delimiter)
            .setHeader(*headers.toTypedArray())
            .get()

        file.bufferedWriter(charset).use { writer ->
            val csvPrinter = org.apache.commons.csv.CSVPrinter(writer, csvFormat)
            data.forEach { csvPrinter.printRecord(converter(it)) }
            csvPrinter.flush()
        }
    }

}

