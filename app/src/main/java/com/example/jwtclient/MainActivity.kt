package com.example.jwtclient

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var tokenManager: TokenManager
    private lateinit var api: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tokenManager = TokenManager(this)
        api = ApiClient.instance

        val etLogin = findViewById<EditText>(R.id.etLogin)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnCallApi = findViewById<Button>(R.id.btnCallApi)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        LogManager.logAction("Приложение запущено")

        btnRegister.setOnClickListener {
            val login = etLogin.text.toString()
            val pass = etPassword.text.toString()
            if (login.isBlank() || pass.isBlank()) {
                Toast.makeText(this, "Заполните логин и пароль", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            LogManager.logAction("Попытка регистрации: $login")
            api.register(AuthRequest(login, pass)).enqueue(object : Callback<MessageResponse> {
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    if (response.isSuccessful) {
                        val msg = response.body()?.message ?: "OK"
                        LogManager.logResponse("/register", msg)
                        Toast.makeText(this@MainActivity, "Регистрация успешна", Toast.LENGTH_SHORT).show()
                    } else {
                        LogManager.logError("/register", "Код ${response.code()}")
                        Toast.makeText(this@MainActivity, "Ошибка регистрации", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    LogManager.logError("/register", t.message ?: "unknown")
                    Toast.makeText(this@MainActivity, "Ошибка сети", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnLogin.setOnClickListener {
            val login = etLogin.text.toString()
            val pass = etPassword.text.toString()

            LogManager.logAction("Попытка авторизации: $login")
            api.login(AuthRequest(login, pass)).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val token = response.body()!!.token
                        tokenManager.saveToken(token)
                        LogManager.logResponse("/login", "Токен получен")
                        Toast.makeText(this@MainActivity, "Авторизация успешна", Toast.LENGTH_SHORT).show()
                    } else {
                        LogManager.logError("/login", "Код ${response.code()}")
                        Toast.makeText(this@MainActivity, "Ошибка авторизации", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    LogManager.logError("/login", t.message ?: "unknown")
                    Toast.makeText(this@MainActivity, "Ошибка сети", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnCallApi.setOnClickListener {
            val token = tokenManager.getToken()
            if (token == null) {
                Toast.makeText(this, "Сначала авторизуйтесь", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            LogManager.logAction("Запрос к защищенному API")
            api.getProtectedData("Bearer $token").enqueue(object : Callback<MessageResponse> {
                override fun onResponse(call: Call<MessageResponse>, response: Response<MessageResponse>) {
                    if (response.isSuccessful) {
                        val msg = response.body()?.message ?: "OK"
                        tvResult.text = msg
                        LogManager.logResponse("/protected", msg)
                    } else {
                        tvResult.text = "Ошибка: ${response.code()}"
                        LogManager.logError("/protected", "Код ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<MessageResponse>, t: Throwable) {
                    tvResult.text = "Ошибка сети"
                    LogManager.logError("/protected", t.message ?: "unknown")
                }
            })
        }
    }
}
