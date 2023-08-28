package com.example.currency.data.model.response

data class LatestRatesResponse(
    val base: String?,
    val date: String?,
    val rates: Map<String, Double>?,
    val success: Boolean,
    val timestamp: Int?,
    val error: ErrorResponse?
)