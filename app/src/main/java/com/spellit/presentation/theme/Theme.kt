package com.spellit.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Shapes
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp

private val ColorWhite = Color.White

private val SpellItColors = lightColorScheme(
    primary = SkyBlue,
    onPrimary = ColorWhite,
    primaryContainer = PaleBlue,
    onPrimaryContainer = DeepBlue,
    secondary = GrassGreen,
    onSecondary = ColorWhite,
    secondaryContainer = PaleGreen,
    onSecondaryContainer = LeafGreen,
    tertiary = GrapePurple,
    onTertiary = ColorWhite,
    tertiaryContainer = PalePurple,
    onTertiaryContainer = GrapePurple,
    background = Parchment,
    onBackground = Ink,
    surface = ColorWhite,
    onSurface = Ink,
    surfaceVariant = PaleYellow,
    onSurfaceVariant = Ink,
    error = CoralRed,
    errorContainer = PalePink,
    onErrorContainer = CoralRed
)

private val SpellItTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 48.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 40.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
)

private val SpellItShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = CircleShape
)

@Composable
fun SpellItTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SpellItColors,
        typography = SpellItTypography,
        shapes = SpellItShapes
    ) {
        content()
    }
}