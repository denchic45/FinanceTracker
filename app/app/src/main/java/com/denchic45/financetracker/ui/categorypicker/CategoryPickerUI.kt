package com.denchic45.financetracker.ui.categorypicker

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.domain.model.CategoryItem
import com.denchic45.financetracker.ui.NoDataContent
import com.denchic45.financetracker.ui.icon.AppIcons
import com.denchic45.financetracker.ui.icon.CategorizedIcons
import com.denchic45.financetracker.ui.resource.CacheableResourceListContent
import com.denchic45.financetracker.ui.resource.CircularLoadingBox
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryPickerSheet(
    income: Boolean,
    viewModel: CategoryPickerViewModel = koinViewModel {
        parametersOf(income)
    }
) {
    val categories by viewModel.categories.collectAsState()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(onDismissRequest = {
        viewModel.onDismissClick() }, sheetState = sheetState) {
        CacheableResourceListContent(
            resource = categories,
            loadingContent = { CircularLoadingBox(Modifier.height(160.dp)) },
            dataContent = { items ->
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(82.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(items, key = { it.id }) { category ->
                        CategoryGridItem(
                            category = category,
                            onClick = { viewModel.onSelect(category) }
                        )
                    }
                }
            },
            emptyDataContent = {
                NoDataContent(
                    modifier = Modifier.height(160.dp),
                    title = { Text(text = "У вас нет ни одной категории") },
                    action = {
                        Button(onClick = viewModel::onCreateCategoryClick) {
                            Text("Создать категорию")
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Card(
            modifier = Modifier.size(56.dp)
        ) {
            println("Category: ${category.iconName}")
            Icon(
                imageVector = AppIcons.CategorizedIcons.getValue(category.iconName),
                contentDescription = "category icon",
                modifier = modifier
            )
        }
        Text(
            text = category.name,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center
        )
    }
}