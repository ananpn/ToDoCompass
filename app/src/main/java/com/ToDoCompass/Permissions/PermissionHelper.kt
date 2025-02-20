package com.ToDoCompass.Permissions

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ToDoCompass.LogicAndData.Constants.Companion.basicPermissions
import javax.inject.Singleton
import kotlin.random.Random


class PermissionHelper(context: Context)  {
    val context = context
    private val rqCode: Int = (System.currentTimeMillis()%1000).toInt()

    fun requestPermissions(activity : Activity) {
        Log.v("PermissionHelper requestPermissions", "launch")
        
        val permissions = basicPermissions
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                Log.v("PermissionHelper requestPermissions", "not granted permission = $permission")
                ActivityCompat.requestPermissions(
                    activity,
                    permissions,
                    rqCode
                )
            }
        }
    }

    suspend fun checkAndRequestPermissions()  {
        var deniedPermissions: String = ""
        val permissions = basicPermissions
        for (permission in permissions){
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions += permission
        }}
    }

    suspend fun checkPermissions() : Boolean {
        var deniedPermissions: String = ""
        for (permission in basicPermissions){
            if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_DENIED) {
                deniedPermissions += permission
            }
        }
        return deniedPermissions == ""
    }

    suspend fun checkNormalPermissions() : Boolean {
        var deniedPermissions: String = ""
        val permissions = basicPermissions
        for (permission in permissions){
            if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED) {
                deniedPermissions += permission
            }
        }
        return deniedPermissions == ""
    }
}


fun initializePermissionHelper(
    context : Context,
    activity : Activity
) {
    Log.v("initializePermissionHelper", "launch")
    val permissionHelper = PermissionHelper(context)
    permissionHelper.requestPermissions(activity)
    
}

suspend fun obtainBackGroundPermissions(
    context : Context,
    activity : Activity
) {
    val permissionHelper = PermissionHelper(context)
}

suspend fun checkPermissions(
    context : Context,
    activity : Activity,
    requireLocation : Boolean
) : Boolean {
    val permissionHelper = PermissionHelper(context)
    return permissionHelper.checkPermissions()
}

suspend fun checkNormalPermissions(
    context : Context,
    activity : Activity,
    requireLocation : Boolean
) : Boolean {
    val permissionHelper = PermissionHelper(context)
    return permissionHelper.checkNormalPermissions()
}
