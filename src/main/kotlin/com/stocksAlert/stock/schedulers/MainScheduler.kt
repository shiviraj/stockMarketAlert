package com.stocksAlert.stock.schedulers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class MainScheduler(
    @Autowired private val bestTradeableStockScheduler: BestTradeableStockScheduler,
    @Autowired private val messageScheduler: MessageScheduler,
    @Autowired private val stockFetcherScheduler: StockFetcherScheduler
) {

    @Scheduled(cron = "0 0/5 10-11 * * 1-5")
    fun start() {
        getCurrentTask().start()
    }

    private fun getCurrentTask(): Scheduler {
        val minute = LocalDateTime.now().minute
        return when {
            minute == 0 -> stockFetcherScheduler
            minute < 50 -> bestTradeableStockScheduler
            else -> messageScheduler
        }
    }
}
