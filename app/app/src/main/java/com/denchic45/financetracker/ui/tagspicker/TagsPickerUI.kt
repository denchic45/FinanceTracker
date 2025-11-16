package com.denchic45.financetracker.ui.tagspicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.domain.model.TagItem
import com.denchic45.financetracker.ui.NoDataContent
import com.denchic45.financetracker.ui.resource.CacheableResourceListContent
import com.denchic45.financetracker.ui.resource.CircularLoadingBox
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsPickerSheet(
    selectedTagIds: List<Long>,
    viewModel: TagsPickerViewModel = koinViewModel {
        parametersOf("selectedTagIds", selectedTagIds)
    }
) {
    val tags by viewModel.tags.collectAsState()
    val selected = viewModel.selectedTags
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = viewModel::onDismissClick, sheetState = sheetState) {

        CacheableResourceListContent(
            resource = tags,
            loadingContent = { CircularLoadingBox(Modifier.height(160.dp))},
            dataContent = { tags ->
                Column {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        tags.forEach { tag ->
                            TagChip(
                                tag = tag,
                                selected = tag in selected,
                                onClick = { viewModel.onTagSelect(tag) }
                            )
                        }
                    }

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onClick = viewModel::onDoneClick
                    ) {
                        Text("Сохранить")
                    }
                }
            },
            emptyDataContent = {
                NoDataContent(
                    modifier = Modifier.height(160.dp),
                    title = { Text(text = "У вас нет ни одного тега") },
                    action = {
                        Button(onClick = viewModel::onCreateTagClick) {
                            Text("Создать тег")
                        }
                    }
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagChip(
    tag: TagItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(tag.name) },
        leadingIcon = if (selected) {
            {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else null,
        modifier = modifier
    )
}