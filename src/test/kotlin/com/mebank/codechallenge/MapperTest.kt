package com.mebank.codechallenge

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant

class MapperTest {

    private lateinit var mapper: Mapper

    @BeforeEach
    fun setup() {
        mapper = Mapper()
    }

    @Test
    fun `when csv file is valid`() {
        val result = mapper.read("src/test/resources/mapper-test.csv")

        assertThat(result).isNotEmpty
        assertThat(result).first().isEqualTo(
            Transaction(
                transactionId = "TX10001",
                fromAccountId = "ACC334455",
                toAccountId = "ACC778899",
                createdAt = Instant.parse("2018-10-20T12:47:55.000Z"),
                amount = BigDecimal("25.00"),
                transactionType = TransactionType.PAYMENT,
                relatedTransaction = null
            )
        )
    }
}
