package com.ToDoCompass.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.ToDoCompass.R

sealed class Destinations(
    val route: String,
    val title: String? = null,
    val icon: @Composable () -> ImageVector
) {
    object MainScreenDestination : Destinations(
        route = "main_screen",
        title = "Main",
        icon = { ImageVector.vectorResource(R.drawable.sharp_window_24) }
    )

    /*
    object TotalsDestination : Destinations(
        route = "totals_screen",
        title = "Summary",
        icon = Icons.Outlined.CheckCircle
    )
    */

    object ProfilesDestination : Destinations(
        route = "profiles_screen",
        title = "Groups",
        icon = {Icons.Outlined.List}
    )

    object SettingsDestination : Destinations(
        route = "settings_screen",
        title = "Settings",
        icon = {Icons.Outlined.Settings}
    )

}

