package app.krafted.wildkingdom.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.wildkingdom.R
import app.krafted.wildkingdom.data.model.Fact
import app.krafted.wildkingdom.ui.components.bounceClick
import app.krafted.wildkingdom.ui.theme.DarkBorder
import app.krafted.wildkingdom.ui.theme.TextPrimary
import app.krafted.wildkingdom.ui.theme.TextSecondary
import app.krafted.wildkingdom.ui.theme.TextTertiary

@Composable
fun FactCard(
    fact: Fact,
    accent: Color,
    isBookmarked: Boolean,
    onBookmarkToggle: () -> Unit,
    index: Int,
    animalName: String? = null,
    animalSymbol: String? = null
) {
    val context = LocalContext.current
    val symbolResId = remember(animalSymbol) {
        if (animalSymbol != null)
            context.resources.getIdentifier(animalSymbol, "drawable", context.packageName)
        else 0
    }

    var pulseTarget by remember { mutableStateOf(1f) }
    var previousBookmarked by remember { mutableStateOf(isBookmarked) }
    val pulseScale by animateFloatAsState(
        targetValue = pulseTarget,
        animationSpec = spring(stiffness = 500f, dampingRatio = 0.5f),
        label = "bookmarkPulse"
    )
    LaunchedEffect(isBookmarked) {
        if (isBookmarked != previousBookmarked) {
            previousBookmarked = isBookmarked
            pulseTarget = 1.4f
            kotlinx.coroutines.delay(50L)
            pulseTarget = 1f
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
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
                            colors = listOf(accent.copy(alpha = 0.08f), Color.Transparent),
                            startX = 0f, endX = size.width * 0.5f
                        ),
                        cornerRadius = CornerRadius(16.dp.toPx())
                    )
                    
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.White.copy(alpha = 0.06f), Color.Transparent),
                            startY = 0f, endY = size.height * 0.4f
                        ),
                        cornerRadius = CornerRadius(16.dp.toPx())
                    )
                    
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                DarkBorder.copy(alpha = 0.8f),
                                DarkBorder.copy(alpha = 0.3f)
                            )
                        ),
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
                    .padding(vertical = 14.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(accent, accent.copy(alpha = 0.6f))
                        )
                    )
                    .drawBehind {
                        drawCircle(
                            color = accent.copy(alpha = 0.25f),
                            radius = size.width * 2f,
                            center = Offset(size.width / 2, size.height / 2)
                        )
                    }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 8.dp, top = 14.dp, bottom = 16.dp)
            ) {
                if (animalName != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (symbolResId != 0) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .drawBehind {
                                        drawCircle(
                                            brush = Brush.radialGradient(
                                                colors = listOf(
                                                    accent.copy(alpha = 0.18f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = symbolResId),
                                    contentDescription = animalName,
                                    modifier = Modifier.size(16.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        }
                        Text(
                            text = animalName.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.5.sp,
                                fontSize = 9.sp
                            ),
                            color = accent
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 12.dp, end = 12.dp)
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.White.copy(alpha = 0.12f), Color.Transparent)
                                )
                            )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CategoryBadge(category = fact.category, accent = accent)

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isBookmarked) accent.copy(alpha = 0.15f)
                                else Color.White.copy(alpha = 0.05f)
                            )
                            .bounceClick(onBookmarkToggle),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) AppIcons.Bookmark else AppIcons.BookmarkBorder,
                            contentDescription = if (isBookmarked) stringResource(R.string.tip_remove_bookmark)
                            else stringResource(R.string.tip_add_bookmark),
                            tint = if (isBookmarked) accent else TextTertiary,
                            modifier = Modifier
                                .size(18.dp)
                                .graphicsLayer {
                                    scaleX = pulseScale
                                    scaleY = pulseScale
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = fact.title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.1).sp,
                        fontSize = 16.sp
                    ),
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = fact.body,
                    style = MaterialTheme.typography.bodySmall.copy(
                        lineHeight = 22.sp,
                        letterSpacing = 0.2.sp
                    ),
                    color = TextSecondary
                )
            }
        }
    }
}
