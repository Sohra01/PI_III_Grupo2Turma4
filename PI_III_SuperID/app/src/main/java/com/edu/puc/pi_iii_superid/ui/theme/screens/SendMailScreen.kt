package com.edu.puc.pi_iii_superid.ui.theme.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.superid.R



@Composable
fun SendMailScreen(navController: NavController) {
    val isButtonEnabled = remember { mutableStateOf(true) }
    val timerValue = remember { mutableStateOf(60) }
    val scope = rememberCoroutineScope()

    // Inicia o timer quando o botão é clicado
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
                    painter = painterResource(id =R.drawable.email_enviado),
                    contentDescription = "Email Enviado",
                    modifier = Modifier.size(300.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        startTimer()
                        // ação real de reenvio de e-mail aqui
                    },
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
                        color = Color.White
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
                    Text("VOLTAR", color = Color.White)
                }
            }
        }
    }
}

