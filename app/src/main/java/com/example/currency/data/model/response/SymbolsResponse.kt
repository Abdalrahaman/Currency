package com.example.currency.data.model.response

data class SymbolsResponse(
    val success: Boolean,
    val symbols: Map<String, String>,
    val error: ErrorResponse?
)