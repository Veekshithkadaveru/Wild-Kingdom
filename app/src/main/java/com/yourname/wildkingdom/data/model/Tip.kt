package com.yourname.wildkingdom.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class Tip(
    val id: Int,
    @SerializedName("phase") val phase: Phase,
    @SerializedName("severity") val severity: Severity,
    val title: String,
    val body: String
)
