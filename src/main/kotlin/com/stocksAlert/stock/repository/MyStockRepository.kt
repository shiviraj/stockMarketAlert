package com.stocksAlert.stock.repository

import com.stocksAlert.stock.domain.MyStock
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface MyStockRepository : ReactiveCrudRepository<MyStock, String>
