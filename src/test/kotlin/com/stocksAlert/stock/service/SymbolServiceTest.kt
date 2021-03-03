package com.stocksAlert.stock.service

import com.stocksAlert.stock.repository.SymbolRepository
import com.stocksAlert.stock.schedulers.builder.SymbolBuilder
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SymbolServiceTest(
    @Autowired private val symbolRepository: SymbolRepository,
    @Autowired private val symbolService: SymbolService
) {
    @BeforeEach
    fun setUp() {
        symbolRepository.deleteAll().block()
    }

    @AfterEach
    fun tearDown() {
        symbolRepository.deleteAll().block()
    }

    @Test
    fun `should save symbol in db`() {
        val symbol = SymbolBuilder(name = "AXIS").build()
        symbolService.save(symbol).block()
        assertSoftly {
            val symbols = symbolRepository.findAll().toIterable().toList()
            symbols shouldHaveSize 1
            symbols[0].name shouldBe "AXIS"
        }
    }

    @Test
    fun `should find all symbols stored in db`() {
        val symbol = SymbolBuilder(name = "AXIS").build()
        val symbol1 = symbol.copy(name = "ICICI")
        symbolRepository.saveAll(listOf(symbol, symbol1)).blockLast()

        val allSymbols = symbolService.getAllSymbols().toIterable().toList()

        assertSoftly(allSymbols) {
            it shouldHaveSize 2
            it[0].name shouldBe "AXIS"
            it[1].name shouldBe "ICICI"
        }
    }
}
