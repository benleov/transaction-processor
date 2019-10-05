package com.mebank.codechallenge

import com.mebank.codechallenge.TransactionType.REVERSAL
import java.math.BigDecimal
import java.time.Instant

class BalanceCalculator {

    /**
     * Calculates the balance of a series of transactions across a given account.
     *
     * If a transaction has a reversing transaction, this transaction is omitted from the calculation,
     * even if the reversing transaction is outside the given time frame.
     *
     * @param accountId The account id of the account to calculate.
     * @param start Start time to being calculating from.
     * @param end End time to stop calculating at.
     * @param transactions List of transactions to process.
     * @return The total of all transaction within the provided time frame.
     */
    fun calculate(accountId: String, start: Instant, end: Instant, transactions: List<Transaction>): Result {

        // filter transactions to and from this account
        val accountTransactions = transactions.filter {
            accountId == it.fromAccountId || accountId == it.toAccountId
        }

        // map reversed transaction ids
        val reversals = accountTransactions
            .filter { it.transactionType == REVERSAL }
            .map { it.relatedTransaction }

        // filter reversed and reversal transactions between the start and end date
        val requiredTransactions = accountTransactions
            .filter {
                start.isBefore(it.createdAt)
                        && end.isAfter(it.createdAt)
                        && !reversals.contains(it.transactionId)
                        && it.transactionType != REVERSAL
            }

        // no transactions were found, return zero
        if (requiredTransactions.isEmpty()) {
            return Result(BigDecimal.ZERO, 0)
        }

        // sum the remaining transactions
        val total = requiredTransactions.map { it.getRelativeAmount(accountId) }
            .reduce { acc, curr ->
                acc + curr
            }

        return Result(total, requiredTransactions.size)
    }
}