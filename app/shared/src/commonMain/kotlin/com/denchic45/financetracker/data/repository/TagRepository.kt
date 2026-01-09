package com.denchic45.financetracker.data.repository

import androidx.room.withTransaction
import com.denchic45.financetracker.api.tag.TagApi
import com.denchic45.financetracker.api.tag.model.TagRequest
import com.denchic45.financetracker.api.tag.model.TagResponse
import com.denchic45.financetracker.data.EmptyRequestResult
import com.denchic45.financetracker.data.RequestResult
import com.denchic45.financetracker.data.database.AppDatabase
import com.denchic45.financetracker.data.database.dao.TagDao
import com.denchic45.financetracker.data.database.entity.TagEntity
import com.denchic45.financetracker.data.mapper.toTagEntities
import com.denchic45.financetracker.data.mapper.toTagEntity
import com.denchic45.financetracker.data.mapper.toTagItem
import com.denchic45.financetracker.data.mapper.toTagItems
import com.denchic45.financetracker.data.observeData
import com.denchic45.financetracker.data.safeFetch
import com.denchic45.financetracker.data.safeFetchForEmptyResult
import kotlinx.coroutines.flow.map

class TagRepository(
    private val tagDao: TagDao,
    private val tagApi: TagApi,
    private val database: AppDatabase
) {

    fun observe() = observeData(
        query = tagDao.observeAll().map(List<TagEntity>::toTagItems),
        fetch = {
            tagApi.getList().onRight { responses ->
                database.withTransaction {
                    tagDao.deleteWhereNotIn(responses.map(TagResponse::id))
                    tagDao.upsert(responses.toTagEntities())
                }
            }
        }
    )

    fun findById(tagId: Long) = observeData(
        query = tagDao.observeById(tagId).map { entity ->
            entity?.toTagItem()
        },
        fetch = {
            tagApi.getById(tagId).onRight { upsertTag(it) }
        }
    )

    suspend fun add(request: TagRequest): RequestResult<TagResponse> {
        return safeFetch { tagApi.create(request) }.onRight { response ->
            upsertTag(response)
        }
    }

    suspend fun update(
        tagId: Long,
        request: TagRequest
    ): RequestResult<TagResponse> {
        return safeFetch { tagApi.update(tagId, request) }
            .onRight { response -> upsertTag(response) }
    }

    private suspend fun upsertTag(response: TagResponse) {
        database.withTransaction {
            tagDao.upsert(response.toTagEntity())
        }
    }

    suspend fun remove(tagId: Long): EmptyRequestResult {
        return safeFetchForEmptyResult { tagApi.delete(tagId) }
            .onNone {
                database.withTransaction {
                    tagDao.deleteById(tagId)
                }
            }
    }
}