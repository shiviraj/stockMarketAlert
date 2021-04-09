package com.stocksAlert.stock.controller

import com.stocksAlert.stock.controller.viewModel.MyStockRequest
import com.stocksAlert.stock.domain.MyStock
import com.stocksAlert.stock.schedulers.BestTradeableStockScheduler
import com.stocksAlert.stock.schedulers.MessageScheduler
import com.stocksAlert.stock.schedulers.RemoveUnnecessaryStocksScheduler
import com.stocksAlert.stock.schedulers.StockFetcherScheduler
import com.stocksAlert.stock.service.MyStockService
import com.stocksAlert.stock.service.TradeableStockService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@Component
class Controller(
    @Autowired val myStockService: MyStockService,
    @Autowired val removeUnnecessaryStocksScheduler: RemoveUnnecessaryStocksScheduler,
    @Autowired val bestTradeableStockScheduler: BestTradeableStockScheduler,
    @Autowired val messageScheduler: MessageScheduler,
    @Autowired val stockFetcherScheduler: StockFetcherScheduler,
    @Autowired val tradeableStockService: TradeableStockService
) {

    @GetMapping("/")
    fun index(): String {
        return "OK"
    }

    @PostMapping("/my-stock")
    fun addMyStock(@RequestBody myStock: MyStockRequest): Mono<MyStock> {
        return myStockService.add(myStock)
    }

    @GetMapping("/remove")
    fun remove(): Mono<String> {
        removeUnnecessaryStocksScheduler.start()
        return Mono.just("removing")
    }

    @GetMapping("/tradeable-stocks")
    fun findTradeableStocks(): Mono<String> {
        bestTradeableStockScheduler.start()
        return Mono.just("best tradeable stocks")
    }

    @GetMapping("/message-sender")
    fun messageSender(): Mono<String> {
        messageScheduler.start()
        return Mono.just("message scheduler")
    }

    @GetMapping("/stock-fetcher")
    fun stockFetcher(): Mono<String> {
        stockFetcherScheduler.start()
        return Mono.just("stock fetcher")
    }

    @GetMapping("/tradeable-stocks/{date}")
    fun tradeableStocks(@PathVariable date: String): Mono<Map<String, List<String>>> {
        return tradeableStockService.getAllStocks(date)
    }
}
