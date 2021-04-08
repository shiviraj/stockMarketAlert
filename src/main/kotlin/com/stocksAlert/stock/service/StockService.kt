package com.stocksAlert.stock.service

import com.stocksAlert.stock.domain.Stock
import com.stocksAlert.stock.repository.StockRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class StockService(private val stockRepository: StockRepository) {

    fun getAllBySymbol(symbol: String): Flux<Stock> {
        val now = LocalDateTime.now().toString().split("T")[0]
        return stockRepository.findLastBySymbol(symbol, ".*T(16:00:00|21:30:00).*", ".*${now}T.*")
    }

    fun getAll(): Flux<Stock> {
        return stockRepository.findAll()
    }

    fun saveAll(stocks: List<Stock>): Flux<Stock> {
        return stockRepository.saveAll(stocks)
    }

    fun regexQueryInKey(query: String): Flux<Stock> {
        return stockRepository.findByRegexKey(query)
    }

    fun deleteAll(stocks: List<Stock>): Mono<Void> {
        return stockRepository.deleteAll(stocks)
    }

    fun getOlderRecords(days: Long): Flux<Stock> {
        val toEpochSecond = LocalDateTime.now().minusDays(days).toEpochSecond(ZoneOffset.UTC)
        return stockRepository.getOlder(toEpochSecond)

    }

    fun save(stock: Stock): Mono<Stock> {
        return stockRepository.save(stock)
    }
}
