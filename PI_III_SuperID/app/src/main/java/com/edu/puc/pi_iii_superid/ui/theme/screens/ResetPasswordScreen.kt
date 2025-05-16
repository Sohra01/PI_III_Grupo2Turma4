package com.edu.puc.pi_iii_superid.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun ResetPasswordScreen(
    onSalvarClick: () -> Unit,
    onCancelarClick: () -> Unit
) {
    var novaSenha by remember { mutableStateOf("") }
    var confirmarSenha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }
    var confirmarVisivel by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00A6FF))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "REDEFINIR\nSENHA",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Campo Nova Senha
        OutlinedTextField(
            value = novaSenha,
            onValueChange = { novaSenha = it },
            label = { Text("Nova Senha", color = Color.White) },
            visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                    Icon(
                        imageVector = if (senhaVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedTrailingIconColor = Color.White,
                unfocusedTrailingIconColor = Color.White
            ),
            textStyle = TextStyle(color = Color.White),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        // Campo Confirmar Senha
        OutlinedTextField(
            value = confirmarSenha,
            onValueChange = { confirmarSenha = it },
            label = { Text("Confirmar Senha", color = Color.White) },
            visualTransformation = if (confirmarVisivel) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { confirmarVisivel = !confirmarVisivel }) {
                    Icon(
                        imageVector = if (confirmarVisivel) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.White,
                unfocusedBorderColor = Color.White,
                cursorColor = Color.White,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedLabelColor = Color.White,
                unfocusedLabelColor = Color.White,
                focusedTrailingIconColor = Color.White,
                unfocusedTrailingIconColor = Color.White
            ),
            textStyle = TextStyle(color = Color.White),
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        )

        // Botão SALVAR
        Button(
            onClick = onSalvarClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .height(50.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text("SALVAR", color = Color.White)
        }

        // Botão CANCELAR
        Button(
            onClick = onCancelarClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
                .height(50.dp),
            shape = RoundedCornerShape(50)
        ) {
            Text("CANCELAR", color = Color.White)
        }
    }
}
