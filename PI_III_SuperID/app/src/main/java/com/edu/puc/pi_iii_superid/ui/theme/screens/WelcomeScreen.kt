package com.example.superid.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.R

@Composable
fun WelcomeScreen(
    onLoginClick: () -> Unit, // Callback quando o botão de login for clicado
    onSignUpClick: () -> Unit // Callback quando o botão de cadastro for clicado
) {
    // Superfície que cobre toda a tela com uma cor de fundo azul personalizada
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF00A6FF) // Cor de fundo da tela
    ) {
        // Box para centralizar o conteúdo no meio da tela
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Coluna para organizar os elementos verticalmente
            Column(
                horizontalAlignment = Alignment.CenterHorizontally, // Centraliza horizontalmente
                verticalArrangement = Arrangement.Center, // Centraliza verticalmente
                modifier = Modifier.padding(horizontal = 32.dp) // Espaçamento lateral
            ) {
                // Box que representa o container da logo com formato circular
                Box(
                    modifier = Modifier
                        .size(250.dp) // Tamanho do círculo
                        .clip(CircleShape) // Aplica formato circular
                        .background(Color.White), // Fundo branco dentro do círculo
                    contentAlignment = Alignment.Center // Centraliza a imagem dentro do círculo
                ) {
                    // Exibe a imagem da logo
                    Image(
                        painter = painterResource(id = R.drawable.superid_logo), // Recurso da imagem
                        contentDescription = "Logo SuperID", // Descrição para acessibilidade
                        modifier = Modifier.size(230.dp) // Tamanho da imagem menor que o círculo
                    )
                }

                // Espaçamento entre a logo e os botões
                Spacer(modifier = Modifier.height(52.dp))

                // Botão de LOGIN
                Button(
                    onClick = onLoginClick, // Executa a função passada quando clicar
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)), // Cor do botão
                    shape = RoundedCornerShape(30.dp), // Borda arredondada
                    modifier = Modifier
                        .fillMaxWidth() // Preenche toda a largura disponível
                        .height(50.dp), // Altura do botão
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp) // Elevação (sombra)
                ) {
                    // Texto do botão
                    Text(
                        "LOGIN",
                        color = Color.White, 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold 
                    )
                }

                // Espaçamento entre os botões
                Spacer(modifier = Modifier.height(16.dp))

                // Botão de CADASTRO
                Button(
                    onClick = onSignUpClick, // Executa a função passada quando clicar
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)), // Cor do botão
                    shape = RoundedCornerShape(30.dp), // Borda arredondada
                    modifier = Modifier
                        .fillMaxWidth() // Preenche toda a largura disponível
                        .height(50.dp), // Altura do botão
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp) // Elevação (sombra)
                ) {
                    // Texto do botão
                    Text(
                        "CADASTRO",
                        color = Color.White,
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.Bold 
                    )
                }
            }
        }
    }
}
