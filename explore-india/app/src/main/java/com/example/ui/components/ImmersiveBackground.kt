package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.ImmersiveBackground
import com.example.ui.theme.ImmersivePrimary
import com.example.ui.theme.ImmersivePrimaryContainer

/**
 * Renders the Immersive UI ambient glow background canvas with top-left deep purple glow
 * and bottom-right soft luminous lavender ambient lights.
 */
@Composable
fun ImmersiveBackgroundContainer(
  modifier: Modifier = Modifier,
  content: @Composable BoxScope.() -> Unit
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(ImmersiveBackground)
  ) {
    // Ambient Canvas with soft radial glow gradients
    Canvas(modifier = Modifier.fillMaxSize()) {
      val canvasWidth = size.width
      val canvasHeight = size.height

      // Top-Left Deep Purple Ambient Glow
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            ImmersivePrimaryContainer.copy(alpha = 0.35f),
            ImmersivePrimaryContainer.copy(alpha = 0.15f),
            Color.Transparent
          ),
          center = Offset(x = 0f, y = 0f),
          radius = canvasWidth * 0.9f
        ),
        radius = canvasWidth * 0.9f,
        center = Offset(x = 0f, y = 0f)
      )

      // Bottom-Right Luminous Lavender Glow
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            ImmersivePrimary.copy(alpha = 0.18f),
            ImmersivePrimary.copy(alpha = 0.06f),
            Color.Transparent
          ),
          center = Offset(x = canvasWidth, y = canvasHeight * 0.85f),
          radius = canvasWidth * 0.8f
        ),
        radius = canvasWidth * 0.8f,
        center = Offset(x = canvasWidth, y = canvasHeight * 0.85f)
      )
    }

    // Foreground Screen Content
    content()
  }
}
