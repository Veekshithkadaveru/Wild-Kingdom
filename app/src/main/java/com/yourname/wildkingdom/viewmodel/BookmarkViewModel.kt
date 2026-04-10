package com.yourname.wildkingdom.viewmodel

import android.app.Application
import androidx.annotation.Keep
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourname.wildkingdom.data.AnimalRepository
import com.yourname.wildkingdom.data.db.AppDatabase
import com.yourname.wildkingdom.data.model.Animal
import com.yourname.wildkingdom.data.model.Fact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Keep
data class BookmarkedAnimal(
    val id: String,
    val name: String,
    val accentColor: String,
    val symbol: String,
    val facts: List<Fact>
)

class BookmarkViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AnimalRepository.getInstance(application)
    private val bookmarkDao = AppDatabase.getInstance(application).bookmarkDao()

    private var animalsById: Map<String, Animal> = emptyMap()
    private var allFactsById: Map<Int, Fact> = emptyMap()

    private val _dataReady = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            repository.loadAnimals()
            animalsById = repository.getAnimals().associateBy { it.id }
            allFactsById = repository.getAllFacts().associateBy { it.id }
            _dataReady.value = true
        }
    }

    val bookmarkedAnimals: StateFlow<List<BookmarkedAnimal>> =
        combine(
            bookmarkDao.getAllBookmarks(),
            _dataReady
        ) { bookmarks, ready ->
            if (!ready) return@combine emptyList()
            bookmarks
                .groupBy { it.chapterId }
                .mapNotNull { (animalId, entities) ->
                    val animal = animalsById[animalId] ?: return@mapNotNull null
                    val facts = entities.mapNotNull { entity -> allFactsById[entity.tipId] }
                    if (facts.isEmpty()) return@mapNotNull null
                    BookmarkedAnimal(
                        id = animal.id,
                        name = animal.name,
                        accentColor = animal.accentColor,
                        symbol = animal.symbol,
                        facts = facts
                    )
                }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun removeBookmark(factId: Int) {
        viewModelScope.launch {
            bookmarkDao.removeBookmark(factId)
        }
    }
}
