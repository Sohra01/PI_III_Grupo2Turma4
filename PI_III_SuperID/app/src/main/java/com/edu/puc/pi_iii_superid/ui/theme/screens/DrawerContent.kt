package com.edu.puc.pi_iii_superid.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.superid.R
import com.google.firebase.auth.FirebaseAuth

// Função composable que define o conteúdo do menu lateral (drawer)
@Composable
fun DrawerContent(onClose: () -> Unit, navController: NavController) {

    // Obtém o usuário atualmente autenticado
    val user = FirebaseAuth.getInstance().currentUser
    // Define nome e email do usuário (com valores padrão caso estejam nulos)
    val nome = user?.displayName ?: "Usuário"
    val email = user?.email ?: "E-mail não disponível"

    // Coluna principal que estrutura o conteúdo do drawer
    Column(
        modifier = Modifier
            .fillMaxHeight() // Ocupa toda a altura da tela
            .fillMaxWidth(0.8f) // Ocupa 80% da largura da tela (como é comum em drawers)
            .background(Color.White) // Fundo branco
            .padding(16.dp), // Espaçamento interno
        horizontalAlignment = Alignment.CenterHorizontally // Alinhamento central horizontal
    ) {
        // Linha com botão para fechar o menu (ícone X)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Fechar") // Ícone de fechar
            }
        }

        Spacer(modifier = Modifier.height(40.dp)) // Espaço entre o botão de fechar e o avatar

        // Ícone/avatar do usuário (imagem local do drawable)
        Icon(
            painter = painterResource(id = R.drawable.ic_user), // Recurso de imagem (ícone do usuário)
            contentDescription = "User", // Descrição para acessibilidade
            modifier = Modifier.size(120.dp), // Tamanho do ícone
            tint = Color.Unspecified // Cor padrão (imagem sem alteração de cor)
        )

        Spacer(modifier = Modifier.height(8.dp)) // Espaço entre o avatar e o nome

        // Nome do usuário em letras maiúsculas e em negrito
        Text(nome.uppercase(), fontWeight = FontWeight.Bold)

        // Email do usuário em cor cinza
        Text(email, color = Color.Gray)

        Spacer(modifier = Modifier.height(500.dp)) // Espaço para empurrar os botões para a parte inferior

        // Botão para navegar até a tela de guia
        Button(
            onClick = { navController.navigate("guide") }, // Ação ao clicar
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)), // Cor azul
            shape = RoundedCornerShape(50), // Bordas arredondadas
            modifier = Modifier
                .fillMaxWidth() // Ocupa a largura total disponível
                .padding(vertical = 4.dp) // Espaçamento vertical entre os botões
        ) {
            Text("GUIA", color = Color.White) // Texto branco no botão
        }

        // Botão para fazer logout e voltar à tela de login
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut() // Desloga o usuário
                navController.navigate("login") // Navega para a tela de login
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8F0000)), // Cor vermelha
            shape = RoundedCornerShape(50), // Bordas arredondadas
            modifier = Modifier
                .fillMaxWidth() // Ocupa a largura total disponível
                .padding(vertical = 4.dp) // Espaçamento entre os botões
        ) {
            Text("SAIR", color = Color.White) // Texto branco no botão
        }
    }
}
