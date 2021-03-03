package com.stocksAlert.stock.schedulers.builder

import com.stocksAlert.stock.domain.Symbol
import org.bson.types.ObjectId

data class SymbolBuilder(
val id: ObjectId? = null,
val name: String = ""
) {
fun build(): Symbol {
return Symbol(
id = id,
name = name
)
}
}
