package com.edu.puc.pi_iii_superid.data

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

// ViewModel responsável pela lógica de autenticação (login) do usuário
class LoginViewModel : ViewModel() {

    // Estados observáveis para os campos de e-mail e senha da interface (UI)
    var email = mutableStateOf("") // Guarda o valor atual do e-mail digitado
    var senha = mutableStateOf("") // Guarda o valor atual da senha digitada

    // Instância do Firebase Authentication
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Função para traduzir os erros do Firebase em mensagens mais amigáveis para o usuário
    fun traduzirErroFirebase(error: String): String {
        val erroLower = error.lowercase() // Converte o erro para minúsculo para facilitar a busca de palavras-chave

        return when {
            // Verifica se o erro contém certas palavras-chave e retorna a mensagem correspondente
            "badly formatted" in erroLower -> "E-mail inválido"
            "password is invalid" in erroLower -> "Senha incorreta"
            "no user record" in erroLower ||
                    "no user corresponding" in erroLower ||
                    "auth credential" in erroLower -> "Usuário não encontrado"
            "email not verified" in erroLower -> "E-mail não verificado. Verifique sua caixa de entrada."
            "network error" in erroLower -> "Erro de conexão. Verifique sua internet."
            "too many requests" in erroLower -> "Muitas tentativas. Tente novamente mais tarde."
            "given string is empty" in erroLower -> "Preencha todos os campos"
            else -> "Erro desconhecido: $error" // Caso não encontre nenhum dos erros acima
        }
    }

    // Função responsável por realizar o login no Firebase Authentication
    fun loginUsuario(
        onSuccess: () -> Unit,           // Callback que será executado se o login for bem-sucedido
        onError: (String) -> Unit         // Callback que será executado se houver algum erro (passa a mensagem de erro)
    ) {
        // Remove espaços em branco do e-mail e pega os valores atuais dos estados
        val emailStr = email.value.trim()
        val senhaStr = senha.value

        // Verifica se os campos estão preenchidos
        if (emailStr.isEmpty() || senhaStr.isEmpty()) {
            onError("Por favor, preencha e-mail e senha") // Retorna erro se algum campo estiver vazio
            return
        }

        // Chama o Firebase para tentar fazer login com e-mail e senha
        auth.signInWithEmailAndPassword(emailStr, senhaStr)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Se o login for bem-sucedido, verifica se o usuário foi obtido corretamente
                    val user = auth.currentUser
                    if (user != null) {
                        onSuccess() // Login concluído com sucesso
                    } else {
                        onError("Erro ao obter usuário.") // Erro inesperado: usuário não encontrado após login
                    }
                } else {
                    // Se falhar, pega a mensagem de erro do Firebase
                    val erro = task.exception?.message ?: "Erro desconhecido"
                    // Traduz a mensagem de erro e executa o callback de erro
                    onError(traduzirErroFirebase(erro))
                }
            }
    }
}
