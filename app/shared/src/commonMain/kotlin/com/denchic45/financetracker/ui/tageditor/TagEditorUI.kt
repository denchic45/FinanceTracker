package com.denchic45.financetracker.ui.tageditor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.financetracker.ui.dialog.ConfirmDiscardChangesDialog
import financetracker_app.shared.generated.resources.Res
import financetracker_app.shared.generated.resources.arrow_back
import financetracker_app.shared.generated.resources.check
import financetracker_app.shared.generated.resources.common_back
import financetracker_app.shared.generated.resources.common_name_field
import financetracker_app.shared.generated.resources.common_save
import financetracker_app.shared.generated.resources.tag_new
import financetracker_app.shared.generated.resources.tag_update
import financetracker_app.shared.generated.resources.validation_name_required
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagEditorDialog(
    tagId: Long?,
    viewModel: TagEditorViewModel = koinViewModel { parametersOf(tagId) }
) {
    val state = viewModel.state
    var showDiscardConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(if (tagId == null) Res.string.tag_new else Res.string.tag_update)) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (viewModel.hasChanges()) showDiscardConfirmation = true
                        else viewModel.onDismissClick()
                    }) {
                        Icon(
                            painterResource(Res.drawable.arrow_back),
                            contentDescription = stringResource(Res.string.common_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::onSaveClick, enabled = !state.isLoading) {
                        Icon(
                            painterResource(Res.drawable.check),
                            contentDescription = stringResource(Res.string.common_save)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(Res.string.common_name_field)) },
                isError = state.showNameError,
                supportingText = { if (state.showNameError) Text(stringResource(Res.string.validation_name_required)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    BackHandler(viewModel.hasChanges()) {
        showDiscardConfirmation = true
    }

    ConfirmDiscardChangesDialog(
        show = showDiscardConfirmation,
        onConfirm = {
            showDiscardConfirmation = false
            viewModel.onDismissClick()
        },
        onDismiss = { showDiscardConfirmation = false }
    )
}
