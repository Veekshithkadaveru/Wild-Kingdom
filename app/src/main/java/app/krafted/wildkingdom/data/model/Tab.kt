package app.krafted.wildkingdom.data.model

import androidx.annotation.Keep

@Keep
data class Tab(
    val id: String,
    val label: String,
    val cards: List<Fact>
)
