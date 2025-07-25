package com.vikaspogu.logit.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.vikaspogu.logit.R


val fontName = GoogleFont("Lato")

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs,
)
val fontFamily = FontFamily(
    Font(googleFont = fontName, fontProvider = provider),
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
    ),
)


val TitleBarStyleLarge = TextStyle(
    fontSize = 50.sp,
    fontWeight = FontWeight(700),
    letterSpacing = 0.5.sp,
    fontFamily = fontFamily,
)

val TitleBarStyle = TextStyle(
    fontSize = 22.sp,
    fontWeight = FontWeight(700),
    letterSpacing = 0.5.sp,
    fontFamily = fontFamily,
)

val HeadingStyle = TextStyle(
    fontSize = 24.sp,
    fontWeight = FontWeight(600),
    letterSpacing = 0.5.sp,
    fontFamily = fontFamily,
)

val SmallHeadingStyle = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight(600),
    letterSpacing = 0.5.sp,
    fontFamily = fontFamily,
)

val LegendHeadingStyle = TextStyle(
    fontSize = 10.sp,
    fontWeight = FontWeight(600),
    letterSpacing = 0.5.sp,
    fontFamily = fontFamily,
)

val LegendHeadingMediumStyle = TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight(600),
    letterSpacing = 0.5.sp,
    fontFamily = fontFamily,
)

val TitleStyle = TextStyle(
    fontSize = 36.sp,
    fontWeight = FontWeight(500),
    letterSpacing = 0.5.sp,
    fontFamily = fontFamily,
)