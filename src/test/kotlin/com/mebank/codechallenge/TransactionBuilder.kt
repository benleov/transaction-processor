package com.mebank.codechallenge

import java.math.BigDecimal
import java.time.Instant

/**
 * Test class to construct mock transactions.
 */
class TransactionBuilder {

    /**
     * Build a list of transactions that increases linearly.
     */
    fun buildLinearTransactions(
        transactionCount: Int,
        startAmount: BigDecimal = BigDecimal("0.00"),
        increment: BigDecimal = BigDecimal("1.00"),
        startTime: Instant = Instant.now(),
        fromAccountId: String = "ACC334455",
        toAccountId: String = "ACC778899"
    ): List<Transaction> {

        return (0 until transactionCount).map {
            Transaction(
                transactionId = "TX${it + 1}",
                fromAccountId = fromAccountId,
                toAccountId = toAccountId,
                createdAt = startTime.plusMillis(1000 * it.toLong()),
                amount = startAmount.plus((increment).times(BigDecimal(it))),
                transactionType = TransactionType.PAYMENT,
                relatedTransaction = null
            )
        }
    }
}