package com.stocksAlert.stock.schedulers.view

import com.stocksAlert.stock.domain.Stock
import java.time.LocalDateTime
import java.time.ZoneOffset

data class ResponseView(
    val LongName: String,
    val UlaValue: Double,
    val LastTrdTime: Long,
    val ATP: Double,
    val PercentChange: Double,
    val ScripName: String,
    val Price: Double,
    val Change: Double,
    val Volume: Double,
    val TurnOver: Double,
    val Open: Double,
    val High: Double,
    val Low: Double,
    val PreCloseRate: Double,
    val OI: Double,
    val upperCircuit: Double,
    val lowerCircuit: Double,
    val Wk52High: Double,
    val W2AvgQ: Double,
    val Wk52low: Double,
    val MCapFF: Double,
    val MCapFull: Double,
) {
    fun toStock(): Stock {
        val time = LocalDateTime.ofEpochSecond(LastTrdTime / 1000, 0, ZoneOffset.of("+05:30"))
        val key = "$ScripName $time:00.000"
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


