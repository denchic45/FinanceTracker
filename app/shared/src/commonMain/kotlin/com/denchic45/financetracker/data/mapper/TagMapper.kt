package com.denchic45.financetracker.data.mapper

import com.denchic45.financetracker.api.tag.model.TagResponse
import com.denchic45.financetracker.data.database.entity.TagEntity
import com.denchic45.financetracker.domain.model.TagItem


fun List<TagResponse>.toTagEntities() = map { response ->
    response.toTagEntity()
}

fun TagResponse.toTagEntity() = TagEntity(
    id = id,
    name = name
)

fun TagEntity.toTagResponse() = TagResponse(
    id = id,
    name = name
)

fun List<TagEntity>.toTagResponses() = map(TagEntity::toTagResponse)


fun TagEntity.toTagItem() = TagItem(
    id = id,
    name = name
)

fun List<TagEntity>.toTagItems() = map(TagEntity::toTagItem)