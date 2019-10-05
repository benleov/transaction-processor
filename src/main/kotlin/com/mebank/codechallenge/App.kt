package com.mebank.codechallenge

import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class App {

    fun run(accountId: String, transactionFilePath: String, startDate: String, endDate: String): Result {

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy'T'HH:mm:ss")

        val start = formatter.parse(startDate).let {
            LocalDateTime.from(it).toInstant(ZoneOffset.UTC)
        }

        val end = formatter.parse(endDate).let {
            LocalDateTime.from(it).toInstant(ZoneOffset.UTC)
        }

        val transactions = Mapper().read(transactionFilePath)

        if (transactions.isNullOrEmpty()) {
            return Result(BigDecimal.ZERO, 0)
        }

        return BalanceCalculator().calculate(accountId, start, end, transactions)
    }
}

/**
 * Application entry point; called by gradle.
 */
fun main(args: Array<String>) {

    // this could be better handled using something like picocli
    val split = args[0].split(",")
    val result = App().run(split[0], split[1], split[2], split[3])
    val totalString = if (result.total >= BigDecimal.ZERO) "${result.total}" else "-$${result.total.abs()}"

    println("Relative balance for the period is: $totalString")
    println("Number of transactions included is: ${result.transactionCount}")
}