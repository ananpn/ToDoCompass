package com.ToDoCompass.uiComponents.smallComponents

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ToDoCompass.LogicAndData.StringConstants.Companion.ADD_BUTTON
import com.ToDoCompass.ui.theme.getBasicButtonColors
import com.ToDoCompass.ui.theme.getTextButtonColors

@Composable
fun AddButton(
    modifier : Modifier = Modifier,
    icon : @Composable () -> Unit = {Icon(Icons.Outlined.Add, contentDescription = "")},
    labelText : String = ADD_BUTTON,
    onClicked : () -> Unit,
){
    val defaultModifier = Modifier
        .height(50.dp)
        .fillMaxWidth(0.4f)
    Button(
        modifier = defaultModifier.then(modifier),
        onClick = { onClicked() },
        colors = getTextButtonColors()
    )
    {
        icon()
        Text(labelText, maxLines = 1, overflow = TextOverflow.Visible)
    }
}

@Composable
fun AddButtonIcon(
              onClicked : () -> Unit,
              modifier : Modifier = Modifier
){
    Icon(Icons.Filled.AddCircle, contentDescription = "",
        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
        modifier = modifier
            .size(40.dp)
            .clip(shape = CircleShape)
            .clickable(onClick = {
                onClicked()
            })
    )
}

@Composable
fun DeleteButton(
    modifier : Modifier = Modifier,
    onDeletePressed: () -> Unit)
{
    SecureButton(
        onButtonPressed ={
            onDeletePressed()
        },
        modifier = modifier.width(56.dp),
        contentPadding = PaddingValues(5.dp),
        shape = RoundedCornerShape(corner = CornerSize(15)),
        colors =  getBasicButtonColors(),
        buttonContent = {
            Icon(
                Icons.Outlined.Delete,
                contentDescription ="",
                modifier = Modifier
                    .size(20.dp)
                    .offset(0.dp, 0.dp)
            )
        }
    )
}

@Composable
fun CloseButton(
    modifier : Modifier = Modifier,
    onClose: () -> Unit)
{
    val width = 56.dp


    Button(modifier = modifier.width(50.dp),
        contentPadding = PaddingValues(5.dp),
        shape = RoundedCornerShape(corner = CornerSize(15)),
        colors = getBasicButtonColors(),
        onClick = {
            onClose()
        }
    ) {
        Icon(
            Icons.Outlined.Close,
            contentDescription ="",
            modifier = Modifier
                .size(20.dp)
                .offset(0.dp, 0.dp)
        )
    }
}


@Composable
fun SecureButton(
    modifier : Modifier = Modifier,
    onButtonPressed: () -> Unit,
    buttonContent: @Composable () -> Unit,
    shape : Shape = ButtonDefaults.shape,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
)
{
    // Boolean to track whether the button is activated
    var isButtonActivated by remember { mutableStateOf(false) }
    
    val animationFloat by animateFloatAsState(
        targetValue = if (isButtonActivated) 1f else 0f,
        animationSpec = tween(durationMillis = 500)
    )
    val baseColors = remember{ colors}
    
    Button(
        modifier = modifier,
        onClick = {
            if (isButtonActivated) {
                onButtonPressed() // Execute action on the second press
            } else {
                isButtonActivated = true // Activate the button
            }
        },
        enabled = enabled, // Button is always clickable
        shape = shape,
        colors = animatedButtonColors(animationFloat, baseColors),
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    ) {
        buttonContent()
    }
}


private operator fun Float.times(color: Color): Color {
    return color.copy(
        alpha = color.alpha*this,
        red = color.red*this,
        blue = color.blue*this,
        green = color.green*this,
    )
}

private operator fun Color.plus(color: Color): Color {
    return this.copy(
        alpha = (this.alpha+color.alpha).coerceAtMost(1f),
        red = (this.red+color.red).coerceAtMost(1f),
        blue = (this.blue+color.blue).coerceAtMost(1f),
        green = (this.green+color.green).coerceAtMost(1f),
    )
}

@Composable
fun animatedButtonColors(
    animationFloat : Float,
    baseButtonColors : ButtonColors
) : ButtonColors{
    val disabledContentColor = baseButtonColors.disabledContentColor
    val disabledContainerColor = baseButtonColors.disabledContainerColor
    val finalContentColor = baseButtonColors.contentColor
    val finalContainerColor = baseButtonColors.containerColor
    val contentColor = (1-animationFloat)*disabledContentColor+animationFloat*finalContentColor
    val containerColor = (1-animationFloat)*disabledContainerColor+animationFloat*finalContainerColor
        
    return ButtonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = baseButtonColors.disabledContainerColor,
        disabledContentColor = baseButtonColors.disabledContainerColor
    )
}