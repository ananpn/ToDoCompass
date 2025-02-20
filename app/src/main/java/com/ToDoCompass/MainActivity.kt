package com.ToDoCompass

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.lifecycleScope
import com.ToDoCompass.LogicAndData.Constants.Companion.appDarkThemeGetDelay
import com.ToDoCompass.LogicAndData.Constants.Companion.appInitializedDelay
import com.ToDoCompass.LogicAndData.transformIntToPaletteStyle
import com.ToDoCompass.Notifications.BootReceiver
import com.ToDoCompass.Permissions.initializePermissionHelper
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.ViewModels.UiViewModel
import com.ToDoCompass.di.PrefsImpl
import com.ToDoCompass.ui.theme.AppMainTheme
import com.materialkolor.PaletteStyle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    val context : Context = this
    @Inject
    lateinit var prefsManager: PrefsImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: MainViewModel = hiltViewModel()
            val uiVM : UiViewModel = hiltViewModel()
            /*
            LaunchedEffect(Unit){
                viewModel.darkTheme.collect {
                    val mode = when (it) {
                        true -> AppCompatDelegate.MODE_NIGHT_YES
                        false -> AppCompatDelegate.MODE_NIGHT_NO
                    }
                    AppCompatDelegate.setDefaultNightMode(mode)
                }
            }
            */
            lifecycleScope.launchWhenStarted {

            }

/*
            var palette by remember{mutableStateOf(PaletteStyle.TonalSpot)}
            palette = transformIntToPaletteStyle(viewModel.paletteIn.collectAsState(initial = 0).value)
            */
            var initialized by rememberSaveable{ mutableStateOf(false) }
            //var darkTheme by rememberSaveable{ mutableStateOf(false) }

            //to stop flashing at start
            Surface(
                modifier = Modifier
                    .fillMaxSize(),
                color = Color.DarkGray
            ){}
            LaunchedEffect(Unit){
                delay(appInitializedDelay)
                initialized=true
                //delay(appDarkThemeGetDelay)
                //darkTheme = viewModel.darkThemeGet()
                initializePermissionHelper(context, this@MainActivity)
            }

            LaunchedEffect(Unit){
                if (false) {
                    //enable bootreceivre
                    val receiver = ComponentName(context, BootReceiver::class.java)
                    context.packageManager.setComponentEnabledSetting(
                        receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )

                    //disable bootreceiver
                    val receiver2 = ComponentName(context, BootReceiver::class.java)

                    context.packageManager.setComponentEnabledSetting(
                        receiver2,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP
                    )
                }

            }

            AppMainTheme(
                viewModel = viewModel,
                //darkTheme = darkTheme
            )
            {
                AnimatedVisibility(
                    visible = initialized,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 200,
                            delayMillis = 0,
                            easing = EaseIn
                        )
                    ),
                    exit = fadeOut(
                        animationSpec = tween(
                            durationMillis = 100,
                            delayMillis = 0,
                            easing = EaseIn
                        )
                    )
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize()
                    ) {

                        MainApp(
                            context = context,
                            viewModel = viewModel,
                            uiVM = uiVM,
                        )
                    }
                }
            }
        }
    }

/*

    private fun observeThemeMode() {
        lifecycleScope.launchWhenStarted {
            viewModel.darkTheme.collect {
                val mode = when (it) {
                    true -> AppCompatDelegate.MODE_NIGHT_YES
                    false -> AppCompatDelegate.MODE_NIGHT_NO
                }
                AppCompatDelegate.setDefaultNightMode(mode)
            }
        }
    }
*/

    //how to set Theme from AppCompatDelegate..... defaultNightMode???
}




/*

@Composable
fun NavigationGraph(navController: NavHostController, taskViewModel : TaskViewModel) {
    NavHost(navController, startDestination = Destinations.TaskScreen.route) {
        composable(Destinations.TaskScreen.route) {
            TaskScreen(taskViewModel)
        }
        composable(Destinations.Favourite.route) {
            FavouriteScreen()
        }
        composable(Destinations.Notification.route) {
            NotificationScreen()
        }
        composable(Destinations.AddNew.route) {
        }
    }
}*/
