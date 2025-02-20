package com.ToDoCompass.uiComponents.smallComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun OverFlowIcon(modifier : Modifier = Modifier, directionUp : Boolean = false) {
    Box(modifier = modifier) {
        Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha =0.4f),
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(color = MaterialTheme.colorScheme.outline.copy(alpha=0.1f))
                .graphicsLayer {
                    when (directionUp){
                        true -> rotationX=180F
                        false -> null
                    }
                }
        )
    }
}