package com.example.currency.data.model.response

data class HistoricalResponse(
    val base: String?,
    val date: String?,
    val historical: Boolean?,
    val rates: Map<String, Double>?,
    val success: Boolean,
    val timestamp: Int?,
    val error: ErrorResponse?
)