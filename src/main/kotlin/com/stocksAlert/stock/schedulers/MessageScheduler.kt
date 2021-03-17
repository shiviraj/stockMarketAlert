package com.stocksAlert.stock.schedulers

import com.stocksAlert.stock.config.EnvConfig
import com.stocksAlert.stock.domain.TradeableStock
import com.stocksAlert.stock.service.TradeableStockService
import com.stocksAlert.stock.utils.WebClientWrapper
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MessageScheduler(
    @Autowired private val tradeableStockService: TradeableStockService,
    @Autowired private val envConfig: EnvConfig,
    private val webClient: WebClientWrapper
) {
    @Scheduled(cron = "0 0/5 3-10 * * 1-5")
    @SchedulerLock(name = "BestPriceScheduler_start", lockAtMostFor = "1m", lockAtLeastFor = "1m")
    fun start() {
        tradeableStockService.getStocksUnsentAlert()
            .map {
                sendAlert(it)
            }
            .subscribe()
    }

    private fun sendAlert(tradeableStock: TradeableStock) {
        val body = createMessageBody(tradeableStock)
        tradeableStock.isSendAlert = true

        webClient.post(
            baseUrl = envConfig.webhookUri,
            path = "",
            body = body,
            returnType = String::class.java
        )
            .doOnSuccess {
                tradeableStockService.save(tradeableStock).block()
            }
            .subscribe()
    }

    private fun createMessageBody(tradeableStock: TradeableStock): Map<String, Any> {
        return mapOf(
            "blocks" to listOf(
                mapOf(
                    "type" to "header",
                    "text" to mapOf(
                        "type" to "plain_text",
                        "text" to "${tradeableStock.symbol} - ${tradeableStock.LongName}",
                        "emoji" to true
                    )
                ),
                mapOf(
                    "type" to "section",
                    "fields" to listOf(
                        mapOf(
                            "type" to "mrkdwn",
                            "text" to "*Average Price*\nRs. ${tradeableStock.averagePrice}"
                        ),
                        mapOf("type" to "mrkdwn", "text" to "*Current Price*\nRs. ${tradeableStock.Price}"),
                    )
                ),
                mapOf(
                    "type" to "section",
                    "fields" to listOf(
                        mapOf("type" to "mrkdwn", "text" to "*Trade Type*\nRs. ${tradeableStock.Type}")
                    )
                )
            )
        )
    }
}
