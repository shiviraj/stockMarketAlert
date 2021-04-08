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
import java.util.regex.Pattern
import kotlin.math.absoluteValue

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
        try {
            val stocksEvaluations = evaluateGraph(stocks.subList(0, 20))
            val last2to12StocksEvaluation = stocksEvaluations.subList(2, 12)
            val firstTwoStockEvaluation = stocksEvaluations.subList(0, 2)
            val containSameGrow = containSameGrow(last2to12StocksEvaluation)
            if (containSameGrow["isContainSame"] as Boolean) {
                return updateIfTradeable(firstTwoStockEvaluation, stocks.first(), containSameGrow["grow"] as Grow)
            }
        } catch (e: IndexOutOfBoundsException) {
            println("Insufficient stocks")
        }
        return Mono.empty()
    }

    private fun updateIfTradeable(
        firstTwoStocks: List<StockEvaluation>,
        stock: Stock,
        grow: Grow
    ): Mono<TradeableStock> {
        try {
            val tradeableStock = TradeableStock(
                key = stock.key.split(Pattern.compile("T[0-9]{2}:[0-9]{2}"))[0],
                symbol = stock.symbol,
                averagePrice = stock.averagePrice(),
                LongName = stock.LongName,
                Price = stock.Price,
                Type = calculateTradeType(firstTwoStocks, grow)
            )
            return tradeableStockService.save(tradeableStock)
        } catch (e: DuplicateKeyException) {
            println("Duplicate key error")
        }
        return Mono.empty()
    }

    private fun calculateTradeType(stocks: List<StockEvaluation>, grow: Grow): String {
        val firstStockGrow = stocks.first().stockGrow
        return if (firstStockGrow == stocks[1].stockGrow && firstStockGrow != grow) {
            if (firstStockGrow == Grow.DOWN) "SELL" else "BUY"
        } else "ALERT"
    }

    private fun containSameGrow(stocksEvaluations: List<StockEvaluation>): Map<String, Any> {
        val noOfUpGoing = stocksEvaluations.count {
            it.stockGrow == Grow.UP
        }
        val result = mutableMapOf<String, Any>()
        result["isContainSame"] = noOfUpGoing.isNotInBetween(2, 8)
        result["grow"] = if (noOfUpGoing <= 3) Grow.UP else Grow.DOWN
        return result
    }

    private fun evaluateGraph(stocks: List<Stock>): List<StockEvaluation> {
        val allStocks = stocks.reversed()
        var previousStockPrice = allStocks.first().averagePrice()
        return allStocks.map {
            val averagePrice = it.averagePrice()
            val difference = averagePrice - previousStockPrice
            val stockGrowResult = if (difference < 0) Grow.DOWN else Grow.UP
            previousStockPrice = averagePrice
            StockEvaluation(stockGrowResult, difference.absoluteValue, averagePrice)
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
