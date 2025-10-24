package com.exdigital.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.exdigital.app.ui.components.ExDigitalButton
import com.exdigital.app.ui.components.ExDigitalTextField
import com.exdigital.app.ui.navigation.Screen
import com.exdigital.app.ui.theme.BackgroundDarkest
import com.exdigital.app.ui.theme.PrimaryOrange
import com.exdigital.app.ui.theme.TealAccent
import com.exdigital.app.ui.theme.TextPrimary
import com.exdigital.app.ui.theme.TextTertiary
import com.exdigital.app.ui.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundDarkest,
                        BackgroundDarkest
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Icon(
                imageVector = Icons.Default.Devices,
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp),
                tint = PrimaryOrange
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "ExDigital",
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                color = PrimaryOrange
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Inicia sesión para continuar",
                fontSize = 16.sp,
                color = TextTertiary
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Email Field
            ExDigitalTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorMessage = ""
                },
                label = "Correo electrónico",
                placeholder = "ejemplo@correo.com",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email",
                        tint = TextTertiary
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            ExDigitalTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorMessage = ""
                },
                label = "Contraseña",
                placeholder = "••••••••",
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Password",
                        tint = TextTertiary
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default.Visibility
                            else
                                Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible)
                                "Ocultar contraseña"
                            else
                                "Mostrar contraseña",
                            tint = TextTertiary
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Forgot Password
            Text(
                text = "¿Olvidaste tu contraseña?",
                fontSize = 14.sp,
                color = TealAccent,
                modifier = Modifier
                    .align(Alignment.End)
                    .clickable { /* TODO: Implementar recuperación */ }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Error Message
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            ExDigitalButton(
                text = "Iniciar Sesión",
                onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        errorMessage = "Por favor completa todos los campos"
                    } else {
                        val success = authViewModel.login(email, password)
                        if (success) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        } else {
                            errorMessage = "Credenciales inválidas"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Register Link
            Text(
                text = "¿No tienes cuenta? ",
                fontSize = 14.sp,
                color = TextTertiary
            )

            Text(
                text = "Regístrate aquí",
                fontSize = 14.sp,
                color = TealAccent,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
    }
}