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
    navController: NavController,
    email: String,
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val isButtonEnabled = remember { mutableStateOf(true) }
    val timerValue = remember { mutableStateOf(60) }
    val scope = rememberCoroutineScope()
    val isCheckingVerification = remember { mutableStateOf(false) }
    val verificationStatus = remember { mutableStateOf<String?>(null) }

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

    fun startTimer() {
        isButtonEnabled.value = false
        scope.launch {
            for (i in 60 downTo 1) {
                timerValue.value = i
                delay(1000)
            }
            isButtonEnabled.value = true
        }
    }

    fun onReenviarEmail() {
        enviarEmailRecuperacao()
        startTimer()
    }

    suspend fun verificarEmailVerificado(): Boolean {
        return withContext(Dispatchers.IO) {
            val user = auth.currentUser
            user?.reload()?.await()  // precisa do import da extensão await()
            return@withContext user?.isEmailVerified ?: false
        }
    }

    fun onVerificarEmail() {
        scope.launch {
            isCheckingVerification.value = true
            val verificado = verificarEmailVerificado()
            isCheckingVerification.value = false
            if (verificado) {
                navController.navigate("virifiedmail") {
                    popUpTo("sendMail") { inclusive = true }
                }
            } else {
                verificationStatus.value = "Email ainda não verificado."
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF00A6FF)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "EMAIL DE RECUPERAÇÃO\nENVIADO",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

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

                // Substituído o botão por um texto com as regras da senha
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
                    Text(
                        if (isButtonEnabled.value) "ENVIAR NOVAMENTE"
                        else "AGUARDE ${timerValue.value}s",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }


                Spacer(modifier = Modifier.height(16.dp))

                verificationStatus.value?.let { statusMsg ->
                    Text(
                        text = statusMsg,
                        color = Color.Yellow,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

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


