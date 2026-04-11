package app.krafted.wildkingdom.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.wildkingdom.data.QuizRepository
import app.krafted.wildkingdom.data.model.QuizQuestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuizState(
    val isLoading: Boolean = true,
    val questions: List<QuizQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val score: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val isAnswerSubmitted: Boolean = false,
    val isQuizFinished: Boolean = false
) {
    val currentQuestion: QuizQuestion?
        get() = questions.getOrNull(currentQuestionIndex)
}

class QuizViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QuizRepository.getInstance(application)
    private var allQuestions: List<QuizQuestion> = emptyList()

    companion object {
        private const val QUIZ_SIZE = 10
    }

    private val _uiState = MutableStateFlow(QuizState())
    val uiState: StateFlow<QuizState> = _uiState.asStateFlow()

    init {
        loadQuiz()
    }

    private fun loadQuiz() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            allQuestions = repository.loadQuiz()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                questions = allQuestions.shuffled().take(QUIZ_SIZE)
            )
        }
    }

    fun selectAnswer(index: Int) {
        if (_uiState.value.isAnswerSubmitted) return
        _uiState.value = _uiState.value.copy(selectedAnswerIndex = index)
    }

    fun submitAnswer() {
        val state = _uiState.value
        val currentQuestion = state.currentQuestion
        if (state.isAnswerSubmitted || state.selectedAnswerIndex == null || currentQuestion == null) return

        val isCorrect = state.selectedAnswerIndex == currentQuestion.correctAnswerIndex
        val newScore = if (isCorrect) state.score + 1 else state.score

        _uiState.value = state.copy(
            isAnswerSubmitted = true,
            score = newScore
        )
    }

    fun nextQuestion() {
        val state = _uiState.value
        if (!state.isAnswerSubmitted) return

        if (state.currentQuestionIndex < state.questions.size - 1) {
            _uiState.value = state.copy(
                currentQuestionIndex = state.currentQuestionIndex + 1,
                selectedAnswerIndex = null,
                isAnswerSubmitted = false
            )
        } else {
            _uiState.value = state.copy(isQuizFinished = true)
        }
    }

    fun restartQuiz() {
        _uiState.value = _uiState.value.copy(
            questions = allQuestions.shuffled().take(QUIZ_SIZE),
            currentQuestionIndex = 0,
            score = 0,
            selectedAnswerIndex = null,
            isAnswerSubmitted = false,
            isQuizFinished = false
        )
    }
}
