package com.stocksAlert.stock.utils

import com.stocksAlert.stock.schedulers.view.ResponseView
import org.springframework.stereotype.Component
import java.text.SimpleDateFormat
import java.util.*

@Component
class StringParser {

    companion object {
        fun parse(string: String): ResponseView {
            val stringWithoutHyphen = string.replace("--", "0", true)
            val map = linkedMapOf<String, String>()
            stringWithoutHyphen.removeSurrounding("{", "}")
                .split(",")
                .map {
                    it.split("=").apply {
                        map[this[0].trim()] = this[1].trim()
                    }
                }

            val format = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.US)

            return ResponseView(
                LongName = map["LongName"]!!,
                UlaValue = map["UlaValue"]!!.toDouble(),
                LastTrdTime = format.parse(map["LastTrdTime"]).time,
                ATP = map["ATP"]!!.toDouble(),
                PercentChange = map["PercentChange"]!!.toDouble(),
                ScripName = map["ScripName"]!!,
                Price = map["Price"]!!.toDouble(),
                Change = map["Change"]!!.toDouble(),
                Volume = map["Volume"]!!.toDouble(),
                TurnOver = map["TurnOver"]!!.toDouble(),
                Open = map["Open"]!!.toDouble(),
                High = map["High"]!!.toDouble(),
                Low = map["Low"]!!.toDouble(),
                PreCloseRate = map["PreCloseRate"]!!.toDouble(),
                OI = map["OI"]!!.toDouble(),
                upperCircuit = map["upperCircuit"]!!.toDouble(),
                lowerCircuit = map["lowerCircuit"]!!.toDouble(),
                Wk52High = map["Wk52High"]!!.toDouble(),
                W2AvgQ = map["W2AvgQ"]!!.toDouble(),
                Wk52low = map["Wk52low"]!!.toDouble(),
                MCapFF = map["MCapFF"]!!.toDouble(),
                MCapFull = map["MCapFull"]!!.toDouble(),
            )
        }
    }
}
