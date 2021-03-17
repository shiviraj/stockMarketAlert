package com.stocksAlert.stock.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

const val TRADEABLE_STOCK_COLLECTION = "tradeableStocks"

@Document(TRADEABLE_STOCK_COLLECTION)
data class TradeableStock(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val key: String,
    @Field("Symbol")
    val symbol: String,
    val averagePrice: BigDecimal,
    val calculatedOn: Long = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+05:30")) * 1000,
    var isSendAlert: Boolean = false,
    val LongName: String,
    val Price: BigDecimal,
    val Type: String = "BUY"
) {
}
