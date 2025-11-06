package com.example.superassistant.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.lifecycle.ViewModelProvider
import com.example.superassistant.ui.theme.SuperAssistantTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val chatViewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        chatViewModel.sendUserMessage(true, "")
        setContent {
            SuperAssistantTheme {
                Surface {
                    ChatScreen(viewModel = chatViewModel)
                }
            }
        }
    }
}