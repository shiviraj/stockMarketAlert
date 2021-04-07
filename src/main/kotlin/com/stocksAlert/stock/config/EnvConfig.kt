package com.stocksAlert.stock.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("stock.env")
@ConstructorBinding
data class EnvConfig(val discountPercent: Int, val webhookUri: String, val bseUri: String)
