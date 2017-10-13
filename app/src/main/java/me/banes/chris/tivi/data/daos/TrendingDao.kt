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

package me.banes.chris.tivi.data.daos

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import io.reactivex.Flowable
import io.reactivex.Single
import me.banes.chris.tivi.data.TiviDatabase
import me.banes.chris.tivi.data.entities.TrendingEntry

@Dao
abstract class TrendingDao(db: TiviDatabase) : PaginatedEntryDao<TrendingEntry>(db.showDao()) {
    @Query("SELECT * FROM trending_shows ORDER BY page ASC, watchers DESC")
    abstract override fun entriesImpl(): Flowable<List<TrendingEntry>>

    @Query("SELECT * FROM trending_shows WHERE page = :page ORDER BY watchers DESC")
    abstract override fun entriesPageImpl(page: Int): Flowable<List<TrendingEntry>>

    @Query("DELETE FROM trending_shows WHERE page = :page")
    abstract override fun deletePage(page: Int)

    @Query("DELETE FROM trending_shows")
    abstract override fun deleteAll()

    @Query("SELECT MAX(page) from trending_shows")
    abstract override fun getLastPage(): Single<Int>
}