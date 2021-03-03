package com.stocksAlert.stock.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

const val SYMBOL_COLLECTION = "symbols"

@Document(SYMBOL_COLLECTION)
data class Symbol(
    @Id
    var id: ObjectId? = null,
    val name: String
)
