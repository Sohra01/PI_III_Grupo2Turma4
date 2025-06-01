package com.edu.puc.pi_iii_superid.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

// ViewModel responsável pelo cadastro de novos usuários
class CadastroViewModel : ViewModel() {

    // Estados para armazenar os dados inseridos nos campos de nome, e-mail e senha
    var nomeF by mutableStateOf("")
        private set

    var emailF by mutableStateOf("")
        private set

    var senhaF by mutableStateOf("")
        private set

    // Função chamada sempre que um campo de input for alterado
    fun onFieldChange(campo: String, valor: String) {
        when (campo) {
            "nome" -> nomeF = valor
            "email" -> emailF = valor
            "senhaF" -> senhaF = valor
        }
    }

    // Função principal para cadastrar um novo usuário no Firebase
    fun cadastrarUsuario(
        email: String,
        senha: String,
        onSuccess: () -> Unit,         // Callback chamado em caso de sucesso
        onError: (String) -> Unit      // Callback chamado em caso de erro com uma mensagem
    ) {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        // Verifica se o e-mail já está cadastrado
        auth.fetchSignInMethodsForEmail(email).addOnSuccessListener { result ->
            val signInMethods = result.signInMethods
            if (!signInMethods.isNullOrEmpty()) {
                onError("Este e-mail já está cadastrado.")
                return@addOnSuccessListener
            }

            // Cria o usuário com e-mail e senha
            auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        // Atualiza o nome do perfil do usuário
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(nomeF)
                            .build()

                        user?.updateProfile(profileUpdates)
                        user?.sendEmailVerification() // Envia e-mail de verificação

                        // Dados que serão armazenados no Firestore
                        val userData = hashMapOf(
                            "nome" to nomeF,
                            "email" to email
                        )

                        // Salva os dados do usuário no Firestore
                        user?.uid?.let { uid ->
                            firestore.collection("usuarios").document(uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    // Define categorias padrão para o usuário
                                    val categorias = listOf("Site Web", "Aplicativo", "Teclado de Acesso Físico")

                                    // Lista de tarefas para criar cada categoria
                                    val tasks = categorias.map { categoria ->
                                        val catData = hashMapOf("nome" to categoria)
                                        firestore.collection("usuarios")
                                            .document(uid)
                                            .collection("categorias")
                                            .document(categoria) // ID do documento será o nome da categoria
                                            .set(catData)
                                    }

                                    // Aguarda a conclusão de todas as tarefas de criação de categorias
                                    com.google.android.gms.tasks.Tasks.whenAllComplete(tasks)
                                        .addOnSuccessListener {
                                            onSuccess()
                                        }
                                        .addOnFailureListener { e ->
                                            onError("Erro ao criar categorias: ${e.message}")
                                        }

                                }
                                .addOnFailureListener { e ->
                                    onError("Erro ao salvar dados no Firestore: ${e.message}")
                                }
                        }
                    } else {
                        onError("Erro ao criar usuário: ${task.exception?.message}")
                    }
                }
        }.addOnFailureListener {
            onError("Erro ao verificar e-mail: ${it.message}")
        }
    }
}
