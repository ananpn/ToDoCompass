package com.ToDoCompass.uiComponents.Lists

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

class NoFlingOnNested() : FlingBehavior {
    override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        return 0f
    }
}

class NoNestedScroll() : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        return Offset.Zero
    }
    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        return available
    }
    
    override suspend fun onPreFling(available: Velocity): Velocity {
        return Velocity.Zero
    }
    
    override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        return available
    }
    
    
    
    
}