package com.denchic45.financetracker.ui.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.denchic45.financetracker.di.AppRouter
import com.denchic45.financetracker.ui.AppUIEvent
import com.denchic45.financetracker.ui.LoadingState
import com.denchic45.financetracker.ui.accountdetails.AccountDetailsSheet
import com.denchic45.financetracker.ui.accounteditor.AccountEditorScreen
import com.denchic45.financetracker.ui.accountpicker.AccountPickerSheet
import com.denchic45.financetracker.ui.categorydetails.CategoryDetailsContent
import com.denchic45.financetracker.ui.categoryeditor.CategoryEditorScreen
import com.denchic45.financetracker.ui.categorypicker.CategoryPickerSheet
import com.denchic45.financetracker.ui.navigation.BottomSheetSceneStrategy
import com.denchic45.financetracker.ui.navigation.SimpleOverlaySceneStrategy
import com.denchic45.financetracker.ui.resource.get
import com.denchic45.financetracker.ui.resource.getSuspended
import com.denchic45.financetracker.ui.tagdetails.TagDetailsSheet
import com.denchic45.financetracker.ui.tageditor.TagEditorDialog
import com.denchic45.financetracker.ui.tags.TagsScreen
import com.denchic45.financetracker.ui.tagspicker.TagsPickerSheet
import com.denchic45.financetracker.ui.toast.ToastDurationType
import com.denchic45.financetracker.ui.toast.ToastManager
import com.denchic45.financetracker.ui.transactiondetails.TransactionDetailsSheet
import com.denchic45.financetracker.ui.transactioneditor.TransactionEditorScreen
import com.denchic45.financetracker.ui.transactions.TransactionsScreen
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.common_ok
import kotlinx.coroutines.flow.Flow
import org.jetbrains.compose.resources.stringResource
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootContainer(viewModel: MainViewModel = koinViewModel()) {
    val router = koinInject<AppRouter>()

    HandleAppEvent(viewModel.events)
    val loading by viewModel.loading.collectAsState()
    AppLoading(loading)

    NavDisplay(
        backStack = router.backstack,
        sceneStrategy = remember {
            BottomSheetSceneStrategy<NavEntry>()
                .then(SimpleOverlaySceneStrategy())
        },
        transitionSpec = {
            fadeIn(tween(300)) togetherWith fadeOut(tween(300))
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<NavEntry.Main> {
                MainScreen(viewModel)
            }
            entry<NavEntry.Tags> {
                TagsScreen()
            }
            entry<NavEntry.TagDetails>(
                metadata = SimpleOverlaySceneStrategy.overlay()
            ) { key ->
                TagDetailsSheet(
                    tagId = key.tagId
                )
            }
            entry<NavEntry.DataUsage> { key ->
                TODO()
            }
            entry<NavEntry.Settings> { key ->

            }
            entry<NavEntry.About> { key ->
                TODO()
            }

            entry<NavEntry.AccountDetails>(
                metadata = SimpleOverlaySceneStrategy.overlay()
            ) { key ->
                AccountDetailsSheet(key.accountId)
            }
            entry<NavEntry.Transactions> {
                TransactionsScreen()
            }
            entry<NavEntry.AccountEditor> { key ->
                AccountEditorScreen(
                    accountId = key.accountId
                )
            }
            entry<NavEntry.TransactionEditor> { key ->
                TransactionEditorScreen(
                    transactionId = key.transactionId
                )
            }
            entry<NavEntry.TransactionDetails>(
                metadata = SimpleOverlaySceneStrategy.overlay()
            ) { key ->
                TransactionDetailsSheet(
                    transactionId = key.transactionId
                )
            }
            entry<NavEntry.CategoryDetails>(
                metadata = SimpleOverlaySceneStrategy.overlay()
            ) { key ->
                CategoryDetailsContent(
                    categoryId = key.categoryId
                )
            }
            entry<NavEntry.CategoryEditor> { key ->
                CategoryEditorScreen(
                    categoryId = key.categoryId,
                    income = key.income
                )
            }
            entry<NavEntry.AccountPicker>(
                metadata = SimpleOverlaySceneStrategy.overlay()
            ) { key ->
                key.selectedIds?.let {
                    AccountPickerSheet(selectedIds = it)
                } ?: AccountPickerSheet()
            }
            entry<NavEntry.CategoryPicker>(
                metadata = SimpleOverlaySceneStrategy.overlay()
            ) { key ->
                CategoryPickerSheet(income = key.income, selectedId = key.selectedId)
            }
            entry<NavEntry.TagsPicker>(
                metadata = SimpleOverlaySceneStrategy.overlay()
            ) { key ->
                TagsPickerSheet(selectedTagIds = key.selectedIds)
            }
            entry<NavEntry.TagEditor> { key ->
                TagEditorDialog(tagId = key.tagId)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLoading(state: LoadingState?) {
    state?.let { state ->
        BasicAlertDialog(
            onDismissRequest = {},
            content = {
                Surface(shape = MaterialTheme.shapes.medium) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(state.message.get())
                    }
                }
            })
    }
}

@Composable
fun HandleAppEvent(eventFlow: Flow<AppUIEvent>) {
    val toastManager = koinInject<ToastManager>()
    var showAlertDialog by remember { mutableStateOf<AppUIEvent.AlertMessage?>(null) }

    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is AppUIEvent.AlertMessage -> showAlertDialog = event

                is AppUIEvent.Toast -> {
                    toastManager.showToast(event.message.getSuspended(), ToastDurationType.SHORT)
                }
            }
        }
    }

    showAlertDialog?.let { event ->
        AlertDialog(
            onDismissRequest = { showAlertDialog = null },
            title = { Text(event.title.get()) },
            text = { event.text?.let { Text(it.get()) } },
            confirmButton = {
                TextButton(onClick = { showAlertDialog = null }) {
                    Text(stringResource(Res.string.common_ok))
                }
            })
    }
}
