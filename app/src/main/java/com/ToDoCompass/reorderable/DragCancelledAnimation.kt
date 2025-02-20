/*
 * Copyright 2022 André Claßen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

interface DragCancelledAnimation {
    suspend fun dragCancelled(position: ItemPosition, offset: Offset)
    val position: ItemPosition?
    val offset: Offset
}

class NoDragCancelledAnimation : DragCancelledAnimation {
    override suspend fun dragCancelled(position: ItemPosition, offset: Offset) {}
    override val position: ItemPosition? = null
    override val offset: Offset = Offset.Zero
}

class SpringDragCancelledAnimation(private val stiffness: Float = Spring.StiffnessMedium) : DragCancelledAnimation {
    private val animatable = Animatable(Offset.Zero, Offset.VectorConverter)
    override val offset: Offset
        get() = animatable.value

    override var position by mutableStateOf<ItemPosition?>(null)
        private set

    //Used for cancel drag animation and also reordering animation
    override suspend fun dragCancelled(position: ItemPosition, offset: Offset) {
        this.position = position
        animatable.snapTo(offset)
        animatable.animateTo(
            Offset.Zero,
            spring(stiffness = stiffness, visibilityThreshold = Offset.VisibilityThreshold)
        )
        this.position = null
    }

}

/*/*
 * Copyright 2022 André Claßen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset

interface DragCancelledAnimation {
    suspend fun dragCancelled(position: ItemPosition, offset: Offset)
    suspend fun animateMany(positions : List<AnimationPositions>)
    val position1: ItemPosition?
    val position2: ItemPosition?
    val position3: ItemPosition?
    val offset1: Offset
    val offset2 : Offset
    val offset3 : Offset
}
/*

class NoDragCancelledAnimation : DragCancelledAnimation {
    override suspend fun dragCancelled(position: ItemPosition, offset: Offset) {}
    override suspend fun animateMany(positions : List<AnimationPositions>) {}
    override val position: ItemPosition? = null
    override val offset: Offset = Offset.Zero
}
*/


class SpringDragCancelledAnimation(private val stiffness: Float = Spring.StiffnessMedium) : DragCancelledAnimation {
    private val animatable1 = Animatable(Offset.Zero, Offset.VectorConverter, label = "1")
    private val animatable2 = Animatable(Offset.Zero, Offset.VectorConverter, label = "2")
    private val animatable3 = Animatable(Offset.Zero, Offset.VectorConverter, label = "3")
    override val offset1: Offset
        get() = animatable1.value
    override val offset2: Offset
        get() = animatable2.value
    override val offset3: Offset
        get() = animatable3.value

    override var position1 by mutableStateOf<ItemPosition?>(null)
        private set
    override var position2 by mutableStateOf<ItemPosition?>(null)
        private set
    override var position3 by mutableStateOf<ItemPosition?>(null)
        private set

    private var animationRunning : Int? = null

    override suspend fun dragCancelled(position: ItemPosition, offset: Offset) {
        if (animationRunning==null) {
            Log.v("dragcancelledanim", "dragcancelled null animation 1 starting, index = ${position.index}")
            animationRunning = 1
            this.position1 = position
            animatable1.snapTo(offset)
            animatable1.animateTo(
                Offset.Zero,
                spring(stiffness = stiffness, visibilityThreshold = Offset.VisibilityThreshold)
            )
            this.position1 = null
            animationRunning = null
        }
        if (animationRunning==1) {
            Log.v("dragcancelledanim", "dragcancelled 1 animation 2 starting, index = ${position.index}")
            animationRunning = 2
            this.position2 = position
            animatable2.snapTo(offset)
            animatable2.animateTo(
                Offset.Zero,
                spring(stiffness = stiffness, visibilityThreshold = Offset.VisibilityThreshold)
            )
            this.position2 = null
            animationRunning = null
        }
        if (animationRunning==2) {
            Log.v("dragcancelledanim", "dragcancelled 2 animation 3 starting, index = ${position.index}")
            animationRunning = 3
            this.position3 = position
            animatable3.snapTo(offset)
            animatable3.animateTo(
                Offset.Zero,
                spring(stiffness = stiffness, visibilityThreshold = Offset.VisibilityThreshold)
            )
            this.position3 = null
            animationRunning = null
        }
        if (animationRunning==3) {
            Log.v("dragcancelledanim", "dragcancelled 3 animation 1 starting, index = ${position.index}")
            animationRunning = 1
            this.position1 = position
            animatable1.snapTo(offset)
            animatable1.animateTo(
                Offset.Zero,
                spring(stiffness = stiffness, visibilityThreshold = Offset.VisibilityThreshold)
            )
            this.position1 = null
            animationRunning = null
        }
    }

    override suspend fun animateMany(positions : List<AnimationPositions>) {
        for (_position in positions){
            var position : ItemPosition? = null
            position = _position.itemPosition
            val _animatable = Animatable(Offset.Zero, Offset.VectorConverter)
            //this.position = position.itemPosition
            _animatable.snapTo(_position.offset)
            _animatable.animateTo(
                Offset.Zero,
                spring(stiffness = stiffness, visibilityThreshold = Offset.VisibilityThreshold)
            )
            position = null
        }
    }
}*/