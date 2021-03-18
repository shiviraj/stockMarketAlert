package com.stocksAlert.stock.schedulers

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class MainScheduler(
    @Autowired private val bestPriceScheduler: BestPriceScheduler,
    @Autowired private val messageScheduler: MessageScheduler,
    @Autowired private val stockFetcherScheduler: StockFetcherScheduler
) {

    @Scheduled(cron = "0 0/10 3-10 * * 1-5")
    @SchedulerLock(name = "Scheduler_start", lockAtMostFor = "1m", lockAtLeastFor = "1m")
    fun start() {
        getCurrentTask().start()
    }

    private fun getCurrentTask(): Scheduler {
        return when (LocalDateTime.now().second) {
            0 -> bestPriceScheduler
            30 -> stockFetcherScheduler
            else -> messageScheduler
        }
    }
}
