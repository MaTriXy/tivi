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

package me.banes.chris.tivi.calls

import com.uwetrottmann.trakt5.TraktV2
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import me.banes.chris.tivi.util.AppRxSchedulers
import me.banes.chris.tivi.util.DatabaseTxRunner

abstract class TraktCall<Input, Output, out DaoType>(
        protected val databaseTxRunner: DatabaseTxRunner,
        protected val dao: DaoType,
        protected val trakt: TraktV2,
        protected val schedulers: AppRxSchedulers) {

    fun observable(): Flowable<Output> {
        return createDatabaseObservable()
                .subscribeOn(schedulers.disk)
    }

    fun refresh(): Completable {
        return load().toCompletable()
    }

    private fun load(): Single<Output> {
        return networkCall()
                .subscribeOn(schedulers.network)
                .filter { filterResponse(it) }
                .flatMap { mapToOutput(it) }
                .observeOn(schedulers.disk)
                .doOnSuccess { saveEntry(it) }
                .toSingle()
    }

    protected abstract fun networkCall(): Single<Input>

    protected open fun filterResponse(response: Input): Boolean {
        return true
    }

    protected abstract fun createDatabaseObservable(): Flowable<Output>

    protected abstract fun mapToOutput(input: Input): Maybe<Output>

    protected abstract fun saveEntry(show: Output)

}