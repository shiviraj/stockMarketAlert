package com.stocksAlert.stock.schedulers

import com.stocksAlert.stock.service.StockService
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component


@Component
class RemoveUnnecessaryStocksScheduler(
    @Autowired private val stockService: StockService
) {

    @Scheduled(cron = "0 0 3 * * *")
    @SchedulerLock(name = "RemoveUnnecessaryStocksScheduler_start", lockAtMostFor = "1m", lockAtLeastFor = "1m")
    fun start() {
        stockService.regexQueryInKey("^(?!.*(16:00:00|21:30:00)).*")
            .flatMap {
                stockService.delete(it)
            }
            .subscribe()
    }
}
