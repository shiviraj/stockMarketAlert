package com.stocksAlert.stock.service

import com.stocksAlert.stock.controller.viewModel.MyStockRequest
import com.stocksAlert.stock.domain.MyStock
import com.stocksAlert.stock.repository.MyStockRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.ZonedDateTime

@Service
class MyStockService(
    private val myStockRepository: MyStockRepository
) {
    fun add(myStockRequest: MyStockRequest): Mono<MyStock> {
        val myStock = MyStock(
            symbol = myStockRequest.symbol,
            purchasedOn = ZonedDateTime.parse(myStockRequest.purchasedOn).toEpochSecond() * 1000,
            LongName = myStockRequest.LongName,
            costPrice = myStockRequest.costPrice,
            qty = myStockRequest.qty,
            alertOnGainPercentage = myStockRequest.alertOnGainPercentage,
            alertOnLossPercentage = myStockRequest.alertOnLossPercentage
        )
        return myStockRepository.save(myStock)
    }
}
