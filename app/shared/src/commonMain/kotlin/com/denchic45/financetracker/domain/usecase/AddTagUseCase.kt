package com.denchic45.financetracker.domain.usecase

import com.denchic45.financetracker.api.tag.model.TagRequest
import com.denchic45.financetracker.api.tag.model.TagResponse
import com.denchic45.financetracker.data.RequestResult
import com.denchic45.financetracker.data.repository.TagRepository

class AddTagUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(request: TagRequest): RequestResult<TagResponse> {
        return tagRepository.add(request)
    }
}