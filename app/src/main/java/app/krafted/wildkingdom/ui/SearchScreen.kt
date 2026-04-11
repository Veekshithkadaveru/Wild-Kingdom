package app.krafted.wildkingdom.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import app.krafted.wildkingdom.R
import app.krafted.wildkingdom.ui.components.CategoryBadge
import app.krafted.wildkingdom.ui.components.bounceClick
import app.krafted.wildkingdom.ui.components.pressEffect
import app.krafted.wildkingdom.ui.theme.DarkBackground
import app.krafted.wildkingdom.ui.theme.DarkBorder
import app.krafted.wildkingdom.ui.theme.TextPrimary
import app.krafted.wildkingdom.ui.theme.TextSecondary
import app.krafted.wildkingdom.ui.theme.TextTertiary
import app.krafted.wildkingdom.viewmodel.SearchResult
import app.krafted.wildkingdom.viewmodel.SearchViewModel
import android.graphics.Color as AndroidColor

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onResultClick: (chapterId: String, tipId: Int) -> Unit = { _, _ -> },
    viewModel: SearchViewModel = viewModel()
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 40.dp),
            modifier = Modifier.navigationBarsPadding()
        ) {
            item {
                SearchHeader(
                    query = query,
                    onQueryChange = viewModel::onQueryChange,
                    onBackClick = onBackClick,
                    focusRequester = focusRequester
                )
            }

            if (query.isNotBlank() && results.isEmpty()) {
                item {
                    EmptySearchState(query = query)
                }
            }

            if (query.isNotBlank() && results.isNotEmpty()) {
                item {
                    ResultCountBar(count = results.size)
                }
            }

            itemsIndexed(results, key = { _, r -> "${r.animalId}_${r.fact.id}" }) { index, result ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(200)) +
                            slideInVertically(
                                initialOffsetY = { it / 4 },
                                animationSpec = tween(280, delayMillis = index.coerceAtMost(8) * 40)
                            )
                ) {
                    SearchResultCard(
                        result = result,
                        query = query,
                        onClick = { onResultClick(result.animalId, result.fact.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    focusRequester: FocusRequester
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 8.dp)
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
                .bounceClick(onBackClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.search_back),
                tint = Color.White.copy(alpha = 0.85f),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.search_title),
            style = MaterialTheme.typography.headlineLarge.copy(
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp
            ),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.search_subtitle),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.3.sp
            ),
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .width(32.dp)
                .height(3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFF9A825),
                            Color(0xFFF9A825).copy(alpha = 0.3f)
                        )
                    )
                )
        )

        Spacer(modifier = Modifier.height(20.dp))

        SearchField(
            query = query,
            onQueryChange = onQueryChange,
            focusRequester = focusRequester
        )
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    focusRequester: FocusRequester
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val glowAlpha by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        animationSpec = tween(300),
        label = "glow"
    )
    val accentGold = Color(0xFFF9A825)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .drawBehind {
                if (glowAlpha > 0f) {
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                accentGold.copy(alpha = 0.10f * glowAlpha),
                                accentGold.copy(alpha = 0.03f * glowAlpha)
                            )
                        ),
                        cornerRadius = CornerRadius(14.dp.toPx())
                    )
                }
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.05f),
                    cornerRadius = CornerRadius(14.dp.toPx())
                )
                val borderColor = if (glowAlpha > 0.5f)
                    accentGold.copy(alpha = 0.30f)
                else
                    DarkBorder.copy(alpha = 0.6f)
                drawRoundRect(
                    color = borderColor,
                    cornerRadius = CornerRadius(14.dp.toPx()),
                    style = Stroke(width = (0.5f + 0.5f * glowAlpha).dp.toPx())
                )
            }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = if (isFocused) accentGold.copy(alpha = 0.7f) else TextTertiary,
                modifier = Modifier.size(20.dp)
            )

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                interactionSource = interactionSource,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                ),
                cursorBrush = SolidColor(accentGold),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
                    .focusRequester(focusRequester),
                decorationBox = { innerTextField ->
                    Box {
                        if (query.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_placeholder),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                color = TextTertiary
                            )
                        }
                        innerTextField()
                    }
                }
            )

            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn(tween(150)),
                exit = fadeOut(tween(150))
            ) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true, radius = 13.dp),
                            onClick = { onQueryChange("") }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.search_clear),
                        tint = TextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultCountBar(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = pluralStringResource(R.plurals.search_results_found, count, count),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.3.sp
            ),
            color = TextTertiary
        )
    }
}

@Composable
private fun EmptySearchState(query: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 56.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .drawBehind {
                    drawCircle(color = Color.White.copy(alpha = 0.03f))
                    drawCircle(
                        color = Color.White.copy(alpha = 0.06f),
                        radius = size.minDimension / 2 * 0.7f
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                tint = TextTertiary.copy(alpha = 0.5f),
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.search_no_results),
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.search_nothing_matched, query),
            style = MaterialTheme.typography.bodySmall.copy(
                letterSpacing = 0.2.sp
            ),
            color = TextTertiary
        )
    }
}

@Composable
private fun SearchResultCard(
    result: SearchResult,
    query: String,
    onClick: () -> Unit
) {
    val accent = try {
        Color(AndroidColor.parseColor(result.accentColor))
    } catch (_: IllegalArgumentException) {
        Color(0xFFF9A825)
    }
    val context = LocalContext.current
    val symbolResId = remember(result.symbol) {
        context.resources.getIdentifier(result.symbol, "drawable", context.packageName)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .pressEffect(onClick)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .drawBehind {
                    drawRoundRect(
                        color = Color(0xFF0D1219),
                        cornerRadius = CornerRadius(16.dp.toPx())
                    )
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                accent.copy(alpha = 0.08f),
                                Color.Transparent
                            ),
                            startX = 0f,
                            endX = size.width * 0.5f
                        ),
                        cornerRadius = CornerRadius(16.dp.toPx())
                    )
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.05f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = size.height * 0.4f
                        ),
                        cornerRadius = CornerRadius(16.dp.toPx())
                    )
                    drawRoundRect(
                        color = DarkBorder.copy(alpha = 0.6f),
                        cornerRadius = CornerRadius(16.dp.toPx()),
                        style = Stroke(width = 0.5.dp.toPx())
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
                    .width(4.dp)
                    .fillMaxHeight()
                    .padding(vertical = 12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(accent, accent.copy(alpha = 0.6f))
                        )
                    )
                    .drawBehind {
                        drawCircle(
                            color = accent.copy(alpha = 0.2f),
                            radius = size.width * 2f,
                            center = Offset(size.width / 2, size.height / 2)
                        )
                    }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        if (symbolResId != 0) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .drawBehind {
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    accent.copy(alpha = 0.15f),
                                                    Color.Transparent
                                                )
                                            ),
                                            radius = size.minDimension / 2
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = symbolResId),
                                    contentDescription = result.animalName,
                                    modifier = Modifier.size(14.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                        Text(
                            text = result.animalName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.3.sp,
                                fontSize = 10.sp
                            ),
                            color = accent
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = result.fact.category,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 0.6.sp,
                                fontSize = 8.sp
                            ),
                            color = TextTertiary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .padding(top = 8.dp, bottom = 8.dp)
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    accent.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                CategoryBadge(category = result.fact.category, accent = accent)

                Spacer(modifier = Modifier.height(8.dp))

                HighlightedText(
                    text = result.fact.title,
                    highlight = query,
                    accentColor = accent,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.2).sp
                    ),
                    baseColor = TextPrimary
                )

                Spacer(modifier = Modifier.height(5.dp))

                HighlightedText(
                    text = result.fact.body,
                    highlight = query,
                    accentColor = accent,
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = 18.sp,
                        letterSpacing = 0.1.sp
                    ),
                    baseColor = TextSecondary,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun HighlightedText(
    text: String,
    highlight: String,
    accentColor: Color,
    style: androidx.compose.ui.text.TextStyle,
    baseColor: Color,
    maxLines: Int = Int.MAX_VALUE
) {
    val hlTrimmed = highlight.trim()
    if (hlTrimmed.isEmpty()) {
        Text(
            text = text,
            style = style,
            color = baseColor,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis
        )
        return
    }

    val annotated = buildAnnotatedString {
        var start = 0
        while (start < text.length) {
            val idx = text.indexOf(hlTrimmed, startIndex = start, ignoreCase = true)
            if (idx == -1) {
                withStyle(SpanStyle(color = baseColor)) {
                    append(text.substring(start))
                }
                break
            }
            if (idx > start) {
                withStyle(SpanStyle(color = baseColor)) {
                    append(text.substring(start, idx))
                }
            }
            withStyle(
                SpanStyle(
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    background = accentColor.copy(alpha = 0.10f)
                )
            ) {
                append(text.substring(idx, idx + hlTrimmed.length))
            }
            start = idx + hlTrimmed.length
        }
    }

    Text(
        text = annotated,
        style = style,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}
