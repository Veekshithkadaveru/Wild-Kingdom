package com.yourname.wildkingdom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourname.wildkingdom.data.AnimalRepository
import com.yourname.wildkingdom.data.db.AppDatabase
import com.yourname.wildkingdom.data.db.BookmarkEntity
import com.yourname.wildkingdom.data.model.Animal
import com.yourname.wildkingdom.data.model.Fact
import com.yourname.wildkingdom.data.model.Tab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChapterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AnimalRepository(application)
    private val bookmarkDao = AppDatabase.getInstance(application).bookmarkDao()

    private val _animalId = MutableStateFlow("")
    private val _activeTabId = MutableStateFlow("FACTS")
    val activeTabId: StateFlow<String> = _activeTabId.asStateFlow()

    private val _highlightFactId = MutableStateFlow<Int?>(null)
    val highlightFactId: StateFlow<Int?> = _highlightFactId.asStateFlow()

    private var _animalData: Animal? = null
    val animal: StateFlow<Animal?> get() = _animalState
    private val _animalState = MutableStateFlow<Animal?>(null)

    val tabs: StateFlow<List<Tab>> = _animalState
        .map { it?.tabs ?: emptyList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredFacts: StateFlow<List<Fact>> = combine(
        _animalState,
        _activeTabId
    ) { animal, tabId ->
        animal?.tabs?.find { it.id == tabId }?.cards ?: emptyList()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bookmarkedFactIds: StateFlow<Set<Int>> = bookmarkDao.getBookmarkedIds()
        .combine(MutableStateFlow(Unit)) { ids, _ -> ids.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    fun loadAnimal(animalId: String, highlightFactId: Int? = null) {
        _highlightFactId.value = highlightFactId
        if (_animalId.value == animalId && _animalState.value != null) {
            if (highlightFactId != null) {
                val tabId =
                    _animalData?.tabs?.find { tab -> tab.cards.any { it.id == highlightFactId } }?.id
                if (tabId != null) _activeTabId.value = tabId
            }
            return
        }
        _animalId.value = animalId
        viewModelScope.launch {
            repository.loadAnimals()
            _animalData = repository.getAnimal(animalId)
            _animalState.value = _animalData
            if (highlightFactId != null) {
                val tabId =
                    _animalData?.tabs?.find { tab -> tab.cards.any { it.id == highlightFactId } }?.id
                if (tabId != null) _activeTabId.value = tabId
            }
        }
    }

    fun clearHighlight() {
        _highlightFactId.value = null
    }

    fun setActiveTab(tabId: String) {
        _activeTabId.value = tabId
    }

    fun toggleBookmark(fact: Fact) {
        viewModelScope.launch {
            val animalId = _animalId.value
            if (bookmarkedFactIds.value.contains(fact.id)) {
                bookmarkDao.removeBookmark(fact.id)
            } else {
                bookmarkDao.addBookmark(
                    BookmarkEntity(
                        tipId = fact.id,
                        chapterId = animalId,
                        tipTitle = fact.title
                    )
                )
            }
        }
    }
}
