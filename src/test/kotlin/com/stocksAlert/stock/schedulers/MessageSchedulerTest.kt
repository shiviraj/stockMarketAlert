package com.stocksAlert.stock.schedulers

import com.stocksAlert.stock.config.EnvConfig
import com.stocksAlert.stock.repository.BuyableStockRepository
import com.stocksAlert.stock.schedulers.builder.BuyableStockBuilder
import com.stocksAlert.stock.service.BuyableStockService
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
import java.math.BigDecimal

@SpringBootTest
class MessageSchedulerTest(
    @Autowired private val buyableStockRepository: BuyableStockRepository
) {
    private val webClientWrapper = mockk<WebClientWrapper>()
    private val messageScheduler = MessageScheduler(
        buyableStockService = BuyableStockService(buyableStockRepository),
        envConfig = EnvConfig(0, "uri"),
        webClient = webClientWrapper
    )

    @BeforeEach
    fun setUp() {
        buyableStockRepository.deleteAll().block()
    }

    @AfterEach
    fun tearDown() {
        buyableStockRepository.deleteAll().block()
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
                mapOf("type" to "header",
                    "text" to mapOf("type" to "plain_text",
                        "text" to "${stock.symbol} - ${stock.LongName}",
                        "emoji" to true
                    )
                ),
                mapOf("type" to "section",
                    "fields" to listOf(mapOf("type" to "mrkdwn", "text" to "*Average Price*\nRs. ${stock.averagePrice}"),
                        mapOf("type" to "mrkdwn", "text" to "*Current Price*\nRs. ${stock.Price}")
                    )
                )
            )
        )

        buyableStockRepository.saveAll(listOf(stock)).blockLast()

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


            val buyableStocks = buyableStockRepository.findAll().toIterable().toList()
            buyableStocks shouldHaveSize 1
            buyableStocks shouldContainAll listOf(
                stock.copy(isSendAlert = true)
            )
            bodySlot.captured shouldBe expected
        }

    }
}
