package com.stocksAlert.stock.schedulers

import com.stocksAlert.stock.repository.BuyableStockRepository
import com.stocksAlert.stock.repository.StockRepository
import com.stocksAlert.stock.repository.SymbolRepository
import com.stocksAlert.stock.schedulers.builder.StockBuilder
import com.stocksAlert.stock.schedulers.builder.SymbolBuilder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal

@SpringBootTest
class BestPriceSchedulerTest(
    @Autowired private val stockRepository: StockRepository,
    @Autowired private val symbolRepository: SymbolRepository,
    @Autowired private val bestPriceScheduler: BestPriceScheduler,
    @Autowired private val buyableStockRepository: BuyableStockRepository
) {
    @BeforeEach
    fun setUp() {
        stockRepository.deleteAll().block()
        symbolRepository.deleteAll().block()
        buyableStockRepository.deleteAll().block()
    }

    @AfterEach
    fun tearDown() {
        stockRepository.deleteAll().block()
        symbolRepository.deleteAll().block()
        buyableStockRepository.deleteAll().block()
    }

    @Test
    fun `should find the best price for scheduler`() {
        val stock = StockBuilder(
            key = "abc",
            symbol = "AXISBANK",
            LastTrdTime = 10000000,
            LongName = "Axis Bank",
            UlaValue = BigDecimal(200),
            ATP = BigDecimal(210),
            PercentChange = 0.0,
            Price = BigDecimal(220),
            Change = BigDecimal(2),
            Volume = 0,
            TurnOver = 0,
            Open = BigDecimal(190),
            High = BigDecimal(220),
            Low = BigDecimal(180),
            PreCloseRate = BigDecimal(200),
            OI = BigDecimal(200),
            upperCircuit = 0,
            lowerCircuit = 0,
            Wk52High = BigDecimal(200),
            W2AvgQ = BigDecimal(200),
            Wk52low = BigDecimal(200),
            MCapFF = BigDecimal(200),
            MCapFull = BigDecimal(200)
        ).build()
        val stock1 = stock.copy(key = "bca", Price = BigDecimal(210))
        val stock2 = stock.copy(key = "cab", Price = BigDecimal(200))

        val symbol = SymbolBuilder(name = "AXISBANK").build()

        symbolRepository.saveAll(listOf(symbol)).blockLast()
        stockRepository.saveAll(listOf(stock, stock1, stock2)).blockLast()

        bestPriceScheduler.start()

        assertSoftly {
            val buyableStocks = buyableStockRepository.findAll().toIterable().toList()
            buyableStocks shouldHaveSize 1
            buyableStocks[0].Price shouldBe BigDecimal(200)
        }

    }
}
