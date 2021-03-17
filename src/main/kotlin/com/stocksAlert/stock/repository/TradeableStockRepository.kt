package com.stocksAlert.stock.repository

import com.stocksAlert.stock.domain.TradeableStock
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
interface TradeableStockRepository : ReactiveCrudRepository<TradeableStock, String> {
    fun findAllByIsSendAlert(isSendAlert: Boolean): Flux<TradeableStock>
}
