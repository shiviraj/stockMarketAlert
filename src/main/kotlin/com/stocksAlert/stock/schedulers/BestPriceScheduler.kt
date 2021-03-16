package com.stocksAlert.stock.schedulers

import com.stocksAlert.stock.domain.Stock
import com.stocksAlert.stock.domain.TradeableStock
import com.stocksAlert.stock.schedulers.view.Grow
import com.stocksAlert.stock.schedulers.view.StockEvaluation
import com.stocksAlert.stock.service.BuyableStockService
import com.stocksAlert.stock.service.StockService
import com.stocksAlert.stock.service.SymbolService
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class BestPriceScheduler(
    @Autowired private val stockService: StockService,
    @Autowired private val symbolService: SymbolService,
    @Autowired private val buyableStockService: BuyableStockService
) {

    @Scheduled(cron = "0 0/15 3-10 * * 1-5")
    @SchedulerLock(name = "BestPriceScheduler_start", lockAtMostFor = "1m", lockAtLeastFor = "1m")
    fun start() {
        symbolService.getAllSymbols()
            .flatMap { symbol ->
                fetchLastStocksBySymbol(symbol.name)
            }.map {
                calculateUpDownMarketAndUpdateDB(it)
            }.subscribe()
    }

    private fun calculateUpDownMarketAndUpdateDB(stocks: List<Stock>) {
        val now = LocalDateTime.now().toString().split("T")[0]
        val allStocks = stocks.filter {
            !it.key.contains(Regex(".*${now}T.*"))
        }
        val stocksEvaluations = evaluateGraph(allStocks)
        try {
            val last2to12StocksEvaluation = stocksEvaluations.subList(2, 12)
            val firstTwoStockEvaluation = stocksEvaluations.subList(0, 1)
            if (isContainSameGrow(last2to12StocksEvaluation)) {
                updateIfTradeable(firstTwoStockEvaluation.first(), last2to12StocksEvaluation.first(), stocks.first())
            }
        } catch (e: IndexOutOfBoundsException) {
            println("Insufficient stocks")
        }
    }

    private fun updateIfTradeable(first: StockEvaluation, stockEvaluation: StockEvaluation, stock: Stock) {
        if (first.stockGrow == stockEvaluation.stockGrow) {
            val buyableStock = TradeableStock(
                key = stock.key,
                symbol = stock.symbol,
                averagePrice = stock.averagePrice(),
                calculatedOn = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC),
                LongName = stock.LongName,
                Price = stock.Price,
                Type = if (first.stockGrow == Grow.UP) "BUY" else "DOWN"
            )
            buyableStockService.save(buyableStock).subscribe()
        }
    }

    private fun isContainSameGrow(stocksEvaluation: List<StockEvaluation>): Boolean {
        return stocksEvaluation.distinctBy {
            it.stockGrow
        }.size == 1

    }

    private fun evaluateGraph(allStocks: List<Stock>): List<StockEvaluation> {
        var previousStockPrice = allStocks.first().averagePrice()
        return allStocks.map {
            val averagePrice = it.averagePrice()
            val difference = averagePrice - previousStockPrice
            val stockGrowResult = if (difference < BigDecimal(0)) Grow.UP else Grow.DOWN
            previousStockPrice = averagePrice
            StockEvaluation(stockGrowResult, difference.abs(), averagePrice)
        }
    }

    private fun fetchLastStocksBySymbol(symbol: String): Mono<List<Stock>> {
        return stockService.getAllBySymbol(symbol).collectList()
    }
}
