package com.stocksAlert.stock.schedulers

import com.stocksAlert.stock.domain.Stock
import com.stocksAlert.stock.domain.Symbol
import com.stocksAlert.stock.service.BuyableStockService
import com.stocksAlert.stock.service.StockService
import com.stocksAlert.stock.service.SymbolService
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class UniqueSymbolScheduler(
    @Autowired private val stockService: StockService,
    @Autowired private val symbolService: SymbolService,
    @Autowired private val buyableStockService: BuyableStockService
) {

    @Scheduled(cron = "0 0 8 * * 0")
    @SchedulerLock(name = "BestPriceScheduler_start", lockAtMostFor = "1m", lockAtLeastFor = "1m")
    fun start() {
        stockService.getAll()
            .collectList()
            .map {
                it.distinctBy { stock ->
                    stock.symbol
                }
            }
            .map {
                updateSymbol(it)
            }
            .subscribe()
    }

    private fun updateSymbol(it: List<Stock>) {
        it.forEach { stock ->
            symbolService.save(Symbol(name = stock.symbol)).subscribe()
        }
    }
}
