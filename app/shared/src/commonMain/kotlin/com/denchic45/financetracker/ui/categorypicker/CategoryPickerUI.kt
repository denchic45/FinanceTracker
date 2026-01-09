package com.denchic45.financetracker.ui.categorypicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.domain.model.CategoryItem
import com.denchic45.financetracker.ui.NoDataContent
import com.denchic45.financetracker.ui.iconpicker.IconGridItem
import com.denchic45.financetracker.ui.resource.CacheableResourceListContent
import com.denchic45.financetracker.ui.resource.CircularLoadingBox
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.allDrawableResources
import financetracker_app.shared.generated.resources.category_add
import financetracker_app.shared.generated.resources.category_list_empty
import financetracker_app.shared.generated.resources.category_pick_title
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerSheet(
    income: Boolean,
    selectedId: Long?,
    viewModel: CategoryPickerViewModel = koinViewModel {
        parametersOf(selectedId)
        parametersOf(income)
    }
) {
    val categories by viewModel.categories.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(onDismissRequest = {
        viewModel.onDismissClick()
    }, sheetState = sheetState) {
        CacheableResourceListContent(
            resource = categories,
            loadingContent = { CircularLoadingBox(Modifier.height(160.dp)) },
            dataContent = { items ->
                Column {
                    Text(
                        stringResource(Res.string.category_pick_title),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    LazyVerticalGrid(
                        contentPadding = PaddingValues(16.dp),
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(items, key = { it.id }) { category ->
                            CategoryGridItem(
                                category = category,
                                selected = category.id == selectedId,
                                onClick = { viewModel.onSelect(category) }
                            )
                        }
                    }
                }
            },
            emptyDataContent = {
                NoDataContent(
                    modifier = Modifier.height(160.dp),
                    title = { Text(text = stringResource(Res.string.category_list_empty)) },
                    action = {
                        Button(onClick = viewModel::onCreateCategoryClick) {
                            Text(stringResource(Res.string.category_add))
                        }
                    }
                )
            }
        )
    }
}

@Composable
fun CategoryGridItem(
    category: CategoryItem,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconGridItem(
            iconResource = Res.allDrawableResources.getValue(category.iconName),
            selected = selected,
            onClick = onClick
        )
        Text(
            text = category.name,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}