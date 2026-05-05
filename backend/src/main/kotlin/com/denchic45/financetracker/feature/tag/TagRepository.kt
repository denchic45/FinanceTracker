package com.denchic45.financetracker.feature.tag

import arrow.core.*
import com.denchic45.financetracker.api.error.TagNotFound
import com.denchic45.financetracker.api.tag.model.TagRequest
import com.denchic45.financetracker.api.tag.model.TagResponse
import com.denchic45.financetracker.database.table.TagDao
import com.denchic45.financetracker.database.table.Tags
import com.denchic45.financetracker.database.table.UserDao
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.uuid.Uuid

class TagRepository {

    fun add(request: TagRequest, ownerId: Uuid): TagResponse = transaction {
        TagDao.new {
            name = request.name
            owner = UserDao[ownerId]
        }.toTagResponse()
    }

    fun findById(tagId: Long): Either<TagNotFound, TagResponse> = transaction {
        TagDao.findById(tagId)?.toTagResponse()?.right() ?: TagNotFound.left()
    }

    fun findAll(ownerId: Uuid): List<TagResponse> = transaction {
        TagDao.find(Tags.ownerId eq ownerId).toTagResponses().toList()
    }

    fun update(tagId: Long, request: TagRequest): Either<TagNotFound, TagResponse> = transaction {
        TagDao.findById(tagId)?.apply {
            name = request.name
        }?.toTagResponse()?.right() ?: TagNotFound.left()
    }

    fun remove(tagId: Long): Option<TagNotFound> = transaction {
        TagDao.findById(tagId)?.delete() ?: return@transaction TagNotFound.some()
        none()
    }
}