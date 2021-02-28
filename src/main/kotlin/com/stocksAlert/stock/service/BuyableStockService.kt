package com.stocksAlert.stock.service

import com.stocksAlert.stock.domain.BuyableStock
import com.stocksAlert.stock.repository.BuyableStockRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class BuyableStockService(private val buyableStockRepository: BuyableStockRepository) {

    fun save(buyableStock: BuyableStock): Mono<BuyableStock> {
        return buyableStockRepository.save(buyableStock)
    }

    fun getAll(): Flux<BuyableStock> {
        return buyableStockRepository.findAll()
    }
}
