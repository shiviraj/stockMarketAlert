package com.stocksAlert.stock.schedulers

import com.stocksAlert.stock.domain.BuyableStock
import com.stocksAlert.stock.service.BuyableStockService
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class MessageScheduler(
    @Autowired private val buyableStockService: BuyableStockService
) {
    private val webClient = WebClient.builder().build()

    @Scheduled(cron = "0 0/15,30/45 3-10 * * 1-6")
    @SchedulerLock(name = "BestPriceScheduler_start", lockAtMostFor = "1m", lockAtLeastFor = "1m")
    fun start() {
        buyableStockService.getAll()
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

        webClient.post()
            .uri(System.getenv("WEBHOOK_URI"))
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(String::class.java)
            .doOnSuccess {
                buyableStockService.save(buyableStock)
            }
            .retry(3)
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
