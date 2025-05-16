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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward

@Composable
fun OnBoardingScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Topo com curva azul
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .background(
                        Color(0xFF03A9F4),
                        shape = RoundedCornerShape(bottomStart = 190.dp, bottomEnd = 190.dp)
                    )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.superid_logo),
                contentDescription = "Ícone de segurança",
                modifier = Modifier.size(94.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "O Super ID é um poderoso gerenciador de senhas inovador, " +
                        "que permite que você salve, proteja e acesse suas contas com praticidade!",
                color = Color(0xFF03A9F4),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    lineHeight = 22.sp
                )
            )
        }

        // Imagem com setas sobrepostas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            // Imagem de fundo
            Image(
                painter = painterResource(id = R.drawable.img_onboarding),
                contentDescription = "Imagem do celular",
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center) // isso garante que a imagem fique centralizada
            )


            // Seta direita
            IconButton(
                onClick = {
                    navController.navigate("guide")
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp)
            ) {
                Icon(Icons.Filled.ArrowForward, contentDescription = "Seta direita", tint = Color.Black)
            }

        }
    }
}





