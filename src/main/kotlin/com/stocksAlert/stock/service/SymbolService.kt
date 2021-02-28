package com.stocksAlert.stock.service

import com.stocksAlert.stock.domain.Symbol
import com.stocksAlert.stock.repository.SymbolRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class SymbolService(@Autowired val symbolRepository: SymbolRepository) {
    fun getAllSymbols(): Flux<Symbol> {
        return symbolRepository.findAll()
    }
}
