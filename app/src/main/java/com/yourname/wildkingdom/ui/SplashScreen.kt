package com.yourname.wildkingdom.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yourname.wildkingdom.R
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {

    var logoScale by remember { mutableFloatStateOf(0.5f) }
    var logoAlpha by remember { mutableFloatStateOf(0f) }
    var logoRotation by remember { mutableFloatStateOf(-30f) }
    var glowAlpha by remember { mutableFloatStateOf(0f) }
    var textAlpha by remember { mutableFloatStateOf(0f) }
    var textOffset by remember { mutableFloatStateOf(30f) }

    val scaleAnim = remember { Animatable(0.5f) }
    val alphaAnim = remember { Animatable(0f) }
    val rotationAnim = remember { Animatable(-30f) }
    val glowAnim = remember { Animatable(0f) }
    val textAlphaAnim = remember { Animatable(0f) }
    val textOffsetAnim = remember { Animatable(30f) }

    val infinite = rememberInfiniteTransition(label = "glow_pulse")
    val pulseScale by infinite.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            tween(1000, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val floatingOffset by infinite.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = EaseOutCubic),
            RepeatMode.Reverse
        ),
        label = "float"
    )
    
    val ringRotation by infinite.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(6000, easing = LinearEasing),
            RepeatMode.Restart
        ),
        label = "ring_rotation"
    )

    LaunchedEffect(Unit) {
        delay(100)

        coroutineScope {
            launch {
                alphaAnim.animateTo(
                    1f,
                    tween(600, easing = EaseOutCubic)
                ) { logoAlpha = value }
            }
            launch {
                scaleAnim.animateTo(
                    1f,
                    tween(800, easing = EaseOutBack)
                ) { logoScale = value }
            }
            launch {
                rotationAnim.animateTo(
                    0f,
                    tween(800, easing = EaseOutBack)
                ) { logoRotation = value }
            }
            launch {
                delay(300)
                textAlphaAnim.animateTo(
                    1f,
                    tween(600, easing = EaseOutCubic)
                ) { textAlpha = value }
            }
            launch {
                delay(300)
                textOffsetAnim.animateTo(
                    0f,
                    tween(800, easing = EaseOutBack)
                ) { textOffset = value }
            }
        }

        glowAnim.animateTo(
            1f,
            tween(600, easing = EaseOutCubic)
        ) { glowAlpha = value }

        delay(400)
        onSplashFinished()
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF1E1511),
                        Color(0xFF0A0A0A)
                    ),
                    radius = 800f
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.graphicsLayer {
                translationY = floatingOffset
            }
        ) {

            Box(
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .scale(if (glowAlpha >= 0.9f) pulseScale else 1f)
                        .graphicsLayer { alpha = glowAlpha }
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        Color(0xFFFF9800).copy(alpha = 0.35f),
                                        Color(0xFFFF5722).copy(alpha = 0.15f),
                                        Color.Transparent
                                    )
                                )
                            )
                        }
                )
                
                Box(
                    modifier = Modifier
                        .size(218.dp)
                        .graphicsLayer {
                            alpha = glowAlpha
                            rotationZ = ringRotation
                            scaleX = logoScale
                            scaleY = logoScale
                        }
                        .border(
                            width = 3.dp,
                            brush = Brush.sweepGradient(
                                listOf(
                                    Color.Transparent,
                                    Color(0xFFFF9800),
                                    Color(0xFFFFEB3B),
                                    Color.Transparent,
                                    Color(0xFFFF5722),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                Image(
                    painter = painterResource(R.drawable.tiger_sym_7),
                    contentDescription = stringResource(R.string.splash_logo_description),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(130.dp)
                        .graphicsLayer {
                            scaleX = logoScale
                            scaleY = logoScale
                            alpha = logoAlpha
                            rotationZ = logoRotation
                        }
                        .clip(CircleShape)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                listOf(Color(0xFFFFB74D), Color(0xFFFF5722), Color(0xFFFFB74D))
                            ),
                            shape = CircleShape
                        )
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp,
                    fontSize = 32.sp
                ),
                color = Color.White,
                modifier = Modifier.graphicsLayer {
                    alpha = textAlpha
                    translationY = textOffset
                }
            )
        }
    }
}
