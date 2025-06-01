package com.edu.puc.pi_iii_superid.ui.theme.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun RecoveryMailScreen(
    navController: NavHostController // Controlador de navegação para troca de telas
) {
    val context = LocalContext.current // Contexto atual para mostrar Toasts
    var email by remember { mutableStateOf("") } // Estado que armazena o texto do email digitado

    // Função que verifica no Firestore se o email existe e se está verificado
    fun verificarEmailNoFirestore(
        email: String,
        context: Context,
        onPodeRecuperar: () -> Unit, // Callback para caso o email esteja verificado e possa recuperar senha
        onEmailNaoVerificado: () -> Unit, // Callback para email não verificado
        onNaoExiste: () -> Unit // Callback para email que não existe no banco
    ) {
        val db = FirebaseFirestore.getInstance() // Instância do Firestore
        db.collection("usuarios") // Coleção "usuarios"
            .whereEqualTo("email", email) // Filtra pelo email informado
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) { // Se encontrou algum documento com esse email
                    val usuario = result.documents.first() // Pega o primeiro usuário encontrado
                    val emailVerificado = usuario.getBoolean("emailVerificado") ?: false // Verifica campo "emailVerificado"

                    if (emailVerificado) {
                        onPodeRecuperar() // Chama callback para permitir recuperação
                    } else {
                        onEmailNaoVerificado() // Chama callback para email não verificado
                    }
                } else {
                    onNaoExiste() // Chama callback para email não encontrado
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Erro ao verificar e-mail", Toast.LENGTH_SHORT).show() // Toast em caso de erro na consulta
            }
    }

    // Função que envia o link de recuperação de senha pelo Firebase Auth
    fun enviarLinkDeRecuperacao(email: String, context: Context) {
        val auth = FirebaseAuth.getInstance()

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Link enviado para o e-mail!", Toast.LENGTH_LONG).show() // Sucesso no envio
                } else {
                    val errorMsg = task.exception?.message ?: "Erro desconhecido"
                    Toast.makeText(context, "Erro: $errorMsg", Toast.LENGTH_LONG).show() // Exibe mensagem de erro detalhada
                }
            }
    }

    // Função chamada ao clicar no botão "ENVIAR"
    fun onEnviarClick(email: String) {
        verificarEmailNoFirestore(
            email,
            context,
            onPodeRecuperar = {
                enviarLinkDeRecuperacao(email, context) // Se permitido, envia email de recuperação
                navController.navigate("sendmail/${email}") // Navega para tela de confirmação de envio
            },
            onEmailNaoVerificado = {
                Toast.makeText(context, "E-mail não verificado. Verifique sua caixa de entrada.", Toast.LENGTH_LONG).show() // Feedback ao usuário
            },
            onNaoExiste = {
                Toast.makeText(context, "E-mail não encontrado", Toast.LENGTH_SHORT).show() // Feedback ao usuário
            }
        )
    }

    // UI da tela
    Box(
        modifier = Modifier
            .fillMaxSize() // Preenche toda tela
            .background(Color(0xFF0096FF)) // Fundo azul similar à imagem referenciada
            .padding(24.dp),
        contentAlignment = Alignment.Center // Centraliza conteúdo dentro da Box
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // Centraliza elementos da coluna horizontalmente
            modifier = Modifier.fillMaxWidth() // Ocupa toda a largura possível
        ) {
            // Título principal da tela
            Text(
                text = "EMAIL PARA RECUPERAÇÃO\nDE SENHA",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 40.dp) // Espaço abaixo do texto
            )

            // Campo de texto para o usuário digitar o email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it }, // Atualiza o estado ao digitar
                placeholder = { Text("Email", color = Color.White.copy(alpha = 0.7f)) }, // Texto de dica com transparência
                singleLine = true, // Linha única
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email) // Teclado otimizado para email
            )

            Spacer(modifier = Modifier.height(40.dp)) // Espaço entre o campo e o botão

            // Botão para enviar o link de recuperação
            Button(
                onClick = { onEnviarClick(email) }, // Chama a função ao clicar
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)),
                shape = RoundedCornerShape(30.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "ENVIAR",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(30.dp)) // Espaço antes do botão "Voltar"

            // Botão para voltar à tela anterior
            Button(
                onClick = { navController.popBackStack() }, // Volta uma tela na navegação
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF004A8F)),
                shape = RoundedCornerShape(30.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Text(
                    text = "VOLTAR",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
