package com.stocksAlert.stock.service

import com.stocksAlert.stock.domain.Stock
import com.stocksAlert.stock.domain.calculateAveragePrice
import com.stocksAlert.stock.repository.StockRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.LocalDateTime

@Service
class StockService(private val stockRepository: StockRepository) {

    fun getAllBySymbol(symbol: String): Flux<Stock> {
        val now = LocalDateTime.now().toString().split("T")[0]
        return stockRepository.findLastBySymbol(symbol, ".*T16:00:00.*", ".*${now}T.*")
    }

    fun findAverage(symbol: String): Mono<BigDecimal> {
        return getAllBySymbol(symbol).collectList()
            .map {
                it.calculateAveragePrice()
            }
    }

    fun getAll(): Flux<Stock> {
        return stockRepository.findAll()
    }

    fun delete(stocks: Stock): Mono<Void> {
        return stockRepository.delete(stocks)
    }

    fun saveAll(stocks: List<Stock>): Flux<Stock> {
        return stockRepository.saveAll(stocks)
    }
}
