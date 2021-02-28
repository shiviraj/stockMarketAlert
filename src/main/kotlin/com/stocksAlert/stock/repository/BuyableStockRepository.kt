package com.stocksAlert.stock.repository

import com.stocksAlert.stock.domain.BuyableStock
import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface BuyableStockRepository : ReactiveCrudRepository<BuyableStock, String>
