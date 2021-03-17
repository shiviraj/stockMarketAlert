package com.stocksAlert.stock.service

import com.stocksAlert.stock.domain.Symbol
import com.stocksAlert.stock.repository.SymbolRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class SymbolService(@Autowired val symbolRepository: SymbolRepository) {
    fun getAllSymbols(): Flux<Symbol> {
        return symbolRepository.findAll()
    }

    fun save(symbol: Symbol): Mono<Symbol> {
        return symbolRepository.save(symbol)
    }

    fun deleteAll(list: List<Symbol>): Mono<Void> {
        return symbolRepository.deleteAll(list)
    }

    fun saveAll(symbols: List<Symbol>): Flux<Symbol> {
        return symbolRepository.saveAll(symbols)
    }
}
