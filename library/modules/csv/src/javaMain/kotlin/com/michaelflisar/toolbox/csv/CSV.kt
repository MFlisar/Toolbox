package com.michaelflisar.toolbox.csv

import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVRecord
import java.io.File

object CSV {

    fun <T> readFile(
        file: File,
        skipLines: Int = 1,
        skipComments: Boolean = true,
        ignoreSourroundingSpaces: Boolean = true,
        ignoreEmptyLines: Boolean = true,
        delimiter: Char = ';',
        mapper: (record: CSVRecord) -> T?
    ): List<T> {
        return CSVFormat.Builder.create(CSVFormat.DEFAULT)
            .apply {
                setIgnoreSurroundingSpaces(ignoreSourroundingSpaces)
                setIgnoreEmptyLines(ignoreEmptyLines)
                setDelimiter(delimiter)
            }.build().parse(file.reader(Charsets.UTF_8))
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

}

