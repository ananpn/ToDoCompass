package com.ToDoCompass.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ToDoCompass.LogicAndData.StringConstants.Companion.ADD_PROFILE_BUTTON
import com.ToDoCompass.LogicAndData.StringConstants.Companion.PROFILES_SETTINGS_TOP
import com.ToDoCompass.ViewModels.MainViewModel
import com.ToDoCompass.uiComponents.Lists.SimpleReorderableList
import com.ToDoCompass.uiComponents.Modals.AddProfileDialog
import com.ToDoCompass.uiComponents.Modals.DeleteDialogSuper
import com.ToDoCompass.uiComponents.Modals.EditDialogSuper
import com.ToDoCompass.uiComponents.smallComponents.AddButton
import com.ToDoCompass.uiComponents.smallComponents.GroupEditGrid
import com.ToDoCompass.uiComponents.smallComponents.GroupManageCard
import com.ToDoCompass.uiComponents.smallComponents.ProfileCard
import kotlinx.coroutines.launch
import rememberReorderableLazyListState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ProfilesScreen(vm : MainViewModel
) {
    val scope = rememberCoroutineScope()

    val profiles = vm.profiles.sortedBy{it.profileOrder}
    /*
        LaunchedEffect(initialRowTapMode){
            rowTapMode.value = initialRowTapMode
            delay(50)
        }
        */
    LaunchedEffect(Unit){
        vm.closeGroupEditCard()
        vm.updateProfilesFromDB()
        vm.checkDefaultNotifTypes()
    }

    Scaffold(
        modifier = Modifier.background(color = Color.Red),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(PROFILES_SETTINGS_TOP)
                }
            )
        },
    ) { innerPadding ->
    Column(
        modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(25.dp))
        val state = rememberReorderableLazyListState(
            onMove = {from, to ->
                vm.switchProfiles(from.index, to.index)
            },
            group = 69,
            onDragEnd = {_first, _second -> scope.launch{
                vm.saveProfilesToDB()
            }
            },
            maxScrollPerFrame = 8.dp
        
        )
        Box(modifier = Modifier.height(260.dp)){
            SimpleReorderableList(
                state = state,
                modifier = Modifier,
                itemsClickable = false,
                items = profiles,
                itemKey = {
                    it -> it.idProfile
                },
                itemOrder = {
                    it -> it.profileOrder
                },
                itemContentBox = {isDragging, item ->
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(20))
                            .height(40.dp)
                            .combinedClickable(
                                onClick = {
                                    //set profile ########################
                                    if (item.idProfile != null) {
                                        vm.setDispProfile(item.idProfile)
                                    }
                                },
                                onDoubleClick = {
                                    vm.openUpdateDialog(entity = item)
                                }
                            )
                            .background(
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                            .shadow(elevation = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Spacer(modifier = Modifier.width(20.dp))
                        ProfileCard(item)
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    
                }
            )
        }
        
        Spacer(modifier = Modifier.height(15.dp))
        AddButton(
            labelText = ADD_PROFILE_BUTTON,
            onClicked = {vm.openNewProfileDia()},
            modifier = Modifier.offset(x = -50.dp)
        )
        Spacer(modifier = Modifier.height(25.dp))
        Box(modifier = Modifier.height(200.dp).padding(horizontal = 20.dp)) {
            GroupEditGrid(
                vm = vm,
            )
        }
        
    }


    }
    AddProfileDialog(
        vm = vm,
    )
    
    EditDialogSuper(
        vm = vm
    )
    
    GroupManageCard(
        vm = vm,
        onDismiss = {
            vm.closeGroupEditCard()
        }
    )
    
    DeleteDialogSuper(
        vm = vm,
        onDelete = {
            vm.closeUpdateDialog()
        }
    )

    /*
    EditProfileDialog(
        vm = vm,
        onDismiss = {scope.launch {
            vm.updateProfilesFromDB()
            vm.closeUpdateDialog()
        }},
        onDelete = {scope.launch{
            vm.updateDataFromDB()
            vm.updateProfilesFromDB()
            delay(20)
            if (vm.uiState.dispProfileId == vm.uiState.upProfile.idProfile){
                vm.setDispProfile(profiles.firstOrNull()?.idProfile ?:1)
            }
            vm.closeUpdateDialog()
        }}
    )
    */

}