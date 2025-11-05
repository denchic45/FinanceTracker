package com.denchic45.financetracker.feature.tag

import com.denchic45.financetracker.database.table.TagDao
import com.denchic45.financetracker.api.tag.model.TagResponse
import org.jetbrains.exposed.sql.SizedIterable

fun TagDao.toTagResponse() = TagResponse(
    id = id.value,
    name = name
)

fun SizedIterable<TagDao>.toTagResponses() = map(TagDao::toTagResponse)