package com.stocksAlert.stock.utils

import com.stocksAlert.stock.schedulers.view.ResponseView
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@Component
class StringParser {

    companion object {
        fun parse(string: String): ResponseView {
            val map = linkedMapOf<String, String>()
            string.removeSurrounding("{", "}")
                .split(",")
                .map {
                    it.split("=").apply {
                        map[this[0].trim()] = this[1].trim()
                    }
                }

            val format = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US)

            return ResponseView(
                LongName = map["LongName"]!!,
                UlaValue = map["UlaValue"]!!.toBigDecimal(),
                LastTrdTime = format.parse(map["LastTrdTime"]).time,
                ATP = map["ATP"]!!.toBigDecimal(),
                PercentChange = map["PercentChange"]!!.toDouble(),
                ScripName = map["ScripName"]!!,
                Price = map["Price"]!!.toBigDecimal(),
                Change = map["Change"]!!.toBigDecimal(),
                Volume = map["Volume"]!!.toDouble(),
                TurnOver = map["TurnOver"]!!.toDouble(),
                Open = map["Open"]!!.toBigDecimal(),
                High = map["High"]!!.toBigDecimal(),
                Low = map["Low"]!!.toBigDecimal(),
                PreCloseRate = map["PreCloseRate"]!!.toBigDecimal(),
                OI = map["OI"]!!.toBigDecimal(),
                upperCircuit = map["upperCircuit"]!!.toDouble(),
                lowerCircuit = map["lowerCircuit"]!!.toDouble(),
                Wk52High = map["Wk52High"]!!.toBigDecimal(),
                W2AvgQ = map["W2AvgQ"]!!.toBigDecimal(),
                Wk52low = map["Wk52low"]!!.toBigDecimal(),
                MCapFF = map["MCapFF"]!!.toBigDecimal(),
                MCapFull = map["MCapFull"]!!.toBigDecimal(),
            )
        }
    }
}
