package com.spellit.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import com.spellit.presentation.theme.BrightOrange
import com.spellit.presentation.theme.BubblePink
import com.spellit.presentation.theme.GrassGreen
import com.spellit.presentation.theme.GrapePurple
import com.spellit.presentation.theme.SkyBlue
import com.spellit.presentation.theme.SunYellow
import kotlinx.coroutines.delay
import kotlin.random.Random

private const val GRAVITY = 700f

private data class Confetto(
    val x: Float,
    val y: Float,
    val velocityX: Float,
    val velocityY: Float,
    val color: Color,
    val size: Float
)

private val confettiColors = listOf(SunYellow, SkyBlue, GrassGreen, BubblePink, GrapePurple, BrightOrange)

@Composable
fun ConfettiBurst(
    modifier: Modifier = Modifier,
    particleCount: Int = 90,
    durationMillis: Long = 2600L,
    play: Boolean = true
) {
    val particles = remember { mutableStateOf(emptyList<Confetto>()) }
    var running by remember { mutableStateOf(false) }
    var size by remember { mutableStateOf(IntSize.Zero) }

    LaunchedEffect(play, size) {
        if (!play) {
            running = false
            particles.value = emptyList()
            return@LaunchedEffect
        }
        if (size.width == 0 || size.height == 0) return@LaunchedEffect
        running = true
        val rand = Random(System.nanoTime())
        val startMillis = System.currentTimeMillis()
        particles.value = List(particleCount) {
            Confetto(
                x = size.width / 2f,
                y = size.height * 0.35f,
                velocityX = rand.nextFloat() * 1600f - 800f,
                velocityY = rand.nextFloat() * -1800f - 400f,
                color = confettiColors.random(rand),
                size = rand.nextFloat() * 14f + 6f
            )
        }
        while (running && System.currentTimeMillis() - startMillis < durationMillis) {
            delay(16L)
            val elapsed = (System.currentTimeMillis() - startMillis) / 1000f
            particles.value = particles.value.map { p ->
                p.copy(
                    x = p.x + p.velocityX * elapsed,
                    y = p.y + p.velocityY * elapsed + 0.5f * GRAVITY * elapsed * elapsed
                )
            }.filter { it.y < size.height + 60f && it.x > -60f && it.x < size.width + 60f }
        }
        running = false
    }

    Canvas(
        modifier = modifier.onSizeChanged { size = it }
    ) {
        if (!running) return@Canvas
        particles.value.forEach { p ->
            drawRect(
                color = p.color,
                topLeft = Offset(p.x - p.size / 2f, p.y - p.size / 2f),
                size = Size(p.size, p.size / 1.6f)
            )
        }
    }
}