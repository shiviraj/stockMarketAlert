package com.stocksAlert.stock.service

import com.stocksAlert.stock.domain.Stock
import com.stocksAlert.stock.domain.calculateAveragePrice
import com.stocksAlert.stock.repository.StockRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal

@Service
class StockService(private val stockRepository: StockRepository) {

    fun getAllBySymbol(symbol: String): Flux<Stock> {
        return stockRepository.findAllBySymbol(symbol)
    }

    fun findAverage(symbol: String): Mono<BigDecimal> {
        return getAllBySymbol(symbol).collectList()
            .map {
                it.calculateAveragePrice()
            }
    }
}
