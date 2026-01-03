package com.denchic45.financetracker.domain.usecase

import arrow.core.Ior
import com.denchic45.financetracker.data.Failure
import com.denchic45.financetracker.data.repository.TagRepository
import com.denchic45.financetracker.domain.model.TagItem
import kotlinx.coroutines.flow.Flow


class ObserveTagsUseCase(private val tagRepository: TagRepository) {
    operator fun invoke(): Flow<Ior<Failure, List<TagItem>>> {
        return tagRepository.observe()
    }
}