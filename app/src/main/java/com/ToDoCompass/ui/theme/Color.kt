package com.ToDoCompass.ui.theme

import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val primaryLight = Color(0xFF94443F)
val secondaryLight = Color(0xFF757173)
val tertiaryLight = Color(0xFF8D4731)

val primaryDark = Color(0xFF94443F)
val secondaryDark = Color(0xFF757173)
val tertiaryDark = Color(0xFF8D4731)


val textBoxBackGround = Color(0xFF4D4D4D)
val currentDateBG = Color(0xFFFFEAEA)
val defaultBG = Color(0xFFF5FFF5)
val BackGroundCol = Color(0xFFFFFFFF)


val buttonBorderColor = Color(0xFFE7BBBB)

val deleteButtonContainerColor = Color(0xFF525252)
val deleteButtonContentColor = Color(0xFFFDF4F4)
val deleteButtonDisabledContainerColor = Color(0xFF2A2E2A)
val deleteButtonDisabledContentColor = Color(0xFFFFFFFF)

val textButtonContainerColor = Color(0xFF743838)
val textButtonContentColor = Color(0xFFFDF4F4)
val textButtonDisabledContainerColor = Color(0xFF574F4F)
val textButtonDisabledContentColor = Color(0xFFFFFFFF)



/*
val sliderColors = SliderColors(
    thumbColor = Color(0xFF625b71)
)*/

@Composable
fun getDeleteButtonColors() : ButtonColors{

    //if (activateDelete) return  ButtonDefaults.filledTonalButtonColors()
    return ButtonDefaults.filledTonalButtonColors(
        //containerColor = MaterialTheme.colorScheme.
    )
/*

    FilledTonalButtonTokens
        .DisabledContainerColor
        .toColor()
        .copy(alpha = FilledTonalButtonTokens.DisabledContainerOpacity)
    val disabledContentColor: Color = FilledTonalButtonTokens
        .DisabledLabelTextColor
        .toColor()
        .copy(alpha = FilledTonalButtonTokens.DisabledLabelTextOpacity)
*/

/*
    return  ButtonDefaults.buttonColors(
        containerColor = deleteButtonContainerColor,
        contentColor = deleteButtonContentColor,
        disabledContainerColor = deleteButtonDisabledContainerColor,
        disabledContentColor =  deleteButtonDisabledContentColor
    )
    */
}

@Composable
fun getTextButtonColors() : ButtonColors{
    return  ButtonDefaults.filledTonalButtonColors()
    /*
    return  ButtonDefaults.buttonColors(
        containerColor = textButtonContainerColor,
        contentColor = textButtonContentColor,
        disabledContainerColor = textButtonDisabledContainerColor,
        disabledContentColor =  textButtonDisabledContentColor
    )
    */
}

@Composable
fun getTransparentButtonColors() : ButtonColors{
    return  ButtonDefaults.filledTonalButtonColors(
        Color.Transparent,
        Color.Transparent,
        Color.Transparent,
        Color.Transparent,
    )
    /*
    return  ButtonDefaults.buttonColors(
        containerColor = textButtonContainerColor,
        contentColor = textButtonContentColor,
        disabledContainerColor = textButtonDisabledContainerColor,
        disabledContentColor =  textButtonDisabledContentColor
    )
    */
}

@Composable
fun getOffsetColor() : Color{
    return Color(
        alpha = 0.35f,
        red = 0.6f,
        green = 0.6f,
        blue = 0.64f
    )
}

@Composable
fun getBasicButtonColors() : ButtonColors{
    return  ButtonDefaults.filledTonalButtonColors(
        containerColor = getOffsetColor()
    )
    /*
    return  ButtonDefaults.buttonColors(
        containerColor = textButtonContainerColor,
        contentColor = textButtonContentColor,
        disabledContainerColor = textButtonDisabledContainerColor,
        disabledContentColor =  textButtonDisabledContentColor
    )
    */
}




//more colors*******

val dark_primary = Color(0xFFFFB951)
val dark_onPrimary = Color(0xFF452B00)
val dark_primaryContainer = Color(0xFF633F00)
val dark_onPrimaryContainer = Color(0xFFFFDDB3)
val dark_secondary = Color(0xFFDDC2A1)
val dark_onSecondary = Color(0xFF3E2D16)
val dark_secondaryContainer = Color(0xFF56442A)
val dark_onSecondaryContainer = Color(0xFFFBDEBC)
val dark_tertiary = Color(0xFFB8CEA1)
val dark_onTertiary = Color(0xFF243515)
val dark_tertiaryContainer = Color(0xFF3A4C2A)
val dark_onTertiaryContainer = Color(0xFFD4EABB)
val dark_error = Color(0xFFFFB4AB)
val dark_errorContainer = Color(0xFF93000A)
val dark_onError = Color(0xFF690005)
val dark_onErrorContainer = Color(0xFFFFDAD6)
val dark_background = Color(0xFF1F1B16)
val dark_onBackground = Color(0xFFEAE1D9)
val dark_surface = Color(0xFF1F1B16)
val dark_onSurface = Color(0xFFEAE1D9)
val dark_surfaceVariant = Color(0xFF4F4539)
val dark_onSurfaceVariant = Color(0xFFD3C4B4)
val dark_outline = Color(0xFF9C8F80)
val dark_inverseOnSurface = Color(0xFF1F1B16)
val dark_inverseSurface = Color(0xFFEAE1D9)
val dark_inversePrimary = Color(0xFF825500)
val dark_shadow = Color(0xFF000000)
val dark_surfaceTint = Color(0xFFFFB951)
val dark_outlineVariant = Color(0xFF4F4539)
val dark_scrim = Color(0xFF000000)


val seed = Color(0xFF821E00)

