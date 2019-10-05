package com.mebank.codechallenge

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.Instant

enum class TransactionType { PAYMENT, REVERSAL }

data class Transaction(
    val transactionId: String,
    val fromAccountId: String,
    val toAccountId: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:m:ss", timezone = "UTC")
    val createdAt: Instant,
    val amount: BigDecimal,
    val transactionType: TransactionType,
    val relatedTransaction: String? = null
)

fun Transaction.getRelativeAmount(accountId: String): BigDecimal =
    when (accountId) {
        fromAccountId -> amount.negate()
        toAccountId -> amount
        else -> throw RuntimeException("Invalid account id provided: $accountId")
    }
