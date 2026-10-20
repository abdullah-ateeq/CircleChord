package com.example.circlechord.ui.home

import android.graphics.Paint
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CircleDiagram(
    angleDegrees: Float,
    enableAnimations: Boolean,
    modifier: Modifier = Modifier
) {
    val targetAngle = angleDegrees.coerceIn(0.1f, 360f)
    val animatedAngle by animateFloatAsState(
        targetValue = targetAngle,
        animationSpec = if (enableAnimations) tween(durationMillis = 350) else tween(durationMillis = 0),
        label = "DiagramAngle"
    )

    val circleColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
    val radiusLineColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
    val chordColor = MaterialTheme.colorScheme.tertiary
    val centerDotColor = MaterialTheme.colorScheme.onSurface
    val endpointDotColor = MaterialTheme.colorScheme.tertiary
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val arcColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .padding(8.dp)
            .semantics {
                contentDescription = "Circle showing the central angle, radius, and chord."
            }
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val center = Offset(canvasWidth / 2f, canvasHeight / 2f)
            val radius = (minOf(canvasWidth, canvasHeight) / 2f) * 0.78f

            // 1. Draw outer circle
            drawCircle(
                color = circleColor,
                radius = radius,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )

            // Convert animatedAngle to radians
            val angleRad = Math.toRadians(animatedAngle.toDouble())
            val halfAngleRad = angleRad / 2.0

            // Bisector points straight down (90 degrees = pi/2)
            // P1 = 90 - halfAngle, P2 = 90 + halfAngle
            val baseAngle = Math.PI / 2.0
            val p1Angle = baseAngle - halfAngleRad
            val p2Angle = baseAngle + halfAngleRad

            val p1 = Offset(
                x = (center.x + radius * cos(p1Angle)).toFloat(),
                y = (center.y + radius * sin(p1Angle)).toFloat()
            )
            val p2 = Offset(
                x = (center.x + radius * cos(p2Angle)).toFloat(),
                y = (center.y + radius * sin(p2Angle)).toFloat()
            )

            // 2. Draw angle arc at center
            val arcRadius = (radius * 0.28f).coerceAtLeast(24.dp.toPx())
            val startDeg = Math.toDegrees(p1Angle).toFloat()
            val sweepDeg = animatedAngle

            drawArc(
                color = arcColor,
                startAngle = startDeg,
                sweepAngle = sweepDeg,
                useCenter = true,
                topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                size = Size(arcRadius * 2f, arcRadius * 2f)
            )

            // 3. Draw radius lines
            drawLine(
                color = radiusLineColor,
                start = center,
                end = p1,
                strokeWidth = 3.5f.dp.toPx()
            )
            drawLine(
                color = radiusLineColor,
                start = center,
                end = p2,
                strokeWidth = 3.5f.dp.toPx()
            )

            // 4. Draw chord line
            drawLine(
                color = chordColor,
                start = p1,
                end = p2,
                strokeWidth = 4.5f.dp.toPx()
            )

            // 5. Center dot
            drawCircle(
                color = centerDotColor,
                radius = 5.dp.toPx(),
                center = center
            )

            // 6. Endpoint dots
            drawCircle(
                color = endpointDotColor,
                radius = 6.dp.toPx(),
                center = p1
            )
            drawCircle(
                color = endpointDotColor,
                radius = 6.dp.toPx(),
                center = p2
            )

            // 7. Text labels using native canvas
            val paint = Paint().apply {
                this.color = textColor
                this.textSize = 34f
                this.isAntiAlias = true
                this.isFakeBoldText = true
            }

            // Draw label 'r' on the left radius line midpoint
            val rMidX = (center.x + p1.x) / 2f - 24f
            val rMidY = (center.y + p1.y) / 2f
            drawContext.canvas.nativeCanvas.drawText("r", rMidX, rMidY, paint)

            // Draw label 'c' on chord midpoint
            val cMidX = (p1.x + p2.x) / 2f
            val cMidY = (p1.y + p2.y) / 2f + if (animatedAngle > 180f) -18f else 32f
            paint.textAlign = Paint.Align.CENTER
            drawContext.canvas.nativeCanvas.drawText("c", cMidX, cMidY, paint)

            // Draw label 'θ' in the arc
            val bisectorAngle = baseAngle
            val thetaDist = arcRadius * 1.55f
            val thetaX = (center.x + thetaDist * cos(bisectorAngle)).toFloat()
            val thetaY = (center.y + thetaDist * sin(bisectorAngle)).toFloat()
            drawContext.canvas.nativeCanvas.drawText("θ", thetaX, thetaY, paint)
        }
    }
}
