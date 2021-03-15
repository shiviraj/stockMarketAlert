package com.stocksAlert.stock.schedulers

import com.stocksAlert.stock.config.EnvConfig
import com.stocksAlert.stock.schedulers.view.ResponseView
import com.stocksAlert.stock.service.StockService
import com.stocksAlert.stock.utils.StringParser
import com.stocksAlert.stock.utils.WebClientWrapper
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Component
class StockFetcherScheduler(
    @Autowired
    private val webClientWrapper: WebClientWrapper,
    @Autowired
    private val envConfig: EnvConfig,
    @Autowired
    private val stockService: StockService,
) {
    private var pageNo: Int = 1

    @Scheduled(cron = "0 * 3-10 * * 1-5")
    @SchedulerLock(name = "UpdateOldRecordsScheduler_start", lockAtMostFor = "1m", lockAtLeastFor = "1m")
    fun start() {
        fetchStock()
            .map {
                it.map { responseView ->
                    responseView.toStock()
                }
            }
            .map {
                println("saved stock ${LocalDateTime.now()}, $pageNo")
                stockService.saveAll(it).subscribe()
            }
            .subscribe()
    }

    private fun fetchStock(): Mono<List<ResponseView>> {
        val linkedMultiValueMap = LinkedMultiValueMap<String, String>()
        linkedMultiValueMap.add("flag", "Equity")
        linkedMultiValueMap.add("ddlVal1", "All");
        linkedMultiValueMap.add("ddlVal2", "All")
        linkedMultiValueMap.add("m", "0")
        linkedMultiValueMap.add("pgN", "${pageNo++}")

        return webClientWrapper.get(
            baseUrl = envConfig.bseUri,
            path = "",
            returnType = List::class.java,
            queryParams = linkedMultiValueMap,
            headers = mapOf(
                "User-Agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:85.0) Gecko/20100101 Firefox/85.0",
            ),
        ).map {
            if (it.isEmpty())
                pageNo = 1

            it.map { str ->
                StringParser.parse(str.toString())
            }
        }
    }
}
