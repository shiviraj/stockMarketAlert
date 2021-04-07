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
        lateinit var stocksEvaluations: List<StockEvaluation>
        try {
            stocksEvaluations = evaluateGraph(stocks).subList(0, 20)
        } catch (e: IndexOutOfBoundsException) {
            println("Insufficient stocks")
        }

        val last2to12StocksEvaluation = stocksEvaluations.subList(2, 12)
        val firstTwoStockEvaluation = stocksEvaluations.subList(0, 2)
        if (isContainSameGrow(last2to12StocksEvaluation)) {
            return updateIfTradeable(firstTwoStockEvaluation, stocks.first())
        }
        return Mono.empty()
    }

    private fun updateIfTradeable(
        firstTwoStocks: List<StockEvaluation>,
        stock: Stock
    ): Mono<TradeableStock> {
        try {
            val tradeableStock = TradeableStock(
                key = stock.key.split(Pattern.compile("T[0-9]{2}:[0-9]{2}"))[0],
                symbol = stock.symbol,
                averagePrice = stock.averagePrice(),
                LongName = stock.LongName,
                Price = stock.Price,
                Type = calculateTradeType(firstTwoStocks)
            )
            return tradeableStockService.save(tradeableStock)
        } catch (e: DuplicateKeyException) {
            println("Duplicate key error")
        }
        return Mono.empty()
    }

    private fun calculateTradeType(stocks: List<StockEvaluation>): String {
        return when {
            stocks.first().stockGrow == stocks[1].stockGrow && stocks.first().stockGrow == Grow.UP -> "BUY"
            stocks.first().stockGrow == stocks[1].stockGrow && stocks.first().stockGrow == Grow.DOWN -> "SELL"
            else -> "ALERT"
        }
    }

    private fun isContainSameGrow(stocksEvaluations: List<StockEvaluation>): Boolean {
        val noOfUpGoing = stocksEvaluations.count {
            it.stockGrow == Grow.UP
        }
        return noOfUpGoing.isNotInBetween(2, 8)
    }

    private fun evaluateGraph(stocks: List<Stock>): List<StockEvaluation> {
        val allStocks = stocks.reversed()
        var previousStockPrice = allStocks.first().averagePrice()
        return allStocks.map {
            val averagePrice = it.averagePrice()
            val difference = averagePrice - previousStockPrice
            val stockGrowResult = if (difference < BigDecimal(0)) Grow.DOWN else Grow.UP
            previousStockPrice = averagePrice
            StockEvaluation(stockGrowResult, difference.abs(), averagePrice)
        }.reversed()
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
    return this <= start || this >= end
}
