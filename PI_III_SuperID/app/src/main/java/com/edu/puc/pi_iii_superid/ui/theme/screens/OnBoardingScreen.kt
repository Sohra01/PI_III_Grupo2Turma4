package com.edu.puc.pi_iii_superid.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.superid.R
import androidx.compose.foundation.Image
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

// Composable que representa a tela de onboarding do app
@Composable
fun OnBoardingScreen(navController: NavController) {

    // Coluna principal que organiza o conteúdo verticalmente
    Column(
        modifier = Modifier
            .fillMaxSize() // Ocupa todo o espaço disponível
            .background(Color.White), // Fundo branco
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween // Espaço entre os elementos verticalmente
    ) {
        // Subcoluna para elementos do topo (logo, texto, botão)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Barra curva azul na parte superior
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(
                        Color(0xFF03A9F4), // Azul
                        shape = RoundedCornerShape(bottomStart = 190.dp, bottomEnd = 190.dp) // Cantos inferiores arredondados
                    )
            )

            Spacer(modifier = Modifier.height(16.dp)) // Espaço

            // Logo do Super ID
            Image(
                painter = painterResource(id = R.drawable.superid_logo),
                contentDescription = "Ícone de segurança",
                modifier = Modifier.size(180.dp)
            )

            Spacer(modifier = Modifier.height(16.dp)) // Espaço

            // Texto descritivo do app
            Text(
                text = "O Super ID é um poderoso gerenciador de senhas inovador, " +
                        "que permite que você salve, proteja e acesse suas contas com praticidade!",
                color = Color(0xFF03A9F4), // Azul
                textAlign = TextAlign.Center, // Centraliza o texto
                modifier = Modifier.padding(horizontal = 24.dp), // Margens laterais
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 35.sp // Espaçamento entre linhas
                )
            )

            Spacer(modifier = Modifier.height(36.dp)) // Espaço

            // Botão para prosseguir para a próxima tela
            Button(
                onClick = { navController.navigate("guide") }, // Navega para a tela de guia
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF03A9F4)) // Cor azul
            ) {
                Text("PROSSEGUIR", color = Color.White) // Texto branco
            }
        }

        // Imagem grande na parte inferior da tela
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f) // Ocupa 70% da altura da tela
                .align(Alignment.End) // Alinha ao final da coluna principal
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_onboarding),
                contentDescription = "Imagem do celular",
                modifier = Modifier
                    .fillMaxSize() // Imagem ocupa todo o espaço do Box
                    .align(Alignment.Center) // Centraliza imagem no Box
            )

        }
    }
}
