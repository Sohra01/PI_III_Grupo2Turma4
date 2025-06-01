package com.edu.puc.pi_iii_superid.ui.theme.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.superid.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

@Composable
fun SendMailScreen(
    navController: NavController, // Controlador de navegação para troca de telas
    email: String, // Email para enviar o link de recuperação
) {
    val context = LocalContext.current // Contexto da aplicação para exibir Toasts
    val auth = FirebaseAuth.getInstance() // Instância do FirebaseAuth para operações de autenticação
    val isButtonEnabled = remember { mutableStateOf(true) } // Estado para habilitar/desabilitar botão reenviar email
    val timerValue = remember { mutableStateOf(60) } // Estado para controlar o contador regressivo do botão
    val scope = rememberCoroutineScope() // CoroutineScope para executar tarefas assíncronas
    val isCheckingVerification = remember { mutableStateOf(false) } // Estado para indicar se está checando verificação do email
    val verificationStatus = remember { mutableStateOf<String?>(null) } // Estado para mensagem de status da verificação do email

    // Função para enviar email de recuperação via Firebase
    fun enviarEmailRecuperacao() {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Link enviado para o e-mail!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Algo deu errado...", Toast.LENGTH_LONG).show()
                }
            }
    }

    // Função que inicia o timer para desabilitar botão por 60 segundos
    fun startTimer() {
        isButtonEnabled.value = false // Desabilita o botão
        scope.launch {
            for (i in 60 downTo 1) { // Contagem regressiva de 60 segundos
                timerValue.value = i
                delay(1000) // Espera 1 segundo
            }
            isButtonEnabled.value = true // Reabilita o botão após o tempo
        }
    }

    // Função chamada para reenviar o email e iniciar o timer
    fun onReenviarEmail() {
        enviarEmailRecuperacao()
        startTimer()
    }

    // Função suspensa para verificar se o email do usuário foi verificado
    suspend fun verificarEmailVerificado(): Boolean {
        return withContext(Dispatchers.IO) {
            val user = auth.currentUser
            user?.reload()?.await()  // Atualiza os dados do usuário (await requer import da extensão Kotlin Coroutines Firebase)
            return@withContext user?.isEmailVerified ?: false // Retorna true se o email foi verificado, false caso contrário
        }
    }

    // Função que executa a verificação de email dentro de uma coroutine
    fun onVerificarEmail() {
        scope.launch {
            isCheckingVerification.value = true // Indica que está checando
            val verificado = verificarEmailVerificado() // Verifica status
            isCheckingVerification.value = false // Fim da verificação
            if (verificado) {
                navController.navigate("virifiedmail") { // Navega para tela "virifiedmail" se verificado
                    popUpTo("sendMail") { inclusive = true } // Remove tela de envio da pilha de navegação
                }
            } else {
                verificationStatus.value = "Email ainda não verificado." // Mensagem caso não verificado
            }
        }
    }

    // UI principal da tela
    Surface(
        modifier = Modifier.fillMaxSize(), // Preenche toda a tela
        color = Color(0xFF00A6FF) // Fundo azul
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp), // Espaçamento das bordas
            horizontalAlignment = Alignment.CenterHorizontally, // Centraliza horizontalmente
            verticalArrangement = Arrangement.SpaceBetween // Espaça itens igualmente verticalmente
        ) {
            Spacer(modifier = Modifier.height(24.dp)) // Espaço superior

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Título principal com texto em branco e centralizado
                Text(
                    text = "EMAIL DE RECUPERAÇÃO\nENVIADO",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // Imagem ilustrativa do envio do email
                Image(
                    painter = painterResource(id = R.drawable.email_enviado),
                    contentDescription = "Email Enviado",
                    modifier = Modifier.size(400.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Texto explicativo das regras da senha
                Text(
                    text = "A nova senha deve conter pelo menos:\n" +
                            "- Uma letra minúscula\n" +
                            "- Uma letra maiúscula\n" +
                            "- Um número\n" +
                            "Ao cadastrar uma senha inválida a entrada no app sera barrada!",
                    color = Color.Yellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botão para reenviar email, desabilitado durante o timer
                Button(
                    onClick = { onReenviarEmail() },
                    enabled = isButtonEnabled.value,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isButtonEnabled.value) Color(0xFF004A8F) else Color.Gray
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    // Texto do botão mostra "Enviar novamente" ou o contador regressivo
                    Text(
                        if (isButtonEnabled.value) "ENVIAR NOVAMENTE"
                        else "AGUARDE ${timerValue.value}s",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Se houver mensagem de status da verificação, exibe em amarelo
                verificationStatus.value?.let { statusMsg ->
                    Text(
                        text = statusMsg,
                        color = Color.Yellow,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botão para voltar para a tela de login
                Button(
                    onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("VOLTAR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
}
