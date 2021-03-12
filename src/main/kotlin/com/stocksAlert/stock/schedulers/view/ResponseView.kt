package com.stocksAlert.stock.schedulers.view

import com.stocksAlert.stock.domain.Stock
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

data class ResponseView(
    val LongName: String,
    val UlaValue: BigDecimal,
    val LastTrdTime: Long,
    val ATP: BigDecimal,
    val PercentChange: Double,
    val ScripName: String,
    val Price: BigDecimal,
    val Change: BigDecimal,
    val Volume: Double,
    val TurnOver: Double,
    val Open: BigDecimal,
    val High: BigDecimal,
    val Low: BigDecimal,
    val PreCloseRate: BigDecimal,
    val OI: BigDecimal,
    val upperCircuit: Double,
    val lowerCircuit: Double,
    val Wk52High: BigDecimal,
    val W2AvgQ: BigDecimal,
    val Wk52low: BigDecimal,
    val MCapFF: BigDecimal,
    val MCapFull: BigDecimal,
) {
    fun toStock(): Stock {
        val time = LocalDateTime.ofEpochSecond(LastTrdTime / 1000, 0, ZoneOffset.of("+05:30"))
        val key = "$ScripName $time:00:000Z"
        return Stock(
            key = key,
            symbol = ScripName,
            LastTrdTime = LastTrdTime,
            LongName = LongName,
            UlaValue = UlaValue,
            ATP = ATP,
            PercentChange = PercentChange,
            Price = Price,
            Change = Change,
            Volume = Volume,
            TurnOver = TurnOver,
            Open = Open,
            High = High,
            Low = Low,
            PreCloseRate = PreCloseRate,
            OI = OI,
            upperCircuit = upperCircuit,
            lowerCircuit = lowerCircuit,
            Wk52High = Wk52High,
            W2AvgQ = W2AvgQ,
            Wk52low = Wk52low,
            MCapFF = MCapFF,
            MCapFull = MCapFull
        )
    }
}


