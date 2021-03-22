package com.stocksAlert.stock.service

import com.stocksAlert.stock.domain.TradeableStock
import com.stocksAlert.stock.repository.TradeableStockRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class TradeableStockService(private val tradeableStockRepository: TradeableStockRepository) {
    fun save(tradeableStock: TradeableStock): Mono<TradeableStock> {
        return tradeableStockRepository.save(tradeableStock)
    }

    fun getStocksUnsentAlert(): Flux<TradeableStock> {
        return tradeableStockRepository.findAllByIsSendAlert(false)
    }

    fun deleteAll(): Mono<Void> {
        return tradeableStockRepository.deleteAll()

    }
}
