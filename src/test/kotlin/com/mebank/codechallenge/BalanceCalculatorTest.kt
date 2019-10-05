package com.mebank.codechallenge

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class BalanceCalculatorTest {

    private lateinit var balanceCalculator: BalanceCalculator

    @BeforeEach
    fun setup() {
        balanceCalculator = BalanceCalculator()
    }

    @Test
    fun `when linear positive transaction sequence`() {

        val destinationAccount = "ACCOUNT2"
        val transactions = TransactionBuilder()
            .buildLinearTransactions(
                10,
                BigDecimal("1.00"),
                BigDecimal("1.00"),
                Instant.parse("2000-01-01T01:00:00.000Z"),
                "ACCOUNT1",
                destinationAccount
            )

        val result = balanceCalculator
            .calculate(destinationAccount, Instant.MIN, Instant.MAX, transactions)

        // until is not inclusive
        val expected = (1 until 11).reduce { acc, curr -> acc + curr }
        assertThat(result.total).isEqualByComparingTo(BigDecimal(expected))
    }

    @Test
    fun `when linear negative transaction sequence`() {

        val destinationAccount = "ACCOUNT2"
        val transactions = TransactionBuilder()
            .buildLinearTransactions(
                10,
                BigDecimal("-1.00"),
                BigDecimal("-1.00"),
                Instant.parse("2000-01-01T01:00:00.000Z"),
                "ACCOUNT1",
                "ACCOUNT2"
            )

        val result = balanceCalculator
            .calculate(destinationAccount, Instant.MIN, Instant.MAX, transactions)

        // until is not inclusive
        val expected = (1 until 11).reduce { acc, curr -> acc + curr } * -1
        assertThat(result.total).isEqualByComparingTo(BigDecimal(expected))
    }

    @Test
    fun `when linear positive transaction subset of three transactions`() {

        // start off at one second past so offset from start matches transaction amount
        // to make it a bit easier to follow
        val transactionsStartTime = Instant.parse("2000-01-01T01:00:01.000Z")
        val destinationAccount = "ACCOUNT2"

        val transactions = TransactionBuilder()
            .buildLinearTransactions(
                60,
                BigDecimal("1.00"),
                BigDecimal("1.00"),
                transactionsStartTime,
                "ACCOUNT1",
                destinationAccount
            )

        // not inclusive
        val startTime = Instant.parse("2000-01-01T01:00:10.000Z")
        val endTime = Instant.parse("2000-01-01T01:00:14.000Z")

        val result = balanceCalculator.calculate(destinationAccount, startTime, endTime, transactions)

        assertThat(result.total).isEqualByComparingTo(BigDecimal(36)) // 11,12,13
    }

    @Test
    fun `when linear positive transaction subset of three transactions and contains reversal`() {

        val transactionsStartTime = Instant.parse("2000-01-01T01:00:01.000Z")
        val destinationAccount = "ACCOUNT2"

        val transactions = TransactionBuilder()
            .buildLinearTransactions(
                20,
                BigDecimal("1.00"),
                BigDecimal("1.00"),
                transactionsStartTime,
                "ACCOUNT1",
                destinationAccount
            )

        val withReversal = mutableListOf<Transaction>().also {
            it.addAll(transactions)
            it.add(
                Transaction(
                    transactionId = "REVERSAL",
                    fromAccountId = destinationAccount,
                    toAccountId = "ACC778899",
                    createdAt = transactionsStartTime.plusMillis(1000 * 20),
                    amount = BigDecimal(12),
                    transactionType = TransactionType.REVERSAL,
                    relatedTransaction = "TX12"
                )
            )
        }

        // not inclusive
        val startTime = Instant.parse("2000-01-01T01:00:10.000Z")
        val endTime = Instant.parse("2000-01-01T01:00:14.000Z")

        val result = balanceCalculator.calculate(destinationAccount, startTime, endTime, withReversal)
        // period covers 11, 12 and 13 but 12 has been reversed.
        assertThat(result.total).isEqualByComparingTo(BigDecimal(24))
    }

    @Test
    fun `complex test`() {

        val mapper = Mapper()
        val transactions = mapper.read("src/test/resources/complex-test.csv")

        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

        val start = formatter.parse("20/10/2018 12:47:54").let {
            LocalDateTime.from(it).toInstant(ZoneOffset.UTC)
        }

        val end = formatter.parse("21/10/2018 09:30:01").let {
            LocalDateTime.from(it).toInstant(ZoneOffset.UTC)
        }

        val result1 = balanceCalculator.calculate(
            "1",
            start,
            end,
            transactions!!
        )
        assertThat(result1.total).isEqualByComparingTo(BigDecimal(-32.25))
        assertThat(result1.transactionCount).isEqualTo(2)

        val result2 = balanceCalculator.calculate(
            "2",
            start,
            end,
            transactions
        )
        assertThat(result2.total).isEqualByComparingTo(BigDecimal(37.25))
        assertThat(result2.transactionCount).isEqualTo(3)

        val result3 = balanceCalculator.calculate(
            "3",
            start,
            end,
            transactions
        )

        assertThat(result3.total).isEqualByComparingTo(BigDecimal(-5))
        assertThat(result3.transactionCount).isEqualTo(1)
    }
}
