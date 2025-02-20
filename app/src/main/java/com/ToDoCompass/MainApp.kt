package com.ToDoCompass

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ToDoCompass.LogicAndData.transformBGColor
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.ViewModels.UiViewModel
import com.ToDoCompass.navigation.Destinations
import com.ToDoCompass.navigation.NavigationGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(context : Context,
            //sharedPreferences : SharedPreferences,
            viewModel: MainViewModel,
            uiVM : UiViewModel
) {
    val navController: NavHostController = rememberNavController()
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthDp = remember(configuration){with(density) {configuration.screenWidthDp.dp}}
    val screenWidthPx = remember(configuration){with(density) {configuration.screenWidthDp.dp.toPx()}}
    val screenHeightDp = remember(configuration){with(density) {configuration.screenHeightDp.dp}}

    viewModel.setBGColors(
        newDef = MaterialTheme.colorScheme.background,
        newMod = transformBGColor(color = MaterialTheme.colorScheme.background)
    )

    //easiest way to get the last opened group from DataStore
    LaunchedEffect(Unit){
        //viewModel.populateData()
        viewModel.updateDispProfileId()
        //viewModel.setUiState(dispDates = currentWeek)
        viewModel.updateScreenSize(screenWidthDp, screenWidthPx, screenHeightDp)
        viewModel.checkDefaultNotifTypes()
    }

    LaunchedEffect(configuration){
        viewModel.updateScreenSize(screenWidthDp, screenWidthPx, screenHeightDp)
    }
/*
    var dispDates by rememberSaveable{ mutableStateOf(viewModel.uiState.dispDates.map { date -> TimeFunctions.formatToString(date, "yyyyMMdd") }) }
    LaunchedEffect(viewModel.uiState.dispDates) {
        dispDates = viewModel.uiState.dispDates.map { date -> TimeFunctions.formatToString(date, "yyyyMMdd") }
    }
    */
    /*
    val tasks by viewModel.tasks.collectAsStateWithLifecycle(initialValue = listOf())
    if (tasks.isEmpty()){
        viewModel.letNoTasks(true)
        val profiles = viewModel._dbProfiles.collectAsState(initial = listOf())
        if (profiles.value.isEmpty()){
            viewModel.letNoProfiles(true)
        }
        else viewModel.letNoProfiles(false)
    }
    else{
        viewModel.letNoProfiles(false)
        viewModel.letNoTasks(false)
    }
*/

    Scaffold(
        bottomBar = {
            BottomBar(
                navController = navController,
                viewModel = viewModel
            )
        },
        content= { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)){
                NavigationGraph(
                    context = context,
                    //dispDates = dispDates,
                    navController = navController,
                    viewModel = viewModel,
                    uiVM = uiVM,
                )
            }
        }

    )
}


@Composable
fun BottomBar(
    navController: NavHostController, viewModel: MainViewModel
) {
    val screens = listOf(
        Destinations.MainScreenDestination, Destinations.ProfilesDestination, Destinations.SettingsDestination
    )

    NavigationBar(
        modifier = Modifier
            //.horizontalScroll(rememberScrollState())
        ,
        //containerColor = Color.LightGray,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        screens.forEach { screen ->

            NavigationBarItem(
                label = {
                    Text(text = screen.title!!, maxLines = 1)
                },
                icon = {
                    Icon(imageVector = screen.icon.invoke(), contentDescription = "")
                },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id){
                            saveState=true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    unselectedTextColor = Color.Gray, selectedTextColor = Color.White
                ),
            )
        }

    }
}