package com.stocksAlert.stock.schedulers.builder

import com.stocksAlert.stock.domain.Stock
import org.bson.types.ObjectId
import java.math.BigDecimal

data class StockBuilder(
    val id: ObjectId? = null,
    val key: String = "",
    val symbol: String = "",
    val LastTrdTime: Long = 0,
    val LongName: String = "",
    val UlaValue: BigDecimal = BigDecimal.ZERO,
    val ATP: BigDecimal = BigDecimal.ZERO,
    val PercentChange: Double = 0.0,
    val Price: BigDecimal = BigDecimal.ZERO,
    val Change: BigDecimal = BigDecimal.ZERO,
    val Volume: Double = 0.0,
    val TurnOver: Double = 0.0,
    val Open: BigDecimal = BigDecimal.ZERO,
    val High: BigDecimal = BigDecimal.ZERO,
    val Low: BigDecimal = BigDecimal.ZERO,
    val PreCloseRate: BigDecimal = BigDecimal.ZERO,
    val OI: BigDecimal = BigDecimal.ZERO,
    val upperCircuit: Double = 0.0,
    val lowerCircuit: Double = 0.0,
    val Wk52High: BigDecimal = BigDecimal.ZERO,
    val W2AvgQ: BigDecimal = BigDecimal.ZERO,
    val Wk52low: BigDecimal = BigDecimal.ZERO,
    val MCapFF: BigDecimal = BigDecimal.ZERO,
    val MCapFull: BigDecimal = BigDecimal.ZERO
) {
    fun build(): Stock {
        return Stock(
            id = id,
            key = key,
            symbol = symbol,
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
