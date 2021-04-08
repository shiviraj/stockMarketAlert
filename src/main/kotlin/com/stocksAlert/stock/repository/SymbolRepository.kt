package com.stocksAlert.stock.repository

import com.stocksAlert.stock.domain.Symbol
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface SymbolRepository : ReactiveCrudRepository<Symbol, String>
