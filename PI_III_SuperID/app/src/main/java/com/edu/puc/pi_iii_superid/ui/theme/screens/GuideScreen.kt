package com.edu.puc.pi_iii_superid.ui.theme.screens

// Importação do gerenciador de preferências personalizadas
import PreferencesManager

// Importações de bibliotecas do Jetpack Compose para construção da interface
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

// Composable que representa a tela de guia
@Composable
fun GuideScreen(navController: NavController) {

    // Recupera o contexto atual
    val context = LocalContext.current
    // Cria uma scope para executar tarefas assíncronas
    val scope = rememberCoroutineScope()
    // Cria um gerenciador de preferências com base no contexto
    val preferencesManager = remember { PreferencesManager(context) }

    // Espaçamento superior
    Spacer(modifier = Modifier.height(28.dp))

    // Container principal da tela
    Box(
        modifier = Modifier
            .fillMaxSize() // Ocupa toda a tela
            .background(Color.White) // Fundo branco
            .border(2.dp, Color(0xFF03A9F4)) // Borda azul
    ) {
        // Coluna central para o conteúdo principal
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Espaçamento entre o topo e o conteúdo
            Spacer(modifier = Modifier.height(56.dp))

            // Título da seção
            Text(
                text = "COMO USAR:",
                fontSize = 30.sp,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = Color(0xFF03A9F4),
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Instruções em etapas com ícones
            TextosNumerados("1. Crie sua conta no app com seu nome, e-mail e uma senha mestre.")
            TextosNumerados("2. Crie suas senhas e categorias para acessar nossos sites e aplicativos parceiros, utilizando um ícone exatamente assim:")
            IconeCentral(Icons.Default.Add)
            TextosNumerados("3. E pronto, agora é só clicar no ícone de camera e escanear o QR-code na tela:")
            IconeCentral(Icons.Default.Fullscreen)
            TextosNumerados("4. Agora é só aproveitar sua total segurança com o super ID!")
        }

        // Rodapé com botão fixo no final da tela
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .align(Alignment.BottomCenter) // Alinha o conteúdo no rodapé da tela
                .background(
                    color = Color(0xFF03A9F4),
                    shape = RoundedCornerShape(topStart = 100.dp, topEnd = 100.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Botão "PROSSEGUIR"
            Button(
                onClick = {
                    // Ação ao clicar no botão
                    scope.launch {
                        // Verifica se os termos foram aceitos
                        val aceitou = preferencesManager.termsAccepted.first()
                        if (aceitou) {
                            // Volta para a tela anterior
                            navController.popBackStack()
                        } else {
                            // Navega para a tela de termos de uso
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

// Composable para exibir textos numerados na tela de instrução
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

// Composable para exibir ícones circulares centralizados
@Composable
fun IconeCentral(icon: ImageVector) {
    Box(
        modifier = Modifier
            .size(70.dp) // Tamanho do círculo
            .background(Color(0xFF004A8F), shape = CircleShape), // Fundo azul escuro em forma circular
        contentAlignment = Alignment.Center // Ícone centralizado
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White, // Cor do ícone branca
            modifier = Modifier.size(34.dp) // Tamanho do ícone
        )
    }
    Spacer(modifier = Modifier.height(8.dp)) // Espaço abaixo do ícone
}
