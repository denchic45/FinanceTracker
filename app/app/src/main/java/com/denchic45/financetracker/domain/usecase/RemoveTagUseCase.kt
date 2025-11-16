package com.denchic45.financetracker.domain.usecase

import com.denchic45.financetracker.data.EmptyRequestResult
import com.denchic45.financetracker.data.repository.TagRepository

class RemoveTagUseCase(private val tagRepository: TagRepository) {
    suspend operator fun invoke(tagId: Long): EmptyRequestResult {
        return tagRepository.remove(tagId)
    }
}