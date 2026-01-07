package com.denchic45.financetracker.ui.tags

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.domain.model.TagItem
import com.denchic45.financetracker.ui.TagListItem
import com.denchic45.financetracker.ui.resource.CacheableResourceListContent
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.arrow_left
import financetracker_app.shared.generated.resources.tag_add
import financetracker_app.shared.generated.resources.tags_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsScreen(viewModel: TagsViewModel = koinViewModel()) {
    val tags by viewModel.tags.collectAsState()

    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = viewModel::onDismissClick) {
                    Icon(painterResource(Res.drawable.arrow_left), null)
                }
            },
            title = { Text(stringResource(Res.string.tags_title)) }
        )
    }) { padding ->
        CacheableResourceListContent(
            resource = tags,
            dataContent = { tagList ->
                TagList(
                    tags = tagList,
                    onTagClick = viewModel::onTagClick,
                    onCreateClick = viewModel::onCreateTagClick,
                    modifier = Modifier.padding(padding)
                )
            }
        )
    }
}

@Composable
fun TagList(
    tags: List<TagItem>,
    onTagClick: (Long) -> Unit,
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        item {
            Button(
                onClick = onCreateClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(stringResource(Res.string.tag_add))
            }
        }
        items(tags) { tag ->
            TagListItem(tag = tag, onClick = { onTagClick(tag.id) })
        }
    }
}