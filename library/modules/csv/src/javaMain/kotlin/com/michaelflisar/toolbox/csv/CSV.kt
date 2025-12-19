package com.michaelflisar.toolbox.csv

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.csv.CSVFormat
import java.io.File
import java.nio.charset.Charset

object CSV {

    suspend fun <T> readFile(
        file: File,
        skipLines: Int = 1,
        skipComments: Boolean = true,
        ignoreSurroundingSpaces: Boolean = true,
        ignoreEmptyLines: Boolean = true,
        delimiter: Char = ';',
        charset: Charset = Charsets.UTF_8,
        mapper: (record: List<String>) -> T?,
    ): List<T> {
        return withContext(Dispatchers.IO) {
            CSVFormat.Builder.create(CSVFormat.DEFAULT)
                .apply {
                    setIgnoreSurroundingSpaces(ignoreSurroundingSpaces)
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
                        mapper(parts.toList())
                    }
                }
        }
    }

    suspend fun <T> writeFile(
        file: File,
        manager: CSVManager<T>,
        data: List<T>,
        delimiter: Char = ';',
        charset: Charset = Charsets.UTF_8,
    ) {
        writeFile(
            file = file,
            headers = manager.headers,
            data = data,
            converter = { item -> manager.toList(item) },
            delimiter = delimiter,
            charset = charset
        )

    }

    suspend fun <T> writeFile(
        file: File,
        headers: List<String>,
        data: List<T>,
        converter: (item: T) -> List<String>,
        delimiter: Char = ';',
        charset: Charset = Charsets.UTF_8,
    ) {
        withContext(Dispatchers.IO) {
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

}

