package com.example.superid.ui.theme.screens


import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.edu.puc.pi_iii_superid.data.CadastroViewModel


@Composable
fun SignUpScreen(
    viewModel: CadastroViewModel = viewModel(),
    navController: NavController
) {
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }

    fun traduzirErroFirebase(mensagem: String): String {
        return when {
            "email address is already in use" in mensagem.lowercase() -> "Este e-mail já está em uso."
            "invalid email" in mensagem.lowercase() -> "E-mail inválido."
            "password should be at least" in mensagem.lowercase() -> "A senha deve ter pelo menos 6 caracteres."
            "network error" in mensagem.lowercase() -> "Sem conexão com a internet."
            "user disabled" in mensagem.lowercase() -> "Esta conta foi desativada."
            else -> "Erro ao cadastrar: $mensagem"
        }
    }

    fun validarSenha(senha: String): String? {
        val regex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{6,}$")

        return if (!regex.matches(senha)) {
            "A senha deve ter: uma letra maiúscula, uma minúscula e um número."
        } else {
            null // Senha válida
        }
    }



    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF00A6FF)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "CADASTRO",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedTextField(
                    value = viewModel.nomeF,
                    onValueChange = { viewModel.onFieldChange("nome", it) },
                    label = { Text("Nome", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
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
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.emailF,
                    onValueChange = { viewModel.onFieldChange("email", it) },
                    label = { Text("Email", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
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
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.senhaF,
                    onValueChange = { viewModel.onFieldChange("senhaF", it) },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Senha", color = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha",
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
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.Absolute.Left,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text("Regra de Senha", color = Color.White)
                    RegrasSenhaInfo()
                }

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirmar Senha", color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle Password",
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
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {

                        if (viewModel.nomeF.isBlank() ||
                            viewModel.emailF.isBlank() ||
                            viewModel.senhaF.isBlank() ||
                            confirmPassword.isBlank()
                        ) {
                            Toast.makeText(context, "Preencha todos os campos", Toast.LENGTH_LONG).show()
                            return@Button
                        }

                        if (viewModel.senhaF != confirmPassword) {
                            Toast.makeText(context, "As senhas não coincidem", Toast.LENGTH_LONG).show()
                            return@Button
                        }

                        val erroSenha = validarSenha(viewModel.senhaF)
                        if (erroSenha != null) {
                            Toast.makeText(context, erroSenha, Toast.LENGTH_LONG).show()
                            return@Button
                        }


                        viewModel.cadastrarUsuario(
                            email = viewModel.emailF,
                            senha = viewModel.senhaF,
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Email Enviado. Valide seu email para entrar!",
                                    Toast.LENGTH_LONG
                                ).show()
                                navController.navigate("login")
                            },
                            onError = { erro ->
                                val mensagem = traduzirErroFirebase(erro)
                                Toast.makeText(context, mensagem, Toast.LENGTH_LONG).show()
                            }
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)),
                    shape = RoundedCornerShape(30.dp),
                    elevation = ButtonDefaults.buttonElevation(8.dp)
                ) {
                    Text("CADASTRAR", color = Color.White, fontWeight = FontWeight.Bold,  fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("já possui uma conta?", color = Color.White, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "login",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { navController.navigate("login") }
                    )
                }
            }
        }
    }
}


@Composable
fun RegrasSenhaInfo() {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Mostrar regras da senha",
            tint = Color.White,
            modifier = Modifier
                .size(24.dp)
                .clickable { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = 0.dp, y = 4.dp),
            modifier = Modifier.width(250.dp)
        ) {
            DropdownMenuItem(
                onClick = { expanded = false },
                text = { Text("Regras da senha:", fontWeight = FontWeight.Bold, color = Color.Black) }
            )
            DropdownMenuItem(
                onClick = { expanded = false },
                text = { Text("- Min 6 caracteres", color = Color.Black) }
            )
            DropdownMenuItem(
                onClick = { expanded = false },
                text = { Text("- Uma letra minúscula", color = Color.Black) }
            )
            DropdownMenuItem(
                onClick = { expanded = false },
                text = { Text("- Uma letra maiúscula", color = Color.Black) }
            )
            DropdownMenuItem(
                onClick = { expanded = false },
                text = { Text("- Um número", color = Color.Black) }
            )

        }
    }
}