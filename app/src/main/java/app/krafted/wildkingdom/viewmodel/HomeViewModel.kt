package app.krafted.wildkingdom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.wildkingdom.data.AnimalRepository
import app.krafted.wildkingdom.data.model.Animal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val animals: List<Animal>) : HomeUiState()
    object Error : HomeUiState()
}

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AnimalRepository.getInstance(application)

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val animals = repository.loadAnimals()
            _uiState.value = if (animals.isEmpty()) HomeUiState.Error else HomeUiState.Success(animals)
        }
    }
}
