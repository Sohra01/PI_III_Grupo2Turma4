package com.edu.puc.pi_iii_superid.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

// ViewModel responsável pela lógica de cadastro de usuários
class CadastroViewModel : ViewModel() {

    // Variável observável para o nome
    var nomeF by mutableStateOf("")
        private set // Só pode ser alterada dentro do ViewModel

    // Variável observável para o email
    var emailF by mutableStateOf("")
        private set

    // Variável observável para a senha
    var senhaF by mutableStateOf("")
        private set

    // Função que atualiza os campos de nome, email ou senha com base no parâmetro 'campo'
    fun onFieldChange(campo: String, valor: String) {
        when (campo) {
            "nome" -> nomeF = valor
            "email" -> emailF = valor
            "senhaF" -> senhaF = valor
        }
    }

    // Função responsável por realizar o cadastro do usuário
    fun cadastrarUsuario(
        email: String, // Email informado
        senha: String, // Senha informada
        onSuccess: () -> Unit, // Callback chamado em caso de sucesso
        onError: (String) -> Unit // Callback chamado em caso de erro (retorna a mensagem de erro)
    ) {
        // Instancia o Firebase Authentication e o Firestore
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        // Verifica se já existe um usuário com o e-mail informado
        auth.fetchSignInMethodsForEmail(email).addOnSuccessListener { result ->
            val signInMethods = result.signInMethods
            if (!signInMethods.isNullOrEmpty()) {
                // Se o e-mail já estiver cadastrado, retorna erro
                onError("Este e-mail já está cadastrado.")
                return@addOnSuccessListener
            }

            // Cria o usuário no Firebase Authentication
            auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        // Atualiza o perfil do usuário com o nome (displayName)
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(nomeF)
                            .build()

                        user?.updateProfile(profileUpdates)
                        user?.sendEmailVerification() // Envia e-mail de verificação

                        // Dados do usuário a serem salvos no Firestore
                        val userData = hashMapOf(
                            "nome" to nomeF,
                            "email" to email
                        )

                        // Salva os dados do usuário no Firestore na coleção 'usuarios'
                        user?.uid?.let { uid ->
                            firestore.collection("usuarios").document(uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    // Após salvar os dados do usuário, cria categorias padrão

                                    val categorias = listOf(
                                        "Site Web",
                                        "Aplicativo",
                                        "Teclado de Acesso Físico"
                                    )

                                    // Cria uma lista de tarefas para adicionar as categorias
                                    val tasks = categorias.map { categoria ->
                                        val catData = hashMapOf("nome" to categoria)
                                        firestore.collection("usuarios")
                                            .document(uid)
                                            .collection("categorias")
                                            .document(categoria) // O ID do documento é o nome da categoria
                                            .set(catData)
                                    }

                                    // Aguarda todas as tarefas de criação de categorias finalizarem
                                    com.google.android.gms.tasks.Tasks.whenAllComplete(tasks)
                                        .addOnSuccessListener {
                                            // Se todas as categorias foram criadas com sucesso
                                            onSuccess()
                                        }
                                        .addOnFailureListener { e ->
                                            // Se deu erro ao criar alguma categoria
                                            onError("Erro ao criar categorias: ${e.message}")
                                        }

                                }
                                .addOnFailureListener { e ->
                                    // Erro ao salvar dados do usuário no Firestore
                                    onError("Erro ao salvar dados no Firestore: ${e.message}")
                                }
                        }
                    } else {
                        // Erro ao criar usuário no Authentication
                        onError("Erro ao criar usuário: ${task.exception?.message}")
                    }
                }
        }.addOnFailureListener {
            // Erro na verificação de e-mail (antes de criar o usuário)
            onError("Erro ao verificar e-mail: ${it.message}")
        }
    }
}
