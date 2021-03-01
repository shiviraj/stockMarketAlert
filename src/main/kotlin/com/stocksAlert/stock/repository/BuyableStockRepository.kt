package com.stocksAlert.stock.repository

import com.stocksAlert.stock.domain.BuyableStock
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Service

@Service
interface BuyableStockRepository : ReactiveCrudRepository<BuyableStock, String>
