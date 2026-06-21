@file:OptIn(androidx.compose.animation.ExperimentalAnimationApi::class, androidx.compose.ui.text.ExperimentalTextApi::class)

package com.example.simpleauthapp

import android.os.Bundle
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Цвета
val InstaGradient = Brush.verticalGradient(listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCAF45)))
val SkyBlue = Color(0xFF0095F6)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("welcome") }
            var loggedInUser by remember { mutableStateOf("") }
            
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    when (currentScreen) {
                        "welcome" -> WelcomeScreen { currentScreen = "auth" }
                        "auth" -> AuthScreen { user -> loggedInUser = user; currentScreen = "main" }
                        "main" -> MainScaffold(loggedInUser) { currentScreen = "welcome" }
                    }
                }
            }
        }
    }
}

// --- НАВИГАЦИЯ ---
@Composable
fun MainScaffold(currentUser: String, onLogout: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Лента", "Поиск", "Чат", "Профиль")
    val icons = listOf(Icons.Default.Home, Icons.Default.Search, Icons.Default.Send, Icons.Default.Person)

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, toneElevation = 8.dp) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(icons[index], contentDescription = title) },
                        label = { Text(title, fontSize = 10.sp) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> FeedScreen()
                1 -> SearchScreen()
                2 -> ChatScreen()
                3 -> ProfileScreen(currentUser, onLogout)
            }
        }
    }
}

// --- ЭКРАН ЛЕНТЫ ---
@Composable
fun FeedScreen() {
    val posts = listOf(
        Pair("artem_rgb", "Первый пост в SottoGam! Привет всем."),
        Pair("meta_dev", "Дизайн в стиле инсты почти готов."),
        Pair("user_99", "Как вам наше новое приложение?")
    )
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text("SottoGam", style = TextStyle(brush = InstaGradient, fontSize = 28.sp, fontWeight = FontWeight.Bold), modifier = Modifier.padding(16.dp))
        }
        items(posts) { post ->
            Card(modifier = Modifier.fillMaxWidth().padding(8.dp), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F8F8))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(InstaGradient))
                        Text(post.first, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(post.second)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.DarkGray)
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color.DarkGray)
                    }
                }
            }
        }
    }
}

// --- ЭКРАН ПОИСКА ---
@Composable
fun SearchScreen() {
    var query by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(value = query, onValueChange = { query = it }, placeholder = { Text("Поиск пользователей...") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), leadingIcon = { Icon(Icons.Default.Search, null) })
        Spacer(modifier = Modifier.height(16.dp))
        if (query.isNotEmpty()) {
            Text("Результаты для: @$query", color = Color.Gray)
            // Здесь будет логика поиска по базе
        }
    }
}

// --- ЭКРАН ЧАТА ---
@Composable
fun ChatScreen() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
        Text("Ваши переписки зашифрованы", color = Color.Gray, modifier = Modifier.padding(top = 16.dp))
    }
}

// --- ЭКРАН ПРОФИЛЯ ---
@Composable
fun ProfileScreen(user: String, onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(InstaGradient))
        Text("@$user", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        Button(onClick = onLogout, modifier = Modifier.padding(top = 32.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) {
            Text("Выйти из аккаунта", color = Color.Black)
        }
    }
}

// --- ЭКРАНЫ ПРИВЕТСТВИЯ И ВХОДА (ОБНОВЛЕННЫЕ) ---
@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("SottoGam", style = TextStyle(brush = InstaGradient, fontSize = 48.sp, fontWeight = FontWeight.Bold))
        Text("Будущее общения", color = Color.Gray, modifier = Modifier.padding(bottom = 32.dp))
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
            Text("Начать работу")
        }
    }
}

@Composable
fun AuthScreen(onSuccess: (String) -> Unit) {
    val context = LocalContext.current
    var u by remember { mutableStateOf("") }; var p by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    val prefs = context.getSharedPreferences("users", Context.MODE_PRIVATE)

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(if (isLogin) "Вход" else "Регистрация", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(u, { u = it }, label = { Text("Имя пользователя") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(p, { p = it }, label = { Text("Пароль") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            if (isLogin) {
                if (prefs.getString(u, null) == p) onSuccess(u) else Toast.makeText(context, "Ошибка", Toast.LENGTH_SHORT).show()
            } else {
                prefs.edit().putString(u, p).apply(); isLogin = true
                Toast.makeText(context, "Готово! Теперь войдите", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = SkyBlue), shape = RoundedCornerShape(12.dp)) {
            Text(if (isLogin) "Войти" else "Создать")
        }
        TextButton(onClick = { isLogin = !isLogin }) { Text(if (isLogin) "Нет аккаунта?" else "Уже есть?") }
    }
}
