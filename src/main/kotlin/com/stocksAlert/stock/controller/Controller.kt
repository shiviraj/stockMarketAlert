package com.stocksAlert.stock.controller

import com.stocksAlert.stock.controller.viewModel.MyStockRequest
import com.stocksAlert.stock.domain.MyStock
import com.stocksAlert.stock.service.MyStockService
import com.stocksAlert.stock.service.StockService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.math.BigDecimal

@RestController
@Component
class Controller(
    @Autowired val myStockService: MyStockService,
    @Autowired val stockService: StockService
) {

    @GetMapping("/")
    fun index(): String {
        return "OK"
    }

    @PostMapping("/my-stock")
    fun addMyStock(@RequestBody myStock: MyStockRequest): Mono<MyStock> {
        return myStockService.add(myStock)
    }

    @GetMapping("/average-price/{symbol}")
    fun getAveragePrice(@PathVariable symbol: String): Mono<BigDecimal> {
        return stockService.findAverage(symbol)
    }
}
