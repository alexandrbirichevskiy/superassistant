package com.example.superassistant.yandexgpt.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.superassistant.ChatDatabaseProvider
import com.example.superassistant.DatabaseProvider
import com.example.superassistant.GPTModels
import com.example.superassistant.SuperAssistantRetrofit
import com.example.superassistant.chatgpt.ChatGptRepository
import com.example.superassistant.ollama.OllamaRepository
import com.example.superassistant.yandexgpt.data.ChatRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = DatabaseProvider.getDatabase(this)
        val dbChat = ChatDatabaseProvider.getDatabase(this)

        val dialogsFactory = DialogsViewModelFactory(db.dialogsDao())
        val dialogsViewModel = ViewModelProvider(this, dialogsFactory)[DialogsViewModel::class.java]

        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "dialogs"
            ) {

                composable("dialogs") {
                    MainDialogsScreen(navController, dialogsViewModel)
                }

                composable(
                    route = "chat/{id}/{name}/{service}/{model}",
                    arguments = listOf(
                        navArgument("id") { type = NavType.IntType },
                        navArgument("name") { type = NavType.StringType },
                        navArgument("service") { type = NavType.StringType },
                        navArgument("model") { type = NavType.StringType }
                    )
                ) { backStackEntry ->

                    val id = backStackEntry.arguments?.getInt("id") ?: 0
                    val name = backStackEntry.arguments?.getString("name") ?: ""
                    val service = backStackEntry.arguments?.getString("service") ?: ""
                    val modelKey = backStackEntry.arguments?.getString("model") ?: ""

                    val model = when (modelKey) {
                        GPTModels.YANDEX_PRO.name -> GPTModels.YANDEX_PRO.model
                        GPTModels.YANDEX_LITE.name -> GPTModels.YANDEX_LITE.model
                        else -> modelKey
                    }

                    val chatViewModel = remember {
                        val retrofit = SuperAssistantRetrofit()
                        val dialog = Dialog(id, name, service, model)
                        ChatViewModel(
                            dialog,
                            ChatRepository(retrofit, dbChat.requestDao()),
                            ChatGptRepository(retrofit),
                            OllamaRepository(retrofit = retrofit)
                        )
                    }

                    ChatScreen(
                        viewModel = chatViewModel
                    )
                }
            }
        }
    }
}
