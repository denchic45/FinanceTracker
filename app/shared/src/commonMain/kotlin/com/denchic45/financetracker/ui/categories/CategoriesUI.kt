package com.denchic45.financetracker.ui.categories

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.domain.model.CategoryItem
import com.denchic45.financetracker.ui.CategoryListItem
import com.denchic45.financetracker.ui.MainTopAppBar
import com.denchic45.financetracker.ui.resource.CacheableResourceListContent
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.category_add
import financetracker_app.shared.generated.resources.txn_expense
import financetracker_app.shared.generated.resources.txn_income
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    viewModel: CategoriesViewModel = koinViewModel(),
    onNavigationDrawerClick: () -> Unit
) {
    val incomeCategories by viewModel.incomeCategories.collectAsState()
    val expenseCategories by viewModel.expenseCategories.collectAsState()

    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    val tabTitles = listOf(
        stringResource(Res.string.txn_expense),
        stringResource(Res.string.txn_income)
    )

    Scaffold(topBar = {
        MainTopAppBar(
            onNavigationIconClick = onNavigationDrawerClick,
            actions = {}
        )
    }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                        text = { Text(text = title) }
                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) { page ->
                when (page) {
                    0 -> CacheableResourceListContent(
                        resource = expenseCategories,
                        dataContent = { categories ->
                            CategoryList(
                                categories = categories,
                                onCategoryClick = viewModel::onCategoryClick,
                                onCreateClick = { viewModel.onCreateCategoryClick(income = false) }
                            )
                        }
                    )

                    1 -> CacheableResourceListContent(
                        resource = incomeCategories,
                        dataContent = { categories ->
                            CategoryList(
                                categories = categories,
                                onCategoryClick = viewModel::onCategoryClick,
                                onCreateClick = { viewModel.onCreateCategoryClick(income = true) }
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryList(
    categories: List<CategoryItem>,
    onCategoryClick: (Long) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        item {
            FilledTonalButton(
                onClick = onCreateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(Res.string.category_add))
            }
        }
        items(categories) { category ->
            CategoryListItem(category = category, onClick = { onCategoryClick(category.id) })
        }
    }
}