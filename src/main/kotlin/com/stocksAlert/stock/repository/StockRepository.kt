package com.stocksAlert.stock.repository

import com.stocksAlert.stock.domain.Stock
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface StockRepository : ReactiveCrudRepository<Stock, String> {
    @Query(value = "{'\$and': [{Symbol: ?0}, {'\$or': [{key:{ '\$regex' : ?1 }}, {key:{ '\$regex' : ?2 }}]}]}")
    fun findLastBySymbol(symbol: String, regex: String, today: String): Flux<Stock>

    @Query(value = "{key: {\$regex: ?0}}")
    fun findByRegexKey(query: String): Flux<Stock>

    @Query(value = "{LastTrdTime:{\$lt: ?0 }}")
    fun getOlder(toEpochSecond: Long): Flux<Stock>
}
