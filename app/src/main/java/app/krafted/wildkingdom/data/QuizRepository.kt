package app.krafted.wildkingdom.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import app.krafted.wildkingdom.data.model.QuizData
import app.krafted.wildkingdom.data.model.QuizQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class QuizRepository private constructor(private val context: Context) {

    private var _questions: List<QuizQuestion> = emptyList()
    private val mutex = Mutex()
    private var loaded = false

    companion object {
        @Volatile
        private var INSTANCE: QuizRepository? = null

        fun getInstance(context: Context): QuizRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: QuizRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    suspend fun loadQuiz(): List<QuizQuestion> {
        if (loaded) return _questions
        mutex.withLock {
            if (loaded) return _questions
            _questions = withContext(Dispatchers.IO) {
                try {
                    val json =
                        context.assets.open("quiz.json").bufferedReader().use { it.readText() }
                    Gson().fromJson(json, QuizData::class.java).questions
                } catch (e: Exception) {
                    Log.e("QuizRepository", "Failed to parse quiz.json", e)
                    emptyList()
                }
            }
            loaded = true
        }
        return _questions
    }

    fun getQuestions(): List<QuizQuestion> = _questions
}
