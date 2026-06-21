package com.example.simpleauthapp

import android.os.Bundle
import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AuthScreen(this)
                }
            }
        }
    }
}

@Composable
fun AuthScreen(context: Context) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    val sharedPrefs = context.getSharedPreferences("users_db", Context.MODE_PRIVATE)

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (isLogin) "Вход в SottoGam" else "Регистрация", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        TextField(value = username, onValueChange = { username = it }, label = { Text("Логин") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        TextField(value = password, onValueChange = { password = it }, label = { Text("Пароль") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            modifier = Modifier.fillMaxWidth(),
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
                        Toast.makeText(context, "Ошибка: неверный логин или пароль", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    if (sharedPrefs.contains(username)) {
                        Toast.makeText(context, "Пользователь уже существует", Toast.LENGTH_SHORT).show()
                    } else {
                        sharedPrefs.edit().putString(username, password).apply()
                        Toast.makeText(context, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                        isLogin = true
                    }
                }
            }
        ) {
            Text(if (isLogin) "Войти" else "Зарегистрироваться")
        }

        TextButton(onClick = { isLogin = !isLogin }) {
            Text(if (isLogin) "Нет аккаунта? Создать" else "Уже есть аккаунт? Войти")
        }
    }
}
