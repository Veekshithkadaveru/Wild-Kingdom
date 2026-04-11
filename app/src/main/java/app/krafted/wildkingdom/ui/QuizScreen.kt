package app.krafted.wildkingdom.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.krafted.wildkingdom.R
import app.krafted.wildkingdom.data.model.QuizQuestion
import app.krafted.wildkingdom.ui.components.bounceClick
import app.krafted.wildkingdom.ui.theme.BrandGold
import app.krafted.wildkingdom.ui.theme.DarkBackground
import app.krafted.wildkingdom.viewmodel.QuizViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun QuizScreen(
    onBackClick: () -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Image(
            painter = painterResource(id = R.drawable.home_background_anime),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(DarkBackground.copy(alpha = 0.85f))
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            QuizTopBar(onBackClick = onBackClick)
            
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandGold)
                }
            } else if (state.isQuizFinished) {
                QuizResult(
                    score = state.score,
                    total = state.questions.size,
                    onRestart = { viewModel.restartQuiz() },
                    onHome = onBackClick
                )
            } else {
                state.currentQuestion?.let { question ->
                    QuizContent(
                        question = question,
                        questionIndex = state.currentQuestionIndex,
                        totalQuestions = state.questions.size,
                        selectedOption = state.selectedAnswerIndex,
                        isSubmitted = state.isAnswerSubmitted,
                        onOptionSelect = { viewModel.selectAnswer(it) },
                        onSubmit = { viewModel.submitAnswer() },
                        onNext = { viewModel.nextQuestion() }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        DarkBackground.copy(alpha = 0.95f),
                        DarkBackground.copy(alpha = 0.8f),
                        Color.Transparent
                    )
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
                    .bounceClick(onBackClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = "SURVIVAL QUIZ",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.4.sp
                ),
                color = BrandGold
            )

            Spacer(modifier = Modifier.size(44.dp))
        }
    }
}

@Composable
private fun QuizContent(
    question: QuizQuestion,
    questionIndex: Int,
    totalQuestions: Int,
    selectedOption: Int?,
    isSubmitted: Boolean,
    onOptionSelect: (Int) -> Unit,
    onSubmit: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "QUESTION ${questionIndex + 1}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = Color.White
            )
            Text(
                text = "$totalQuestions",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        val animatedProgress by animateFloatAsState(
            targetValue = ((questionIndex + 1).toFloat() / totalQuestions),
            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessLow),
            label = "progress"
        )
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = BrandGold,
            trackColor = Color.White.copy(alpha = 0.1f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        val textOffsetY = remember { Animatable(30f) }
        val textAlpha = remember { Animatable(0f) }
        
        LaunchedEffect(questionIndex) {
            textOffsetY.snapTo(30f)
            textAlpha.snapTo(0f)
            coroutineScope {
                launch {
                    textOffsetY.animateTo(
                        targetValue = 0f,
                        animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow)
                    )
                }
                launch {
                    textAlpha.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 400)
                    )
                }
            }
        }

        Text(
            text = question.question,
            modifier = Modifier.graphicsLayer {
                translationY = textOffsetY.value
                alpha = textAlpha.value
            },
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                lineHeight = 34.sp
            ),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(count = question.options.size, key = { "$questionIndex-$it" }) { index ->
                val optionOffsetY = remember { Animatable(40f) }
                val optionAlpha = remember { Animatable(0f) }

                LaunchedEffect(questionIndex) {
                    optionOffsetY.snapTo(40f)
                    optionAlpha.snapTo(0f)
                    delay(index * 80L)
                    coroutineScope {
                        launch {
                            optionOffsetY.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(dampingRatio = 0.75f, stiffness = Spring.StiffnessLow)
                            )
                        }
                        launch {
                            optionAlpha.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(durationMillis = 300)
                            )
                        }
                    }
                }

                val isSelected = selectedOption == index
                val isCorrectAnswer = isSubmitted && index == question.correctAnswerIndex
                val isWrongSelection = isSubmitted && isSelected && index != question.correctAnswerIndex

                val optionScale by animateFloatAsState(
                    targetValue = if (isSelected && !isSubmitted) 1.02f else 1f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
                    label = "optionScale"
                )

                val bgColor by animateColorAsState(
                    targetValue = when {
                        isCorrectAnswer -> Color(0xFF4CAF50).copy(alpha = 0.35f)
                        isWrongSelection -> Color(0xFFF44336).copy(alpha = 0.35f)
                        isSelected -> BrandGold.copy(alpha = 0.35f)
                        else -> Color.Black.copy(alpha = 0.5f)
                    },
                    animationSpec = tween(300),
                    label = "bgColor"
                )

                val borderColor by animateColorAsState(
                    targetValue = when {
                        isCorrectAnswer -> Color(0xFF4CAF50).copy(alpha = 0.8f)
                        isWrongSelection -> Color(0xFFF44336).copy(alpha = 0.8f)
                        isSelected -> BrandGold.copy(alpha = 0.8f)
                        else -> Color.White.copy(alpha = 0.15f)
                    },
                    animationSpec = tween(300),
                    label = "borderColor"
                )
                
                val contentColor by animateColorAsState(
                    targetValue = when {
                        isCorrectAnswer -> Color(0xFF81C784)
                        isWrongSelection -> Color(0xFFE57373)
                        isSelected -> BrandGold
                        else -> Color.White.copy(alpha = 0.85f)
                    },
                    animationSpec = tween(300),
                    label = "contentColor"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            translationY = optionOffsetY.value
                            alpha = optionAlpha.value
                            scaleX = optionScale
                            scaleY = optionScale
                        }
                        .clip(RoundedCornerShape(16.dp))
                        .background(bgColor)
                        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                        .clickable(enabled = !isSubmitted) { onOptionSelect(index) }
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = question.options[index],
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (isSelected || isCorrectAnswer) FontWeight.SemiBold else FontWeight.Medium,
                                fontSize = 16.sp
                            ),
                            color = contentColor,
                            modifier = Modifier.weight(1f)
                        )
                        
                        AnimatedVisibility(
                            visible = isSubmitted && (isCorrectAnswer || isWrongSelection),
                            enter = scaleIn(spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium)) + fadeIn(),
                            exit = fadeOut()
                        ) {
                            Icon(
                                imageVector = if (isCorrectAnswer) Icons.Filled.Check else Icons.Filled.Close,
                                contentDescription = null,
                                tint = if (isCorrectAnswer) Color(0xFF4CAF50) else Color(0xFFF44336),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        
                        AnimatedVisibility(
                            visible = !isSubmitted && isSelected,
                            enter = scaleIn(spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMedium)) + fadeIn(),
                            exit = fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(BrandGold)
                            )
                        }
                    }
                }
            }

            if (isSubmitted) {
                item {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(400, delayMillis = 200)) + expandVertically(spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow)),
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Black.copy(alpha = 0.5f))
                                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
                                .padding(20.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(BrandGold)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "EXPLANATION",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp),
                                        color = BrandGold
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = question.explanation,
                                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = if (isSubmitted) onNext else onSubmit,
            enabled = selectedOption != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandGold,
                disabledContainerColor = Color.White.copy(alpha = 0.05f),
                contentColor = Color.Black,
                disabledContentColor = Color.White.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            AnimatedContent(
                targetState = isSubmitted,
                transitionSpec = {
                    if (targetState) {
                        (slideInVertically { height -> height } + fadeIn()) togetherWith (slideOutVertically { height -> -height } + fadeOut())
                    } else {
                        (slideInVertically { height -> -height } + fadeIn()) togetherWith (slideOutVertically { height -> height } + fadeOut())
                    }
                },
                label = "button_text"
            ) { submitted ->
                Text(
                    text = if (submitted) "NEXT QUESTION" else "CONFIRM ANSWER",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                )
            }
        }
    }
}

@Composable
private fun QuizResult(
    score: Int,
    total: Int,
    onRestart: () -> Unit,
    onHome: () -> Unit
) {
    var animationPlayed by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(300)
        animationPlayed = true
    }

    val percentage = if (total > 0) score.toFloat() / total else 0f
    
    val animatedPercentage by animateFloatAsState(
        targetValue = if (animationPlayed) percentage else 0f,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "score_animation"
    )

    val animatedScore by animateIntAsState(
        targetValue = if (animationPlayed) score else 0,
        animationSpec = tween(1500, easing = FastOutSlowInEasing),
        label = "score_count_animation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AnimatedVisibility(
            visible = animationPlayed,
            enter = slideInVertically(initialOffsetY = { -50 }) + fadeIn(tween(500))
        ) {
            Text(
                text = "QUIZ COMPLETED",
                style = MaterialTheme.typography.labelLarge.copy(
                    letterSpacing = 4.sp,
                    fontWeight = FontWeight.Black
                ),
                color = BrandGold
            )
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawArc(
                    color = Color.White.copy(alpha = 0.05f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
                drawArc(
                    color = BrandGold,
                    startAngle = -90f,
                    sweepAngle = animatedPercentage * 360f,
                    useCenter = false,
                    style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$animatedScore",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 64.sp
                    ),
                    color = Color.White
                )
                Text(
                    text = "OUT OF $total",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
        
        AnimatedVisibility(
            visible = animationPlayed,
            enter = slideInVertically(initialOffsetY = { 50 }) + fadeIn(tween(500, delayMillis = 500))
        ) {
            val percentInt = (percentage * 100).toInt()
            Text(
                text = when {
                    percentInt == 100 -> "Perfect Survival Sense!"
                    percentInt >= 80 -> "Great Survival Sense!"
                    percentInt >= 50 -> "Good Effort!"
                    else -> "Needs More Practice!"
                },
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(64.dp))
        
        AnimatedVisibility(
            visible = animationPlayed,
            enter = slideInVertically(initialOffsetY = { 50 }) + fadeIn(tween(500, delayMillis = 800))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = onRestart,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGold, contentColor = Color.Black),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("TRY AGAIN", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
                }
                
                OutlinedButton(
                    onClick = onHome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("RETURN TO HOME", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
                }
            }
        }
    }
}
