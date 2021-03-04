package com.stocksAlert.stock.repository

import com.stocksAlert.stock.domain.Stock
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
interface StockRepository : ReactiveCrudRepository<Stock, String> {
   @Query(value = "{'\$and': [{Symbol: ?0}, {'\$or': [{key:{ '\$regex' : ?1 }}, {key:{ '\$regex' : ?2 }}]}]}")
    fun findLastBySymbol(symbol: String, regex: String, today: String): Flux<Stock>
}
