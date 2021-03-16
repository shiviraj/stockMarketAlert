package com.stocksAlert.stock.schedulers.view

import java.math.BigDecimal

class StockEvaluation(val stockGrow: Grow, private val priceChange: BigDecimal, val price: BigDecimal) {
    override fun toString(): String {
        return "StockEvaluation(stockGrowResult=$stockGrow, abs=$priceChange, averagePrice=$price)"
    }
}

enum class Grow {
    UP,
    DOWN
}
