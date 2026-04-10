package com.yourname.wildkingdom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourname.wildkingdom.data.AnimalRepository
import com.yourname.wildkingdom.data.model.Fact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SearchResult(
    val fact: Fact,
    val animalName: String,
    val animalId: String,
    val accentColor: String,
    val symbol: String
)

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AnimalRepository(application)

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _dataReady = MutableStateFlow(false)
    private var allResults: List<SearchResult> = emptyList()

    init {
        viewModelScope.launch {
            repository.loadAnimals()
            val animals = repository.getAnimals()
            allResults = animals.flatMap { animal ->
                animal.tabs.flatMap { tab ->
                    tab.cards.map { fact ->
                        SearchResult(
                            fact = fact,
                            animalName = animal.name,
                            animalId = animal.id,
                            accentColor = animal.accentColor,
                            symbol = animal.symbol
                        )
                    }
                }
            }
            _dataReady.value = true
        }
    }

    val results: StateFlow<List<SearchResult>> =
        combine(_query, _dataReady) { q, ready ->
            if (!ready || q.isBlank()) return@combine emptyList()
            val trimmed = q.trim()
            allResults.filter { result ->
                result.fact.title.contains(trimmed, ignoreCase = true) ||
                        result.fact.body.contains(trimmed, ignoreCase = true) ||
                        result.fact.category.contains(trimmed, ignoreCase = true)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onQueryChange(newQuery: String) {
        _query.value = newQuery
    }
}
