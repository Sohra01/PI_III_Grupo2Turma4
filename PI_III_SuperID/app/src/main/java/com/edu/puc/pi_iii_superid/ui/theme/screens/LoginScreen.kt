package com.example.superid.ui.theme.screens

import PreferencesManager
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.edu.puc.pi_iii_superid.data.LoginViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

// Função composable que representa a tela de login
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(), // ViewModel que contém os dados de login
    navController: NavController, // Controlador de navegação
    onSignUpClick: () -> Unit, // Callback para clique em "cadastrar"
    onForgotPasswordClick: () -> Unit // Callback para clique em "esqueci a senha"
) {
    val coroutineScope = rememberCoroutineScope() // Escopo para operações assíncronas
    val context = LocalContext.current // Contexto atual
    val preferencesManager = remember { PreferencesManager(context) } // Gerenciador de preferências
    var email by remember { mutableStateOf("") } // Estado local do email (não utilizado neste código)
    var password by remember { mutableStateOf("") } // Estado local da senha (não utilizado neste código)
    var passwordVisible by remember { mutableStateOf(false) } // Estado para visibilidade da senha

    // Surface com cor de fundo azul
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF00A6FF)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // Coluna centralizada na tela
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Título "LOGIN"
                Text(
                    text = "LOGIN",
                    fontSize = 42.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp)) // Espaçamento

                // Campo de texto para email
                OutlinedTextField(
                    value = viewModel.email.value,
                    onValueChange = { viewModel.email.value = it },
                    label = { Text("Email", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedTrailingIconColor = Color.White,
                        unfocusedTrailingIconColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp)) // Espaçamento

                // Campo de texto para senha com ícone de visibilidade
                OutlinedTextField(
                    value = viewModel.senha.value,
                    onValueChange = { viewModel.senha.value = it },
                    label = { Text("Senha", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = "Toggle password visibility",
                                tint = Color.White
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White,
                        focusedTrailingIconColor = Color.White,
                        unfocusedTrailingIconColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Link "Esqueci a senha"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Esqueci a senha",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.clickable { onForgotPasswordClick() }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botão de login
                Button(
                    onClick = {
                        val senhaValida = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+").matches(viewModel.senha.value)
                        if (!senhaValida) {
                            // Validação de senha com exibição de Toast
                            Toast.makeText(
                                context,
                                "Senha inválida! Deve ter ao menos uma letra minúscula, uma maiúscula e um número",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            // Chamada de login com callbacks de sucesso e erro
                            viewModel.loginUsuario(
                                onSuccess = {
                                    coroutineScope.launch {
                                        // Salva a senha mestra e navega para a tela de categoria
                                        preferencesManager.setSenhaMestre(viewModel.senha.value)
                                        Toast.makeText(context, "Login realizado!", Toast.LENGTH_LONG).show()
                                        navController.navigate("category") {
                                            popUpTo("login_screen") { inclusive = true }
                                        }
                                    }
                                },
                                onError = { erro ->
                                    // Exibe erro em um Toast
                                    Toast.makeText(context, erro, Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)),
                    shape = RoundedCornerShape(30.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text("ENTRAR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Link para cadastro
                Row {
                    Text("não possui uma conta?", color = Color.White, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "cadastrar",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onSignUpClick() }
                    )
                }
                Spacer(modifier = Modifier.height(80.dp)) // Espaço final inferior

            }
        }

    }
}
