package com.stocksAlert.stock.schedulers.builder

import com.stocksAlert.stock.domain.TradeableStock
import org.bson.types.ObjectId
import java.math.BigDecimal

data class BuyableStockBuilder(
    val id: ObjectId? = null,
    val key: String = "",
    val symbol: String = "",
    val averagePrice: BigDecimal = BigDecimal.ZERO,
    val calculatedOn: Long = 0,
    val isSendAlert: Boolean = false,
    val LongName: String = "",
    val Price: BigDecimal = BigDecimal.ZERO
) {
    fun build(): TradeableStock {
        return TradeableStock(
            id = id,
            key = key,
            symbol = symbol,
            averagePrice = averagePrice,
            calculatedOn = calculatedOn,
            isSendAlert = isSendAlert,
            LongName = LongName,
            Price = Price
        )
    }
}
