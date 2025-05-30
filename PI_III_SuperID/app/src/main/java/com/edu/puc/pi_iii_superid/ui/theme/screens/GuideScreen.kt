package com.edu.puc.pi_iii_superid.ui.theme.screens

import PreferencesManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


@Composable
fun GuideScreen(navController: NavController) {

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferencesManager = remember { PreferencesManager(context) }

    Spacer(modifier = Modifier.height(28.dp))
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .border(2.dp, Color(0xFF03A9F4)) // Borda azul
    ) {
        // Conteúdo principal centralizado
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(56.dp))

            Text(
                text = "COMO USAR:",
                fontSize = 30.sp,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color(0xFF03A9F4),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TextosNumerados("1. Crie sua conta no app com seu nome, e-mail e uma senha mestre.")
            TextosNumerados("2. Crie suas senhas e categorias para acessar nossos sites e aplicativos parceiros, utilizando um ícone exatamente assim:")
            IconeCentral(Icons.Default.Add)
            TextosNumerados("3. E pronto, agora é só clicar no ícone de camera e escanear o QR-code na tela:")
            IconeCentral(Icons.Default.Fullscreen)
            TextosNumerados("4. Agora é só aproveitar sua total segurança com o super ID!")
        }

        // Rodapé fixo no fundo da tela
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.BottomCenter) // POSICIONA NO FINAL DA TELA
                .background(
                    color = Color(0xFF03A9F4),
                    shape = RoundedCornerShape(topStart = 100.dp, topEnd = 100.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    scope.launch {
                        val aceitou = preferencesManager.termsAccepted.first()
                        if (aceitou) {
                            navController.popBackStack()
                        } else {
                            navController.navigate("termsofuse")
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text("PROSSEGUIR",fontSize = 20.sp, color = Color(0xFF03A9F4))
            }
        }
    }
}


@Composable
fun TextosNumerados(texto: String) {
    Text(
        text = texto,
        color = Color(0xFF03A9F4),
        fontSize = 24.sp,
        style = MaterialTheme.typography.bodyMedium.copy(
            lineHeight = 25.sp
        ),
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth()
    )
}

@Composable
fun IconeCentral(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(70.dp)
            .background(Color(0xFF004A8F), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(34.dp)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

