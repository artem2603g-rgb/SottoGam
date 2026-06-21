@file:OptIn(androidx.compose.animation.ExperimentalAnimationApi::class)

package com.example.simpleauthapp

import android.os.Bundle
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val InstaGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFF833AB4), Color(0xFFFD1D1D), Color(0xFFFCAF45))
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("welcome") }
            
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            slideInHorizontally { it } + fadeIn() with slideOutHorizontally { -it } + fadeOut()
                        }
                    ) { screen ->
                        when (screen) {
                            "welcome" -> WelcomeScreen { currentScreen = "auth" }
                            "auth" -> AuthScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "SottoGam",
            style = TextStyle(
                brush = InstaGradient,
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "Будущее общения уже здесь",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 48.dp)
        )

        AdvantageItem(Icons.Default.FlashOn, "Скорость", "Мгновенная доставка сообщений")
        AdvantageItem(Icons.Default.Security, "Конфиденциальность", "Ваши данные под защитой")
        AdvantageItem(Icons.Default.Palette, "Дизайн", "Минимализм в каждой детали")
        AdvantageItem(Icons.Default.AutoAwesome, "Анимации", "Плавность, которую вы почувствуете")

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF262626))
        ) {
            Text("Начать", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun AdvantageItem(icon: ImageVector, title: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFFE1306C), modifier = Modifier.size(32.dp))
        Column(modifier = Modifier.padding(start = 16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(desc, color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun AuthScreen() {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    val sharedPrefs = context.getSharedPreferences("users_db", Context.MODE_PRIVATE)

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isLogin) "С возвращением!" else "Создать аккаунт",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Логин") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0095F6)),
            onClick = {
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (isLogin) {
                    val savedPass = sharedPrefs.getString(username, null)
                    if (savedPass == password) {
                        Toast.makeText(context, "Успешный вход!", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(context, "Ошибка доступа", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    sharedPrefs.edit().putString(username, password).apply()
                    Toast.makeText(context, "Аккаунт создан!", Toast.LENGTH_SHORT).show()
                    isLogin = true
                }
            }
        ) {
            Text(if (isLogin) "Войти" else "Зарегистрироваться", fontSize = 16.sp)
        }

        TextButton(onClick = { isLogin = !isLogin }, modifier = Modifier.padding(top = 16.dp)) {
            Text(
                if (isLogin) "Ещё нет аккаунта? Зарегистрируйтесь" 
                else "Уже есть аккаунт? Войти",
                color = Color(0xFF0095F6)
            )
        }
    }
}
