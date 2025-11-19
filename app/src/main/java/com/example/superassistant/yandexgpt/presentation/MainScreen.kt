package com.example.superassistant.yandexgpt.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.superassistant.GPTModels
import kotlinx.coroutines.flow.StateFlow

@Composable
fun MainDialogsScreen(
    navController: NavController,
    viewModel: DialogsViewModel,
) {

    var showSheet by remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    val dialogs = viewModel.dialogs.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showSheet = true }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Send")
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            reverseLayout = false,
            state = lazyListState
        ) {
//            item {
//                Button(
//                    modifier = Modifier.fillMaxWidth(),
//                    onClick = { viewModel.connect() }
//                ) {
//                    Text("Подключится к MCP")
//                }
//            }

            itemsIndexed(dialogs.value) { _, item ->
                DialogItem(
                    dialog = item,
                    onClick = {
                        val route = "chat/${it.id}/${it.name}/${it.service}/${it.model}"
                        navController.navigate(route)
                    },
                    onDelete = { viewModel.deleteDialog(it) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Показываем BottomSheet
        if (showSheet) {
            CreateDialogBottomSheet(
                services = viewModel.services,
                models = viewModel.models,
                onDismiss = { showSheet = false },
                onCreate = { name, service, model ->
                    viewModel.addDialog(name, service, model)
                    showSheet = false
                }
            )
        }
    }
}

@Composable
fun DialogItem(
    dialog: Dialog,
    modifier: Modifier = Modifier,
    onClick: (Dialog) -> Unit,
    onDelete: (Dialog) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick(dialog) })
            .padding(horizontal = 12.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Text(
                    text = dialog.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = dialog.service,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = dialog.model,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = { onDelete(dialog) }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateDialogBottomSheet(
    services: StateFlow<List<String>>,
    models: StateFlow<List<GPTModels>>,
    onDismiss: () -> Unit,
    onCreate: (String, String, String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    val services = services.collectAsState().value
    val models = models.collectAsState().value
    var name by remember { mutableStateOf("") }
    var expandedServices by remember { mutableStateOf(false) }
    var selectedService by remember { mutableStateOf("") }
    var expandedModels by remember { mutableStateOf(false) }
    var selectedModel by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Text(
                text = "Создать диалог с агентом",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(16.dp))

            // --- Поле ввода имени ---
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            // --- Выпадающий список моделей ---
            ExposedDropdownMenuBox(
                expanded = expandedServices,
                onExpandedChange = { expandedServices = !expandedServices }
            ) {

                OutlinedTextField(
                    value = selectedService,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Сервис") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedServices)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedServices,
                    onDismissRequest = { expandedServices = false }
                ) {
                    services.forEach { model ->
                        DropdownMenuItem(
                            text = { Text(model) },
                            onClick = {
                                selectedService = model
                                expandedServices = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // --- Выпадающий список моделей ---
            ExposedDropdownMenuBox(
                expanded = expandedModels,
                onExpandedChange = { expandedModels = !expandedModels }
            ) {
                OutlinedTextField(
                    value = selectedModel,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Модель") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedModels)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = expandedModels,
                    onDismissRequest = { expandedModels = false }
                ) {
                    models.forEach { model ->
                        DropdownMenuItem(
                            text = { Text(model.model) },
                            onClick = {
                                selectedModel = model.name
                                expandedModels = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // --- Кнопка ---
            Button(
                onClick = { onCreate(name, selectedService, selectedModel) },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank()
            ) {
                Text("Создать")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

