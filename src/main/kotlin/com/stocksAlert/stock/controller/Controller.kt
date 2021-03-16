package com.stocksAlert.stock.controller

import com.stocksAlert.stock.controller.viewModel.MyStockRequest
import com.stocksAlert.stock.domain.MyStock
import com.stocksAlert.stock.schedulers.RemoveUnnecessaryStocksScheduler
import com.stocksAlert.stock.service.MyStockService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@Component
class Controller(
    @Autowired val myStockService: MyStockService,
    @Autowired val removeUnnecessaryStocksScheduler: RemoveUnnecessaryStocksScheduler
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
}
