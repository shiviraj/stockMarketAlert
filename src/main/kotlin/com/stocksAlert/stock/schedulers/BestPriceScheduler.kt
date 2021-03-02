package com.stocksAlert.stock.schedulers

import com.stocksAlert.stock.domain.BuyableStock
import com.stocksAlert.stock.domain.Stock
import com.stocksAlert.stock.domain.calculateAveragePrice
import com.stocksAlert.stock.service.BuyableStockService
import com.stocksAlert.stock.service.StockService
import com.stocksAlert.stock.service.SymbolService
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Component
class BestPriceScheduler(
    @Autowired private val stockService: StockService,
    @Autowired private val symbolService: SymbolService,
    @Autowired private val buyableStockService: BuyableStockService
) {

    @Scheduled(cron = "0 0/15 3-10 * * 1-6")
    @SchedulerLock(name = "BestPriceScheduler_start", lockAtMostFor = "1m", lockAtLeastFor = "1m")
    fun start() {
        symbolService.getAllSymbols().map {
            it.name
        }.flatMap { symbol ->
            fetchStocksBySymbol(symbol)
        }.map {
            calculateAverageAndUpdateDB(it)
        }.subscribe()
    }

    private fun calculateAverageAndUpdateDB(stocks: List<Stock>) {
        val sortedStocks = stocks.sortedBy { it.LastTrdTime }.reversed()
        val currentStock = sortedStocks[0]
        val averagePrice = stocks.calculateAveragePrice()
        if (isBuyablePrice(averagePrice, currentStock.Price)) {
            updateDB(averagePrice, currentStock)
        }
    }

    private fun updateDB(averagePrice: BigDecimal, currentStock: Stock) {
        val buyableStock = BuyableStock(
            key = currentStock.key,
            averagePrice = averagePrice,
            symbol = currentStock.symbol,
            LongName = currentStock.LongName,
            Price = currentStock.Price
        )

        buyableStockService
            .save(buyableStock)
            .subscribe()
    }

    private fun isBuyablePrice(averagePrice: BigDecimal, price: BigDecimal): Boolean {
        return averagePrice - (averagePrice * BigDecimal(System.getenv("DISCOUNT_PERCENT")) / BigDecimal(100)) > price
    }

    private fun fetchStocksBySymbol(symbol: String): Mono<List<Stock>> {
        return stockService.getAllBySymbol(symbol).collectList()
    }
}
