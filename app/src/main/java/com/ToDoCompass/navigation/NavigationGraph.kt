package com.ToDoCompass.navigation

import android.content.Context
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.EaseOutSine
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ToDoCompass.LogicAndData.Constants.Companion.navigationGraphEnterDelay
import com.ToDoCompass.LogicAndData.Constants.Companion.navigationGraphEnterDur
import com.ToDoCompass.LogicAndData.Constants.Companion.navigationGraphExitDur
import com.ToDoCompass.LogicAndData.Constants.Companion.settingsEnterDur
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.ViewModels.UiViewModel
import com.ToDoCompass.screens.ProfilesScreen
import com.ToDoCompass.screens.SettingsScreen
import com.ToDoCompass.screens.utils.mainScreen


@Composable
fun NavigationGraph(context : Context,
                    navController: NavHostController,
                    //dispDates : List<String>,
                    //sharedPreferences : SharedPreferences,
                    modifier: Modifier = Modifier,
                    viewModel: MainViewModel = hiltViewModel(),
                    uiVM : UiViewModel,
                    
){
    NavHost(
        navController,
        startDestination = Destinations.MainScreenDestination.route,
        enterTransition = { fadeIn(
            animationSpec = tween(
                durationMillis = navigationGraphEnterDur,
                delayMillis = navigationGraphEnterDelay,
                easing = EaseInOut
            )
        ) },
        exitTransition = { fadeOut(
            animationSpec = tween(
                durationMillis = navigationGraphExitDur,
                delayMillis = 0,
                easing = EaseIn
            )
        ) }
    ) {
        composable(
            Destinations.MainScreenDestination.route,
        ) {
            mainScreen(
                vm = viewModel,
                //dispDates = dispDates,
                uiVM = uiVM
            )
        }
        /*
        composable(
            Destinations.TotalsDestination.route,
        ) {
            TotalsScreen(
                viewModel = viewModel,
                tasks = tasks,
                dispDates = dispDates
                //sharedPreferences = sharedPreferences
            )
        }
        */
        composable(
            Destinations.ProfilesDestination.route,
            enterTransition = {
                fadeIn(
                    animationSpec = tween(settingsEnterDur, easing = EaseOutSine)
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = 0,
                        easing = EaseOutSine)
                )
            },
        ){
            ProfilesScreen(viewModel)
        }
        composable(
            Destinations.SettingsDestination.route,
            enterTransition = {
                slideIntoContainer(
                    animationSpec = tween(settingsEnterDur, easing = EaseOutSine),
                    towards = AnimatedContentTransitionScope.SlideDirection.Down
                )
            },
            exitTransition = {
                fadeOut(
                    animationSpec = tween(
                        durationMillis = 300,
                        delayMillis = 0,
                        easing = EaseOutSine)
                )
            },
        ) {
            SettingsScreen(
                vm = viewModel,
                context = context,
            )
        }

    }
}