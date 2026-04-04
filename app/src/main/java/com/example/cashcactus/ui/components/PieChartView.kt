
package com.example.cashcactus.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun PieChartView(data: Map<String, Float>) {

    val total = data.values.sum()
    var startAngle = 0f

    val colors = listOf(
        Color(0xFF4CAF50),
        Color(0xFFFF5722),
        Color(0xFF2196F3),
        Color(0xFFFFC107)
    )

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    ) {
        data.entries.forEachIndexed { index, entry ->

            val sweepAngle = (entry.value / total) * 360f

            drawArc(
                color = colors[index % colors.size],
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = true
            )

            startAngle += sweepAngle
        }
    }
}

