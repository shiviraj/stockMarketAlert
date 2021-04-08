package com.stocksAlert.stock.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

const val STOCK_COLLECTION = "stocks"

@Document(STOCK_COLLECTION)
data class Stock(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    var key: String,
    @Field("Symbol")
    val symbol: String,
    var LastTrdTime: Long,
    val LongName: String,
    val UlaValue: Double,
    val ATP: Double,
    val PercentChange: Double,
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
    val MCapFull: Double
) {
    fun averagePrice(): Double {
        return (High + Low) / 2
    }
}
