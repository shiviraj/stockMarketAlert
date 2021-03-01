package com.stocksAlert.stock.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.math.BigDecimal

const val MY_STOCK_COLLECTION = "myStocks"

@Document(MY_STOCK_COLLECTION)
class MyStock(
    @Id
    var id: ObjectId? = null,
    @Field("Symbol")
    val symbol: String,
    val purchasedOn: Long,
    val LongName: String,
    val costPrice: BigDecimal,
    val qty: Int,
    val alertOnGainPercentage: Double,
    val alertOnLossPercentage: Double
)
