package com.stocksAlert.stock.repository

import com.stocksAlert.stock.domain.TradeableStock
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface TradeableStockRepository : ReactiveCrudRepository<TradeableStock, String> {
    fun findAllByIsSendAlert(isSendAlert: Boolean): Flux<TradeableStock>
}
