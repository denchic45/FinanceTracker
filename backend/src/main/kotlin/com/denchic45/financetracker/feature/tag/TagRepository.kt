package com.denchic45.financetracker.feature.tag

import arrow.core.*
import com.denchic45.financetracker.database.table.TagDao
import com.denchic45.financetracker.error.TagNotFound
import com.denchic45.financetracker.tag.model.TagRequest
import com.denchic45.financetracker.tag.model.TagResponse
import org.jetbrains.exposed.sql.transactions.transaction

class TagRepository() {


    fun add(request: TagRequest): TagResponse = transaction {
        TagDao.new {
            name = request.name
        }.toTagResponse()
    }

    fun findById(tagId: Long): Either<TagNotFound, TagResponse> = transaction {
        TagDao.findById(tagId)?.toTagResponse()?.right() ?: TagNotFound.left()
    }

    fun findAll(): List<TagResponse> = transaction {
        TagDao.all().toTagResponses().toList()
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