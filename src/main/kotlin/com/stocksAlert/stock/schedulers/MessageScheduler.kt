package com.stocksAlert.stock.schedulers

import com.stocksAlert.stock.config.EnvConfig
import com.stocksAlert.stock.domain.TradeableStock
import com.stocksAlert.stock.service.TradeableStockService
import com.stocksAlert.stock.utils.WebClientWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MessageScheduler(
    @Autowired private val tradeableStockService: TradeableStockService,
    @Autowired private val envConfig: EnvConfig,
    private val webClient: WebClientWrapper
) : Scheduler {
    override fun start() {
        tradeableStockService.getStocksUnsentAlert()
            .flatMap { sendAlert(it) }
            .subscribe()
    }

    private fun sendAlert(tradeableStock: TradeableStock): Mono<String> {
        val body = createMessageBody(tradeableStock)
        tradeableStock.isSendAlert = true

        return webClient.post(
            baseUrl = envConfig.webhookUri,
            path = "",
            body = body,
            returnType = String::class.java
        )
            .doOnSuccess {
                tradeableStockService.save(tradeableStock).block()
            }
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
