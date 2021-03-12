package com.stocksAlert.stock.schedulers

import com.stocksAlert.stock.config.EnvConfig
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
import java.time.LocalDateTime

@Component
class BestPriceScheduler(
    @Autowired private val stockService: StockService,
    @Autowired private val symbolService: SymbolService,
    @Autowired private val buyableStockService: BuyableStockService,
    @Autowired private val envConfig: EnvConfig
) {

    @Scheduled(cron = "0 0/15 3-10 * * 1-6")
    @SchedulerLock(name = "BestPriceScheduler_start", lockAtMostFor = "1m", lockAtLeastFor = "1m")
    fun start() {
        symbolService.getAllSymbols()
            .flatMap { symbol ->
                fetchLastStocksBySymbol(symbol.name)
            }.map {
                calculateAverageAndUpdateDB(it)
            }.subscribe()
    }

    private fun calculateAverageAndUpdateDB(stocks: List<Stock>) {
        val now = LocalDateTime.now().toString().split("T")[0]
        val currentStock = stocks.last {
            it.key.contains(Regex(".*${now}T.*"))
        }
        val averagePrice = stocks.filter {
            !it.key.contains(Regex(".*${now}T.*"))
        }.calculateAveragePrice()

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
            .save(buyableStock).subscribe()
    }

    private fun isBuyablePrice(averagePrice: BigDecimal, price: BigDecimal): Boolean {
        val discount = averagePrice * BigDecimal(envConfig.discountPercent) / BigDecimal(100)
        return averagePrice - discount > price
    }

    private fun fetchLastStocksBySymbol(symbol: String): Mono<List<Stock>> {
        return stockService.getAllBySymbol(symbol).collectList()
    }
}
