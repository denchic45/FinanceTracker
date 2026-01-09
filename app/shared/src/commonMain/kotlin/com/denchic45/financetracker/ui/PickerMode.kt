package com.denchic45.financetracker.ui

import java.util.UUID

sealed interface PickerMode {
    data class Single(val initialId: UUID?) : PickerMode
    data class Multiple(val initialIds: Set<UUID>) : PickerMode
}