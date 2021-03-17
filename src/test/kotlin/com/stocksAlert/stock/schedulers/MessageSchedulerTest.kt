package com.stocksAlert.stock.schedulers

import com.stocksAlert.stock.config.EnvConfig
import com.stocksAlert.stock.repository.TradeableStockRepository
import com.stocksAlert.stock.schedulers.builder.BuyableStockBuilder
import com.stocksAlert.stock.service.TradeableStockService
import com.stocksAlert.stock.utils.WebClientWrapper
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.SoftAssertions.assertSoftly
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.math.BigDecimal

@SpringBootTest
class MessageSchedulerTest(
    @Autowired private val tradeableStockRepository: TradeableStockRepository
) {
    private val webClientWrapper = mockk<WebClientWrapper>()
    private val messageScheduler = MessageScheduler(
        tradeableStockService = TradeableStockService(tradeableStockRepository),
        envConfig = EnvConfig(0, "uri", "uri"),
        webClient = webClientWrapper
    )

    @BeforeEach
    fun setUp() {
        tradeableStockRepository.deleteAll().block()
    }

    @AfterEach
    fun tearDown() {
        tradeableStockRepository.deleteAll().block()
    }

    @Test
    fun `should find the best price for scheduler`() {
        val stock = BuyableStockBuilder(
            key = "abc 2021-02-01T16:00:00:123Z",
            symbol = "AXISBANK",
            averagePrice = BigDecimal(120),
            calculatedOn = 0,
            LongName = "Axis Bank Ltd",
            Price = BigDecimal(110)
        ).build()

        val expected = mapOf(
            "blocks" to listOf(
                mapOf(
                    "type" to "header",
                    "text" to mapOf(
                        "type" to "plain_text",
                        "text" to "${stock.symbol} - ${stock.LongName}",
                        "emoji" to true
                    )
                ),
                mapOf(
                    "type" to "section",
                    "fields" to listOf(
                        mapOf("type" to "mrkdwn", "text" to "*Average Price*\nRs. ${stock.averagePrice}"),
                        mapOf("type" to "mrkdwn", "text" to "*Current Price*\nRs. ${stock.Price}")
                    )
                )
            )
        )

        tradeableStockRepository.saveAll(listOf(stock)).toMono().block()

        every {
            webClientWrapper.post(
                baseUrl = any(),
                path = any(),
                body = any(),
                returnType = any<Class<*>>(),
            )
        } returns Mono.just("ok")

        messageScheduler.start()

        val bodySlot = slot<Map<String, Any>>()

        assertSoftly {
            verify(exactly = 1) {
                webClientWrapper.post(
                    baseUrl = "uri",
                    path = "",
                    body = capture(bodySlot),
                    returnType = String::class.java
                )
            }

            val buyableStocks = tradeableStockRepository.findAll().toIterable().toList()
            buyableStocks shouldHaveSize 1
            buyableStocks shouldContainAll listOf(
                stock.copy(isSendAlert = true)
            )
            bodySlot.captured shouldBe expected
        }

    }
}
