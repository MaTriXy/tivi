/*
 * Copyright 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.banes.chris.tivi.ui.epoxymodels

import android.view.View
import com.airbnb.epoxy.EpoxyHolder
import kotlinx.android.extensions.LayoutContainer

class TiviEpoxyHolder : EpoxyHolder(), LayoutContainer {
    private var itemView: View? = null

    override val containerView: View?
        get() = itemView

    override fun bindView(itemView: View?) {
        this.itemView = itemView
    }
}
