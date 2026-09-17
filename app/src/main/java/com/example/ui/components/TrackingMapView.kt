package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HvacBluePrimary
import com.example.ui.theme.HvacBluePrimaryDark
import com.example.ui.theme.HvacThermalOrange

@Composable
fun TrackingMapView(
    progressFraction: Float,
    currentStreet: String,
    etaMinutes: Int,
    distanceMiles: Float,
    isMoving: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseScale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // 1. Map Canvas Background (Subtle realistic map tone)
            drawRect(
                color = Color(0xFF1E293B) // Modern dark cartography theme
            )

            // 2. City Grid & Street Blocks
            drawCityGrid(width, height)

            // 3. Parks / Green zones
            drawRoundRect(
                color = Color(0xFF14532D).copy(alpha = 0.35f),
                topLeft = Offset(width * 0.12f, height * 0.18f),
                size = Size(width * 0.28f, height * 0.22f),
                cornerRadius = CornerRadius(12f, 12f)
            )
            drawRoundRect(
                color = Color(0xFF1E3A8A).copy(alpha = 0.30f),
                topLeft = Offset(width * 0.65f, height * 0.58f),
                size = Size(width * 0.25f, height * 0.24f),
                cornerRadius = CornerRadius(12f, 12f)
            )

            // 4. Define Route Path from Hub to Customer Address
            val routePath = Path().apply {
                moveTo(width * 0.15f, height * 0.78f) // Depot Hub
                lineTo(width * 0.28f, height * 0.58f) // Turn 1
                lineTo(width * 0.48f, height * 0.58f) // Turn 2
                lineTo(width * 0.62f, height * 0.32f) // Turn 3
                lineTo(width * 0.76f, height * 0.32f) // Turn 4
                lineTo(width * 0.85f, height * 0.22f) // Customer Destination
            }

            // Draw full route background shadow
            drawPath(
                path = routePath,
                color = Color(0xFF334155),
                style = Stroke(width = 10.dp.toPx(), pathEffect = PathEffect.cornerPathEffect(16f))
            )

            // Measure path and compute current vehicle coordinates
            val pathMeasure = PathMeasure()
            pathMeasure.setPath(routePath, false)
            val pathLength = pathMeasure.length
            val currentDistance = (pathLength * progressFraction.coerceIn(0.01f, 0.999f))

            val currentPos = pathMeasure.getPosition(currentDistance)

            // Draw Traveled Route (Bright vibrant cyan)
            val traveledPath = Path()
            pathMeasure.getSegment(0f, currentDistance, traveledPath, true)
            drawPath(
                path = traveledPath,
                brush = Brush.horizontalGradient(
                    listOf(Color(0xFF0284C7), Color(0xFF38BDF8))
                ),
                style = Stroke(width = 8.dp.toPx(), pathEffect = PathEffect.cornerPathEffect(16f))
            )

            // Draw Remaining Route (Dashed amber / gray)
            val remainingPath = Path()
            pathMeasure.getSegment(currentDistance, pathLength, remainingPath, true)
            drawPath(
                path = remainingPath,
                color = Color(0xFF94A3B8).copy(alpha = 0.5f),
                style = Stroke(
                    width = 4.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 14f), 0f)
                )
            )

            // 5. Draw Hub Pin (Origin)
            val hubPos = Offset(width * 0.15f, height * 0.78f)
            drawCircle(
                color = Color(0xFF475569),
                radius = 12.dp.toPx(),
                center = hubPos
            )
            drawCircle(
                color = Color(0xFF94A3B8),
                radius = 7.dp.toPx(),
                center = hubPos
            )

            // 6. Draw Customer Destination Pin (Goal) with animated pulse
            val destPos = Offset(width * 0.85f, height * 0.22f)
            // Pulse ring
            drawCircle(
                color = HvacThermalOrange.copy(alpha = pulseAlpha),
                radius = 16.dp.toPx() * pulseScale,
                center = destPos
            )
            // Outer glow
            drawCircle(
                color = HvacThermalOrange.copy(alpha = 0.3f),
                radius = 16.dp.toPx(),
                center = destPos
            )
            // Solid target pin
            drawCircle(
                color = HvacThermalOrange,
                radius = 10.dp.toPx(),
                center = destPos
            )
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = destPos
            )

            // 7. Draw Technician Van Marker at current position with radar wave
            // Radar ping
            if (isMoving) {
                drawCircle(
                    color = Color(0xFF38BDF8).copy(alpha = pulseAlpha),
                    radius = 18.dp.toPx() * pulseScale,
                    center = currentPos
                )
            }
            // Van shadow
            drawCircle(
                color = Color.Black.copy(alpha = 0.5f),
                radius = 15.dp.toPx(),
                center = Offset(currentPos.x + 2f, currentPos.y + 4f)
            )
            // Outer vehicle badge
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
                    center = currentPos,
                    radius = 16.dp.toPx()
                ),
                radius = 14.dp.toPx(),
                center = currentPos
            )
            // Inner vehicle core
            drawCircle(
                color = Color.White,
                radius = 6.dp.toPx(),
                center = currentPos
            )
        }

        // Top Status Overlay Chips
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xEE0F172A), RoundedCornerShape(12.dp))
                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = if (etaMinutes > 0) "⏱ ETA $etaMinutes MINS • $distanceMiles MILES REMAINING" else "🎯 TECHNICIAN ARRIVED AT SITE",
                    color = if (etaMinutes > 0) Color(0xFF38BDF8) else Color(0xFF4ADE80),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Bottom Map Legend / Street Address Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0xDD090D16))
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = "📍 $currentStreet",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
        }
    }
}

private fun DrawScope.drawCityGrid(width: Float, height: Float) {
    val roadColor = Color(0xFF26354A)
    val roadStroke = 3.dp.toPx()

    // Horizontal roads
    drawLine(roadColor, Offset(0f, height * 0.22f), Offset(width, height * 0.22f), strokeWidth = roadStroke)
    drawLine(roadColor, Offset(0f, height * 0.38f), Offset(width, height * 0.38f), strokeWidth = roadStroke)
    drawLine(roadColor, Offset(0f, height * 0.58f), Offset(width, height * 0.58f), strokeWidth = roadStroke)
    drawLine(roadColor, Offset(0f, height * 0.78f), Offset(width, height * 0.78f), strokeWidth = roadStroke)

    // Vertical roads
    drawLine(roadColor, Offset(width * 0.15f, 0f), Offset(width * 0.15f, height), strokeWidth = roadStroke)
    drawLine(roadColor, Offset(width * 0.35f, 0f), Offset(width * 0.35f, height), strokeWidth = roadStroke)
    drawLine(roadColor, Offset(width * 0.55f, 0f), Offset(width * 0.55f, height), strokeWidth = roadStroke)
    drawLine(roadColor, Offset(width * 0.75f, 0f), Offset(width * 0.75f, height), strokeWidth = roadStroke)
    drawLine(roadColor, Offset(width * 0.88f, 0f), Offset(width * 0.88f, height), strokeWidth = roadStroke)
}
