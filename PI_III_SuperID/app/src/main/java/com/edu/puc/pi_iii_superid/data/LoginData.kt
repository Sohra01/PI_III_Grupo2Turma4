package com.edu.puc.pi_iii_superid.data

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

// ViewModel responsável pela lógica de autenticação do login
class LoginViewModel : ViewModel() {

    // Estados mutáveis para email e senha que serão observados pela UI com Compose
    var email = mutableStateOf("")
    var senha = mutableStateOf("")

    // Instância do FirebaseAuth para autenticação de usuários
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Função que traduz mensagens de erro do Firebase para mensagens mais amigáveis ao usuário
    fun traduzirErroFirebase(error: String): String {
        val erroLower = error.lowercase() // Converte erro para minúsculas para facilitar a verificação

        return when {
            // Erro de e-mail mal formatado
            "badly formatted" in erroLower -> "E-mail inválido"

            // Erro de senha incorreta
            "password is invalid" in erroLower -> "Senha incorreta"

            // Erros relacionados a usuário não encontrado
            "no user record" in erroLower ||
            "no user corresponding" in erroLower ||
            "auth credential" in erroLower -> "Usuário não encontrado"

            // E-mail não verificado
            "email not verified" in erroLower -> "E-mail não verificado. Verifique sua caixa de entrada."

            // Erro de conexão com a internet
            "network error" in erroLower -> "Erro de conexão. Verifique sua internet."

            // Erro de muitas tentativas consecutivas
            "too many requests" in erroLower -> "Muitas tentativas. Tente novamente mais tarde."

            // Campos de entrada vazios
            "given string is empty" in erroLower -> "Preencha todos os campos"

            // Qualquer outro erro não identificado especificamente
            else -> "Erro desconhecido: $error"
        }
    }

    // Função que realiza o login do usuário usando e-mail e senha com Firebase Authentication
    fun loginUsuario(
        onSuccess: () -> Unit,         // Callback chamado em caso de sucesso
        onError: (String) -> Unit      // Callback chamado em caso de erro, com mensagem
    ) {
        val emailStr = email.value.trim() // Remove espaços antes/depois do e-mail
        val senhaStr = senha.value        // Obtém valor da senha

        // Verifica se os campos estão preenchidos
        if (emailStr.isEmpty() || senhaStr.isEmpty()) {
            onError("Por favor, preencha e-mail e senha")
            return
        }

        // Tenta autenticar o usuário com FirebaseAuth
        auth.signInWithEmailAndPassword(emailStr, senhaStr)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        onSuccess() // Login bem-sucedido
                    } else {
                        onError("Erro ao obter usuário.") // Algo deu errado ao recuperar o usuário logado
                    }
                } else {
                    // Em caso de falha, obtém a mensagem de erro e traduz
                    val erro = task.exception?.message ?: "Erro desconhecido"
                    onError(traduzirErroFirebase(erro))
                }
            }
    }
}
