package com.stocksAlert.stock.controller.viewModel

import java.math.BigDecimal

data class MyStockRequest(
    val symbol: String,
    val purchasedOn: String,
    val LongName: String,
    val costPrice: BigDecimal,
    val qty: Int = 1,
    val alertOnGainPercentage: Double = 10.0,
    val alertOnLossPercentage: Double = 5.0
)
