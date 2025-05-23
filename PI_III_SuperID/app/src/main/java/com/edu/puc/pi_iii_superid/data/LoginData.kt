package com.edu.puc.pi_iii_superid.data

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {

    // Estados para email e senha que serão usados na UI
    var email = mutableStateOf("")
    var senha = mutableStateOf("")

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Função para traduzir erros do Firebase (exemplo simples, você pode adaptar)
    fun traduzirErroFirebase(error: String): String {
        val erroLower = error.lowercase()

        return when {
            "badly formatted" in erroLower -> "E-mail inválido"
            "password is invalid" in erroLower -> "Senha incorreta"
            "no user record" in erroLower ||
                    "no user corresponding" in erroLower ||
                    "auth credential" in erroLower -> "Usuário não encontrado"
            "email not verified" in erroLower -> "E-mail não verificado. Verifique sua caixa de entrada."
            "network error" in erroLower -> "Erro de conexão. Verifique sua internet."
            "too many requests" in erroLower -> "Muitas tentativas. Tente novamente mais tarde."
            "given string is empty" in erroLower -> "Preencha todos os campos"
            else -> "Erro desconhecido: $error"
        }
    }

    // Função para realizar login no Firebase Authentication
    fun loginUsuario(
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val emailStr = email.value.trim()
        val senhaStr = senha.value

        if (emailStr.isEmpty() || senhaStr.isEmpty()) {
            onError("Por favor, preencha e-mail e senha")
            return
        }

        auth.signInWithEmailAndPassword(emailStr, senhaStr)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        onSuccess()
                    } else {
                        onError("E-mail não verificado. Verifique seu e-mail antes de entrar.")
                    }
                } else {
                    val erro = task.exception?.message ?: "Erro desconhecido"
                    onError(traduzirErroFirebase(erro))
                }
            }
    }
}