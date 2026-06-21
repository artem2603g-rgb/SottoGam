@file:OptIn(androidx.compose.animation.ExperimentalAnimationApi::class, androidx.compose.ui.text.ExperimentalTextApi::class)

package com.example.simpleauthapp

import android.os.Bundle
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Дизайн константы
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

@Composable
fun MainScaffold(currentUser: String, onLogout: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Лента", "Поиск", "Чат", "Профиль")
    val icons = listOf(Icons.Default.Home, Icons.Default.Search, Icons.Default.Send, Icons.Default.Person)

    Scaffold(
        bottomBar = {
            // Исправлено: tonalElevation вместо toneElevation
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
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

@Composable
fun FeedScreen() {
    val posts = listOf(
        Pair("artem_rgb", "Первый пост в SottoGam! 🚀 Начинаем строить мессенджер будущего."),
        Pair("android_fan", "Дизайн в стиле Material 3 выглядит очень свежо."),
        Pair("sottogam_dev", "Локальная база данных подключена. Скоро добавим облако!")
    )
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("SottoGam", style = TextStyle(brush = InstaGradient, fontSize = 28.sp, fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.NotificationsNone, null)
            }
        }
        items(posts) { post ->
            Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(InstaGradient))
                    Text(post.first, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 10.dp))
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.MoreVert, null)
                }
                Box(modifier = Modifier.fillMaxWidth().height(300.dp).background(Color(0xFFF0F0F0)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Image, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                }
                Row(modifier = Modifier.padding(8.dp)) {
                    Icon(Icons.Default.FavoriteBorder, null, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Outlined.ChatBubbleOutline, null, modifier = Modifier.size(28.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Default.Send, null, modifier = Modifier.size(28.dp))
                }
                Text(post.second, modifier = Modifier.padding(horizontal = 12.dp), fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun SearchScreen() {
    var query by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query, 
            onValueChange = { query = it }, 
            placeholder = { Text("Поиск по @username") }, 
            modifier = Modifier.fillMaxWidth(), 
            shape = RoundedCornerShape(12.dp),
            leadingIcon = { Icon(Icons.Default.Search, null) }
        )
        Spacer(modifier = Modifier.height(24.dp))
        if (query.isNotEmpty()) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.size(50.dp).clip(CircleShape).background(Color.LightGray))
                Column(modifier = Modifier.padding(start = 16.dp)) {
                    Text("@$query", fontWeight = FontWeight.Bold)
                    Text("Пользователь SottoGam", color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {}, shape = RoundedCornerShape(8.dp)) { Text("Открыть") }
            }
        }
    }
}

@Composable
fun ChatScreen() {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(64.dp), tint = SkyBlue)
        Text("Ваши чаты пусты", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        Text("Найдите друзей через поиск", color = Color.Gray)
    }
}

@Composable
fun ProfileScreen(user: String, onLogout: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(InstaGradient))
        Text("@$user", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        Text("Пользователь SottoGam • 0 постов", color = Color.Gray)
        
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onLogout, 
            modifier = Modifier.fillMaxWidth(), 
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Выйти из профиля", color = Color.Black)
        }
    }
}

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("SottoGam", style = TextStyle(brush = InstaGradient, fontSize = 48.sp, fontWeight = FontWeight.Bold))
        Text("Новая эра мессенджеров", color = Color.Gray, modifier = Modifier.padding(bottom = 48.dp))
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
            Text("Поехали!")
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
        OutlinedTextField(u, { u = it }, label = { Text("Юзернейм") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(p, { p = it }, label = { Text("Пароль") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            if (u.isEmpty() || p.isEmpty()) return@Button
            if (isLogin) {
                if (prefs.getString(u, null) == p) onSuccess(u) else Toast.makeText(context, "Неверный пароль", Toast.LENGTH_SHORT).show()
            } else {
                prefs.edit().putString(u, p).apply(); isLogin = true
                Toast.makeText(context, "Успешно! Теперь войдите", Toast.LENGTH_SHORT).show()
            }
        }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = SkyBlue), shape = RoundedCornerShape(12.dp)) {
            Text(if (isLogin) "Войти" else "Зарегистрироваться")
        }
        TextButton(onClick = { isLogin = !isLogin }) { Text(if (isLogin) "Изменить режим" else "Уже есть аккаунт?") }
    }
}
