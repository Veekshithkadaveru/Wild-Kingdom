package com.yourname.wildkingdom.data

import android.content.Context
import android.util.Log
import androidx.annotation.Keep
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.yourname.wildkingdom.data.model.Animal
import com.yourname.wildkingdom.data.model.Fact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

@Keep
internal data class AnimalsJson(
    @SerializedName("animals") val animals: List<Animal>
)

class AnimalRepository(private val context: Context) {

    private var _animals: List<Animal> = emptyList()
    private val mutex = Mutex()
    private var loaded = false

    suspend fun loadAnimals(): List<Animal> {
        if (loaded) return _animals
        mutex.withLock {
            if (loaded) return _animals
            _animals = withContext(Dispatchers.IO) {
                try {
                    val json =
                        context.assets.open("animals.json").bufferedReader().use { it.readText() }
                    Gson().fromJson(json, AnimalsJson::class.java).animals
                } catch (e: Exception) {
                    Log.e("AnimalRepository", "Failed to parse animals.json", e)
                    emptyList()
                }
            }
            loaded = true
        }
        return _animals
    }

    fun getAnimals(): List<Animal> = _animals

    fun getAnimal(id: String): Animal? = _animals.find { it.id == id }

    fun getAllFacts(): List<Fact> = _animals.flatMap { animal ->
        animal.tabs.flatMap { tab -> tab.cards }
    }
}
