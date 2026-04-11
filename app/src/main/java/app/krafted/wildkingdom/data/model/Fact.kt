package app.krafted.wildkingdom.data.model

import androidx.annotation.Keep

@Keep
data class Fact(
    val id: Int,
    val title: String,
    val category: String,
    val body: String
)
