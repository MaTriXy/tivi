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

package me.banes.chris.tivi.home.library

import android.arch.lifecycle.MutableLiveData
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.plusAssign
import me.banes.chris.tivi.AppNavigator
import me.banes.chris.tivi.SharedElementHelper
import me.banes.chris.tivi.data.Entry
import me.banes.chris.tivi.data.entities.ListItem
import me.banes.chris.tivi.data.entities.TiviShow
import me.banes.chris.tivi.home.HomeFragmentViewModel
import me.banes.chris.tivi.home.HomeNavigator
import me.banes.chris.tivi.home.library.LibraryViewModel.Section.WATCHED
import me.banes.chris.tivi.home.library.LibraryViewModel.Section.WHATS_NEXT
import me.banes.chris.tivi.tmdb.TmdbManager
import me.banes.chris.tivi.trakt.TraktManager
import me.banes.chris.tivi.trakt.calls.WatchedCall
import me.banes.chris.tivi.util.AppRxSchedulers
import timber.log.Timber
import javax.inject.Inject

class LibraryViewModel @Inject constructor(
        schedulers: AppRxSchedulers,
        private val watchedCall: WatchedCall,
        appNavigator: AppNavigator,
        traktManager: TraktManager,
        tmdbManager: TmdbManager
) : HomeFragmentViewModel(traktManager, appNavigator) {

    data class SectionPage(val section: Section, val items: List<ListItem<out Entry>>)

    enum class Section {
        WHATS_NEXT, WATCHED
    }

    val data = MutableLiveData<LibraryViewState>()

    init {
        disposables += Flowables.combineLatest(
                watchedCall.data(),
                tmdbManager.imageProvider,
                { items, imageUrlProvider ->
                    LibraryViewState(listOf(SectionPage(WATCHED, items.take(20))), imageUrlProvider)
                })
                .observeOn(schedulers.main)
                .subscribe(data::setValue, Timber::e)

        refresh()
    }

    private fun refresh() {
        disposables += watchedCall.refresh(Unit)
                .subscribe(this::onSuccess, this::onRefreshError)
    }

    private fun onSuccess() {
        // TODO nothing really to do here
    }

    private fun onRefreshError(t: Throwable) {
        Timber.e(t, "Error while refreshing")
    }

    fun onSectionHeaderClicked(navigator: HomeNavigator, section: Section, sharedElements: SharedElementHelper) {
        when (section) {
            WATCHED -> navigator.showWatched(sharedElements)
            WHATS_NEXT -> TODO()
        }
    }

    fun onItemPostedClicked(navigator: HomeNavigator, show: TiviShow) {
        navigator.showShowDetails(show, null) // TODO
    }
}
