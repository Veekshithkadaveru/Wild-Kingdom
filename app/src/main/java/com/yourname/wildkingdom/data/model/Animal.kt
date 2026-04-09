package com.yourname.wildkingdom.data.model

import androidx.annotation.Keep

@Keep
data class Animal(
    val id: String,
    val name: String,
    val subtitle: String,
    val symbol: String,
    val background: String,
    val accentColor: String,
    val heroFact: String,
    val tabs: List<Tab>
)
