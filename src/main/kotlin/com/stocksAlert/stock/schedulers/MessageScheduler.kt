package com.stocksAlert.stock.schedulers

import com.stocksAlert.stock.config.EnvConfig
import com.stocksAlert.stock.domain.BuyableStock
import com.stocksAlert.stock.service.BuyableStockService
import com.stocksAlert.stock.utils.WebClientWrapper
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MessageScheduler(
    @Autowired private val buyableStockService: BuyableStockService,
    @Autowired private val envConfig: EnvConfig,
    private val webClient: WebClientWrapper
) {
    @Scheduled(cron = "0 0/5 3-10 * * 1-5")
    @SchedulerLock(name = "BestPriceScheduler_start", lockAtMostFor = "1m", lockAtLeastFor = "1m")
    fun start() {
        buyableStockService.getStocksUnsentAlert()
            .map {
                if (!it.isSendAlert) {
                    sendAlert(it)
                }
            }
            .subscribe()
    }

    private fun sendAlert(buyableStock: BuyableStock) {
        val body = createMessageBody(buyableStock)
        buyableStock.isSendAlert = true

        webClient.post(
            baseUrl = envConfig.webhookUri,
            path = "",
            body = body,
            returnType = String::class.java
        )
            .doOnSuccess {
                buyableStockService.save(buyableStock).block()
            }
            .subscribe()
    }

    private fun createMessageBody(buyableStock: BuyableStock): Map<String, Any> {
        return mapOf(
            "blocks" to listOf(
                mapOf(
                    "type" to "header",
                    "text" to mapOf(
                        "type" to "plain_text",
                        "text" to "${buyableStock.symbol} - ${buyableStock.LongName}",
                        "emoji" to true
                    )
                ),
                mapOf(
                    "type" to "section",
                    "fields" to listOf(
                        mapOf(
                            "type" to "mrkdwn",
                            "text" to "*Average Price*\nRs. ${buyableStock.averagePrice}"
                        ), mapOf("type" to "mrkdwn", "text" to "*Current Price*\nRs. ${buyableStock.Price}")
                    )
                )
            )
        )
    }
}
