package com.example.superassistant.yandexgpt.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.superassistant.yandexgpt.presentation.models.CardDataUi
import com.example.superassistant.yandexgpt.presentation.models.ChatMessageUi
import com.google.gson.Gson
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel
) {
    val messages = viewModel.messages
    val isLoading by viewModel.isLoading
    val lastError by remember { viewModel.lastError }

    val textState = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lazyListState = rememberLazyListState()

    TrackActivityDestroy(viewModel)

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) lazyListState.animateScrollToItem(messages.lastIndex)
    }

    LaunchedEffect(lastError) {
        lastError?.let { err ->
            scope.launch {
                snackbarHostState.showSnackbar(err)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(viewModel.name) })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .imePadding()
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(8.dp),
                reverseLayout = false,
                state = lazyListState
            ) {
                itemsIndexed(messages) { _, item ->
                    MessageRow(item)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextField(
                    value = textState.value,
                    onValueChange = { textState.value = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Введите сообщение...") },
                    singleLine = false,
                    maxLines = 4
                )
                IconButton(
                    onClick = {
                        val text = textState.value.trim()
                        if (text.isNotEmpty() && !isLoading) {
                            viewModel.sendUserMessage(false, text)
                            textState.value = ""
                        } else if (isLoading) {
                            Toast.makeText(
                                context,
                                "Пожалуйста подождите, ответ загружается...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
fun MessageRow(message: ChatMessageUi) {

    var displayedText by remember { mutableStateOf(message.text) }
    val bubbleColor = if (message.isUser) Color(0xFFDCF8C6) else Color(0xFFECECEC)
    val clipboardManager = LocalClipboardManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                clipboardManager.setText(AnnotatedString(displayedText))
//                displayedText = parseCardDataJson(message.text).toString()
            },
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .background(bubbleColor, shape = MaterialTheme.shapes.medium)
                .padding(12.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = displayedText, textAlign = TextAlign.Start)
        }
    }
}

@Composable
fun TrackActivityDestroy(viewModel: ChatViewModel) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                viewModel.saveChat()
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}


fun parseCardDataJson(jsonString: String): List<CardDataUi> {
    val gson = Gson()
    val listType = object : com.google.gson.reflect.TypeToken<List<CardDataUi>>() {}.type
    return gson.fromJson(jsonString, listType)
}
