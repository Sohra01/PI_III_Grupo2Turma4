package com.edu.puc.pi_iii_superid.ui.theme.screens

import PreferencesManager
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Composable
fun PasswordAppScreen(
    onSenhaCorreta: () -> Unit,
    onErro: (() -> Unit)? = null
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val preferencesManager = remember { PreferencesManager(context) }
    var senhaDigitada by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        erro = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Digite sua senha do app", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = senhaDigitada,
            onValueChange = {
                senhaDigitada = it
                erro = false
            },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = erro,
            modifier = Modifier.fillMaxWidth()
        )

        if (erro) {
            Text(
                text = "Senha incorreta",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    val senhaSalva = preferencesManager.getSenhaMestre()
                    if (senhaSalva == senhaDigitada) {
                        onSenhaCorreta()
                    } else {
                        erro = true
                        onErro?.invoke()
                        Toast.makeText(context, "Senha incorreta", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)),
            shape = RoundedCornerShape(30.dp),
            elevation = ButtonDefaults.buttonElevation(8.dp)
        ) {
            Text("Entrar")
        }
    }
}
