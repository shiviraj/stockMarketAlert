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

    fun saveAll(tradeableStocks: List<TradeableStock>): Flux<TradeableStock> {
        return tradeableStockRepository.saveAll(tradeableStocks)
    }

    fun getAllStocks(date: String): Mono<Map<String, List<String>>> {
        return tradeableStockRepository.getStocks(".*$date")
            .collectList()
            .map {
                it
                    .groupBy { tradeableStock ->
                        tradeableStock.Type
                    }
            }
            .map { entry ->
                val mutableMapOf = mutableMapOf<String, List<String>>()
                entry.keys.forEach {
                    mutableMapOf[it] = entry[it]?.map { tradeableStock ->
                        tradeableStock.key
                    }!!
                }
                mutableMapOf
            }
    }
}
