package com.edu.puc.pi_iii_superid.ui.theme.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.superid.R

@Composable
fun VerifiedMailScreen(onProsseguirClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF03A9F4))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.email_verificado),
            contentDescription = "Verificação concluída",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Texto de sucesso
        Text(
            text = "EMAIL VERIFICADO COM\nSUCESSO!",
            color = Color.White,
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Botão prosseguir
        Button(
            onClick = onProsseguirClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)),
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(48.dp)
        ) {
            Text("PROSSEGUIR", color = Color.White)
        }
    }
}
