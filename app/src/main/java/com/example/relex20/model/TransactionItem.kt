package com.example.relex20.model

data class TransactionItem(
    val tripCost: Double,
    val scannedCosts: Double,
    val manualCosts: Double,
){
    fun getTotal(): Double = tripCost + scannedCosts + manualCosts
}


