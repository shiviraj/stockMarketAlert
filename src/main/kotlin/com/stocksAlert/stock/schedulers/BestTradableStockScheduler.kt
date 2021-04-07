package com.stocksAlert.stock.schedulers

import com.stocksAlert.stock.domain.Stock
import com.stocksAlert.stock.domain.TradeableStock
import com.stocksAlert.stock.schedulers.view.Grow
import com.stocksAlert.stock.schedulers.view.StockEvaluation
import com.stocksAlert.stock.service.StockService
import com.stocksAlert.stock.service.SymbolService
import com.stocksAlert.stock.service.TradeableStockService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.regex.Pattern

@Component
class BestTradeableStockScheduler(
    @Autowired private val stockService: StockService,
    @Autowired private val symbolService: SymbolService,
    @Autowired private val tradeableStockService: TradeableStockService
) : Scheduler {

    override fun start() {
        symbolService.getAllSymbols()
            .flatMap { symbol ->
                fetchLastStocksBySymbol(symbol.name)
            }
            .flatMap {
                calculateUpDownMarketAndUpdateDB(it).onErrorResume { Mono.empty() }
            }
            .subscribe()
    }


    private fun calculateUpDownMarketAndUpdateDB(stocks: List<Stock>): Mono<TradeableStock> {
        val now = LocalDateTime.now().toString().split("T")[0]
        val allStocks = stocks.filter {
            !it.key.contains(Regex(".*${now}T.*"))
        }
        val stocksEvaluations = evaluateGraph(allStocks)
        try {
            val last2to12StocksEvaluation = stocksEvaluations.subList(2, 12)
            val firstTwoStockEvaluation = stocksEvaluations.subList(0, 2)
            if (isContainSameGrow(last2to12StocksEvaluation)) {
                return updateIfTradeable(
                    firstTwoStockEvaluation.first(),
                    last2to12StocksEvaluation.first(),
                    stocks.first()
                )
            }
        } catch (e: IndexOutOfBoundsException) {
            println("Insufficient stocks")
        }
        return Mono.empty()
    }

    private fun updateIfTradeable(
        first: StockEvaluation,
        stockEvaluation: StockEvaluation,
        stock: Stock
    ): Mono<TradeableStock> {
        try {
            val tradeableStock = TradeableStock(
                key = stock.key.split(Pattern.compile("T[0-9]{2}:[0-9]{2}"))[0],
                symbol = stock.symbol,
                averagePrice = stock.averagePrice(),
                LongName = stock.LongName,
                Price = stock.Price,
                Type = calculateTradeType(first, stockEvaluation)
            )
            return tradeableStockService.save(tradeableStock)
        } catch (e: DuplicateKeyException) {
            println("Duplicate key error")
        }
        return Mono.empty()
    }

    private fun calculateTradeType(
        first: StockEvaluation,
        stockEvaluation: StockEvaluation
    ): String {
        return when {
            first.stockGrow != stockEvaluation.stockGrow && first.stockGrow == Grow.UP -> "BUY"
            first.stockGrow != stockEvaluation.stockGrow && first.stockGrow == Grow.DOWN -> "SELL"
            else -> "ALERT"
        }
    }

    private fun isContainSameGrow(stocksEvaluations: List<StockEvaluation>): Boolean {
        val noOfUpGoing = stocksEvaluations.count {
            it.stockGrow == Grow.UP
        }
        return noOfUpGoing.isNotInBetween(2, 8)
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
        return stockService.getAllBySymbol(symbol)
            .collectList()
            .filter { it.isNotEmpty() }
            .map {
                it.sortBy { stock -> stock.LastTrdTime }
                it.reversed()
            }
    }
}

private fun Int.isNotInBetween(start: Int, end: Int): Boolean {
    return this < start || this > end
}
