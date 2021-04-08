package com.stocksAlert.stock.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConfigurationProperties("stock.env")
@ConstructorBinding
data class EnvConfig(
    val webhookUriBuy: String,
    val bseUri: String,
    val webhookUriSell: String,
    val webhookUriAlert: String
)
