package com.denchic45.financetracker.ui.tagspicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.check
import financetracker_app.shared.generated.resources.common_done
import financetracker_app.shared.generated.resources.tag_add
import financetracker_app.shared.generated.resources.tag_list_empty
import financetracker_app.shared.generated.resources.tags_pick_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsPickerSheet(
    selectedTagIds: Set<Long>,
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
            loadingContent = { CircularLoadingBox(Modifier.height(160.dp)) },
            dataContent = { tags ->
                Column {
                    Text(
                        stringResource(Res.string.tags_pick_title),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    FlowRow(
                        modifier = Modifier.padding(16.dp),
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

                    FilledTonalButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        onClick = viewModel::onDoneClick
                    ) {
                        Text(stringResource(Res.string.common_done))
                    }
                }
            },
            emptyDataContent = {
                NoDataContent(
                    modifier = Modifier.height(160.dp),
                    title = { Text(text = stringResource(Res.string.tag_list_empty)) },
                    action = {
                        Button(onClick = viewModel::onCreateTagClick) {
                            Text(stringResource(Res.string.tag_add))
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
                    painter = painterResource(Res.drawable.check),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else null,
        modifier = modifier
    )
}