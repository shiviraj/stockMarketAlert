package com.stocksAlert.stock.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller {
    @GetMapping("/")
    fun index(): String {
        return "OK"
    }
}
