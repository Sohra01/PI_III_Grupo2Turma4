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
import com.example.superid.R

@Composable
fun DrawerContent(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.8f)
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botão de fechar (X)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Fechar")
            }
        }

        // Avatar
        Icon(
            painter = painterResource(id = R.drawable.ic_user), // substitua pelo seu drawable
            contentDescription = "User",
            modifier = Modifier.size(120.dp),
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text("NOME USER", fontWeight = FontWeight.Bold)
        Text("Email", color = Color.Gray)

        Spacer(modifier = Modifier.height(240.dp))

        // Botões
        Button(
            onClick = { /* Ação do botão Guia */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text("GUIA", color = Color.White)
        }

        Button(
            onClick = { /* Ação de logout */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8F0000)),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        ) {
            Text("LOG OUT", color = Color.White)
        }
    }
}
