package com.mebank.codechallenge

import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvParser
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File

class Mapper {

    private val mapper = CsvMapper()
        .registerModule(KotlinModule())
        .registerModule(JavaTimeModule())

    fun read(filename: String): List<Transaction>? {

        val csvFile = File(filename)
        val schema = CsvSchema.emptySchema().withHeader()
        return mapper.readerFor(Transaction::class.java)
            .with(schema)
            .with(CsvParser.Feature.TRIM_SPACES)
            .readValues<Transaction>(csvFile)
            .readAll()
            .toList()
    }
}
