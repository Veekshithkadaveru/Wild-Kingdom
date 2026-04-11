package app.krafted.wildkingdom.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.krafted.wildkingdom.R
import app.krafted.wildkingdom.data.model.Animal
import app.krafted.wildkingdom.ui.components.AppIcons
import app.krafted.wildkingdom.ui.components.bounceClick
import app.krafted.wildkingdom.ui.components.pressEffect
import app.krafted.wildkingdom.ui.theme.DarkBackground
import app.krafted.wildkingdom.ui.theme.DarkBorder
import app.krafted.wildkingdom.ui.theme.TextPrimary
import app.krafted.wildkingdom.ui.theme.TextSecondary
import app.krafted.wildkingdom.ui.theme.TextTertiary
import app.krafted.wildkingdom.ui.theme.BrandGold
import app.krafted.wildkingdom.viewmodel.HomeViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.graphics.Color as AndroidColor

@Composable
fun HomeScreen(
    onChapterClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onBookmarksClick: () -> Unit,
    onQuizClick: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val animals by viewModel.animals.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Image(
            painter = painterResource(id = R.drawable.home_background_anime),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(0.35f)
        )

        LazyColumn(
            contentPadding = PaddingValues(bottom = 40.dp),
            modifier = Modifier.navigationBarsPadding()
        ) {
            item {
                HomeHeader(
                    onSearchClick = onSearchClick,
                    onBookmarksClick = onBookmarksClick
                )
            }

            item {
                QuizBanner(onClick = onQuizClick)
            }

            item {
                SectionLabel()
            }

            itemsIndexed(animals, key = { _, animal -> animal.id }) { index, animal ->
                AnimalCard(
                    animal = animal,
                    index = index,
                    onClick = { onChapterClick(animal.id) }
                )
            }
        }
    }
}

@Composable
private fun HomeHeader(
    onSearchClick: () -> Unit,
    onBookmarksClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DarkBackground,
                        DarkBackground,
                        DarkBackground.copy(alpha = 0.95f),
                        DarkBackground.copy(alpha = 0.7f),
                        Color.Transparent
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(top = 36.dp, bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.home_title_disaster),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 44.sp,
                            letterSpacing = (-1.2).sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = stringResource(R.string.home_title_ready),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 40.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 44.sp,
                            letterSpacing = (-1.2).sp,
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFF9A825),
                                    Color(0xFF558B2F)
                                )
                            )
                        )
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFFF9A825),
                                        Color(0xFFF9A825).copy(alpha = 0.4f)
                                    )
                                )
                            )
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.home_subtitle_know),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.3.sp
                            ),
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.home_subtitle_stay_alive),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.3.sp
                            ),
                            color = Color(0xFFF9A825).copy(alpha = 0.85f)
                        )
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 20.dp)
                ) {
                    HeaderIconButton(onClick = onSearchClick) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(R.string.home_search),
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    HeaderIconButton(onClick = onBookmarksClick) {
                        Icon(
                            imageVector = AppIcons.BookmarkBorder,
                            contentDescription = stringResource(R.string.home_bookmarks),
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderIconButton(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.06f))
            .drawBehind {
                drawCircle(
                    color = Color.White.copy(alpha = 0.04f),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                )
            }
            .bounceClick(onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun SectionLabel() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .padding(top = 28.dp, bottom = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFFF9A825))
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.home_section_survival_guides),
            style = MaterialTheme.typography.labelMedium.copy(
                letterSpacing = 2.4.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(0.5.dp)
                .background(Color.White.copy(alpha = 0.08f))
        )
    }
}

@Composable
private fun QuizBanner(onClick: () -> Unit) {
    val offsetY = remember { Animatable(30f) }
    val alpha = remember { Animatable(0f) }
    val infiniteTransition = rememberInfiniteTransition(label = "shine")
    val shineProgress by infiniteTransition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, delayMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shineProgress"
    )

    LaunchedEffect(Unit) {
        coroutineScope {
            launch {
                offsetY.animateTo(0f, spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessLow))
            }
            launch {
                alpha.animateTo(1f, tween(400))
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .graphicsLayer {
                translationY = offsetY.value
                this.alpha = alpha.value
            }
            .clip(RoundedCornerShape(24.dp))
            .bounceClick(onClick)
    ) {
        Box(modifier = Modifier.matchParentSize().background(Color(0xFF0A0F14)))

        Image(
            painter = painterResource(id = R.drawable.tiger_banner),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .matchParentSize()
                .scale(1.1f)
                .blur(radius = 16.dp)
                .graphicsLayer { this.alpha = 0.9f }
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .drawBehind {
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF0D1219),
                                Color(0xFF0D1219).copy(alpha = 0.9f),
                                Color(0xFF0D1219).copy(alpha = 0.4f),
                                Color.Transparent
                            ),
                            startX = 0f,
                            endX = size.width * 0.8f
                        )
                    )
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF0D1219).copy(alpha = 0.8f)
                            ),
                            startY = size.height * 0.5f,
                            endY = size.height
                        )
                    )
                }
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .drawBehind {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.05f),
                                Color.White.copy(alpha = 0.2f),
                                Color.White.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            start = Offset(size.width * shineProgress - size.width * 0.5f, 0f),
                            end = Offset(size.width * shineProgress + size.width * 0.5f, size.height)
                        )
                    )
                }
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .drawBehind {
                    drawRoundRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                BrandGold.copy(alpha = 0.6f),
                                Color.White.copy(alpha = 0.1f),
                                Color.Transparent,
                                Color.White.copy(alpha = 0.05f)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, size.height)
                        ),
                        cornerRadius = CornerRadius(24.dp.toPx()),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.dp.toPx())
                    )
                }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.85f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(BrandGold)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "NEW CHALLENGE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 9.sp
                        ),
                        color = BrandGold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Survival Quiz",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp,
                        shadow = Shadow(Color.Black.copy(alpha = 0.6f), blurRadius = 12f)
                    ),
                    color = Color.White
                )
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = "Test your wilderness knowledge",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        lineHeight = 20.sp,
                        shadow = Shadow(Color.Black.copy(alpha = 0.6f), blurRadius = 8f)
                    ),
                    color = Color.White.copy(alpha = 0.9f)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BrandGold)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "START",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            ),
                            color = Color.Black
                        )
                        Icon(
                            imageVector = AppIcons.ChevronRight,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
@Composable
private fun AnimalCard(animal: Animal, index: Int, onClick: () -> Unit) {
    val offsetY = remember { Animatable(50f) }
    val cardAlpha = remember { Animatable(0f) }
    val accentProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(index * 100L)
        coroutineScope {
            launch {
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = 0.75f,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                cardAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 400)
                )
            }
            launch {
                accentProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 500)
                )
            }
        }
    }

    val accent = remember(animal.accentColor) {
        try {
            Color(AndroidColor.parseColor(animal.accentColor))
        } catch (_: IllegalArgumentException) {
            Color(0xFFF9A825)
        }
    }

    val context = LocalContext.current
    val symbolResId = remember(animal.symbol) {
        context.resources.getIdentifier(animal.symbol, "drawable", context.packageName)
    }
    val backgroundResId = remember(animal.background) {
        context.resources.getIdentifier(animal.background, "drawable", context.packageName)
    }

    val totalFacts = animal.tabs.sumOf { it.cards.size }
    val cardTextShadow = remember {
        Shadow(
            color = Color.Black.copy(alpha = 0.65f),
            offset = Offset(0f, 2f),
            blurRadius = 12f
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .graphicsLayer {
                translationY = offsetY.value
                alpha = cardAlpha.value
            }
            .clip(RoundedCornerShape(20.dp))
            .pressEffect(onClick)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0xFF0D1219))
        )

        if (backgroundResId != 0) {
            Image(
                painter = painterResource(id = backgroundResId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0.65f)
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .drawBehind {
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF0D1219),
                                Color(0xFF0D1219).copy(alpha = 0.85f),
                                Color(0xFF0D1219).copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            startX = 0f,
                            endX = size.width * 0.95f
                        ),
                        cornerRadius = CornerRadius(20.dp.toPx())
                    )

                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                accent.copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            startX = 0f,
                            endX = size.width * 0.5f
                        ),
                        cornerRadius = CornerRadius(20.dp.toPx())
                    )

                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = size.height * 0.4f
                        ),
                        cornerRadius = CornerRadius(20.dp.toPx())
                    )

                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.1f),
                                DarkBorder.copy(alpha = 0.5f)
                            )
                        ),
                        cornerRadius = CornerRadius(20.dp.toPx()),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 0.6.dp.toPx()
                        )
                    )
                }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(4.5.dp)
                    .fillMaxHeight()
                    .padding(vertical = 16.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .graphicsLayer {
                        scaleY = accentProgress.value
                        alpha = accentProgress.value
                    }
                    .background(
                        Brush.verticalGradient(
                            listOf(accent, accent.copy(alpha = 0.6f))
                        )
                    )
                    .drawBehind {
                        drawCircle(
                            color = accent.copy(alpha = 0.3f),
                            radius = size.width * 2.5f,
                            center = Offset(size.width / 2, size.height / 2)
                        )
                    }
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 20.dp, end = 12.dp, top = 22.dp, bottom = 22.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = animal.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            shadow = cardTextShadow,
                            letterSpacing = (-0.5).sp
                        ),
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = animal.subtitle.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            shadow = cardTextShadow,
                            fontSize = 10.sp
                        ),
                        color = accent.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = animal.heroFact,
                        style = MaterialTheme.typography.bodySmall.copy(
                            lineHeight = 20.sp,
                            letterSpacing = 0.1.sp,
                            shadow = cardTextShadow
                        ),
                        color = TextSecondary.copy(alpha = 0.95f),
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(accent.copy(alpha = 0.1f))
                            .border(
                                width = 0.5.dp,
                                color = accent.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.home_tips_count, totalFacts),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            ),
                            color = accent
                        )
                    }
                }

                if (symbolResId != 0) {
                    val infiniteTransition = rememberInfiniteTransition(label = "animalFloat")
                    val floatValue by infiniteTransition.animateFloat(
                        initialValue = -5f,
                        targetValue = 5f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(2500, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "float"
                    )

                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .graphicsLayer { translationY = floatValue },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .drawBehind {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                accent.copy(alpha = 0.18f),
                                                Color.Transparent
                                            )
                                        )
                                    )
                                }
                        )
                        Image(
                            painter = painterResource(id = symbolResId),
                            contentDescription = animal.name,
                            modifier = Modifier.size(72.dp),
                            contentScale = ContentScale.Fit,
                            alpha = 1f
                        )
                    }
                }

                Icon(
                    imageVector = AppIcons.ChevronRight,
                    contentDescription = null,
                    tint = TextTertiary,
                    modifier = Modifier
                        .size(24.dp)
                        .alpha(0.6f)
                )
            }
        }
    }
}
