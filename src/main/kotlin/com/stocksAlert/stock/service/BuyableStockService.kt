package com.stocksAlert.stock.service

import com.stocksAlert.stock.domain.TradeableStock
import com.stocksAlert.stock.repository.BuyableStockRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class BuyableStockService(private val buyableStockRepository: BuyableStockRepository) {
    fun save(tradeableStock: TradeableStock): Mono<TradeableStock> {
        return buyableStockRepository.save(tradeableStock)
    }

    fun getStocksUnsentAlert(): Flux<TradeableStock> {
        return buyableStockRepository.findAllByIsSendAlert(false)
    }
}
