package app.krafted.wildkingdom.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class QuizQuestion(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)

@Keep
data class QuizData(
    @SerializedName("questions")
    val questions: List<QuizQuestion>
)
