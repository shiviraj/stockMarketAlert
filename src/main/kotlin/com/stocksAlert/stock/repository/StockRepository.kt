package com.stocksAlert.stock.repository

import com.stocksAlert.stock.domain.Stock
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
interface StockRepository : ReactiveCrudRepository<Stock, String> {
    fun findAllBySymbol(symbol: String): Flux<Stock>
}
