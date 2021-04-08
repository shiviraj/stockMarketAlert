package com.stocksAlert.stock.schedulers.view

class StockEvaluation(val stockGrow: Grow, private val priceChange: Double, val price: Double) {
    override fun toString(): String {
        return "StockEvaluation(stockGrowResult=$stockGrow, abs=$priceChange, averagePrice=$price)"
    }
}

enum class Grow {
    UP,
    DOWN
}
