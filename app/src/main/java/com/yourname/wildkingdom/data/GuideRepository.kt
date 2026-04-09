package com.yourname.wildkingdom.data

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.yourname.wildkingdom.data.model.Chapter
import com.yourname.wildkingdom.data.model.Tip
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Keep
internal data class Guide(
    @SerializedName("chapters") val chapters: List<Chapter>
)

class GuideRepository(private val context: Context) {

    private var _chapters: List<Chapter> = emptyList()
    private val mutex = Mutex()
    private var loaded = false

    suspend fun loadChapters(): List<Chapter> {
        if (loaded) return _chapters
        mutex.withLock {
            if (loaded) return _chapters
            _chapters = withContext(Dispatchers.IO) {
                try {
                    val json = context.assets.open("guide.json").bufferedReader().use { it.readText() }
                    Gson().fromJson(json, Guide::class.java).chapters
                } catch (e: Exception) {
                    Log.e("GuideRepository", "Failed to parse guide.json", e)
                    emptyList()
                }
            }
            loaded = true
        }
        return _chapters
    }

    fun getChapters(): List<Chapter> = _chapters

    fun getChapter(id: String): Chapter? = _chapters.find { it.id == id }

    fun getAllTips(): List<Tip> = _chapters.flatMap { it.tips }
}
