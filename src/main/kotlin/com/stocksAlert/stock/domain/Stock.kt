package com.stocksAlert.stock.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.math.BigDecimal

const val STOCK_COLLECTION = "stocks"

@Document(STOCK_COLLECTION)
data class Stock(
        @Id
        var id: ObjectId? = null,
        @Indexed(unique = true)
        val key: String,
        @Field("Symbol")
        val symbol: String,
        val LastTrdTime: Long,
        val LongName: String,
        val UlaValue: BigDecimal,
        val ATP: BigDecimal,
        val PercentChange: Double,
        val Price: BigDecimal,
        val Change: BigDecimal,
        val Volume: Long,
        val TurnOver: Long,
        val Open: BigDecimal,
        val High: BigDecimal,
        val Low: BigDecimal,
        val PreCloseRate: BigDecimal,
        val OI: BigDecimal,
        val upperCircuit: Long,
        val lowerCircuit: Long,
        val Wk52High: BigDecimal,
        val W2AvgQ: BigDecimal,
        val Wk52low: BigDecimal,
        val MCapFF: BigDecimal,
        val MCapFull: BigDecimal
)

fun List<Stock>.calculateAveragePrice(): BigDecimal {
    val sum = this.map {
        ((it.High + it.Low) / BigDecimal(2))
    }.sumOf { it }
    return sum / BigDecimal(this.size)
}

