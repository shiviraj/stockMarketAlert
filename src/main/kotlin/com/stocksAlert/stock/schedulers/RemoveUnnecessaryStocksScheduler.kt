package com.stocksAlert.stock.schedulers

import com.stocksAlert.stock.service.StockService
import com.stocksAlert.stock.service.TradeableStockService
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class RemoveUnnecessaryStocksScheduler(
    @Autowired private val stockService: StockService,
    @Autowired private val tradeableStockService: TradeableStockService
) {

    @Scheduled(cron = "0 0 3 * * 1-5")
    @SchedulerLock(name = "RemoveUnnecessaryStocksScheduler_start", lockAtMostFor = "1m", lockAtLeastFor = "1m")
    fun start() {
        stockService.regexQueryInKey("^(?!.*(16:00:00|21:30:00)).*")
            .collectList()
            .flatMap {
                stockService.deleteAll(it)
            }
            .subscribe()

        tradeableStockService.deleteAll().subscribe()
    }
}
