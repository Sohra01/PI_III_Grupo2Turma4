package com.edu.puc.pi_iii_superid.ui.theme.screens

// Importações necessárias
import PreferencesManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// Composable que representa a tela de verificação de senha mestre do app
@Composable
fun PasswordAppScreen(
    onSenhaCorreta: () -> Unit, // Callback executado ao digitar a senha correta
    onErro: (() -> Unit)? = null // Callback opcional executado em caso de erro
) {
    val coroutineScope = rememberCoroutineScope() // Scope para chamadas assíncronas
    val context = LocalContext.current // Contexto atual da aplicação
    val preferencesManager = remember { PreferencesManager(context) } // Gerenciador de preferências
    var senhaDigitada by remember { mutableStateOf("") } // Estado para senha digitada
    var erro by remember { mutableStateOf(false) } // Estado para controle de erro

    // Resetar erro ao carregar a tela
    LaunchedEffect(Unit) {
        erro = false
    }

    // Layout principal da tela
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp), // Margem interna
        verticalArrangement = Arrangement.Center, // Centraliza verticalmente
        horizontalAlignment = Alignment.CenterHorizontally // Centraliza horizontalmente
    ) {
        // Título
        Text(text = "Digite sua senha do app", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp)) // Espaço vertical

        // Campo para digitar a senha
        OutlinedTextField(
            value = senhaDigitada,
            onValueChange = {
                senhaDigitada = it
                erro = false // Resetar erro ao digitar
            },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(), // Oculta a senha
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = erro, // Estilo de erro
            modifier = Modifier.fillMaxWidth()
        )

        // Exibe mensagem de erro, se necessário
        if (erro) {
            Text(
                text = "Senha incorreta",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botão de entrada
        Button(
            onClick = {
                coroutineScope.launch {
                    val senhaSalva = preferencesManager.getSenhaMestre() // Recupera senha salva
                    if (senhaSalva == senhaDigitada) {
                        onSenhaCorreta() // Chama callback se senha for correta
                    } else {
                        erro = true // Indica erro
                        onErro?.invoke() // Chama callback de erro se existir
                        Toast.makeText(context, "Senha incorreta", Toast.LENGTH_SHORT).show() // Notificação
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)), // Cor do botão
            shape = RoundedCornerShape(30.dp), // Bordas arredondadas
            elevation = ButtonDefaults.buttonElevation(8.dp) // Elevação do botão
        ) {
            Text("Entrar") // Texto do botão
        }
    }
}
