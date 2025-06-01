package com.edu.puc.pi_iii_superid.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class CadastroViewModel : ViewModel() {
    var nomeF by mutableStateOf("")
        private set

    var emailF by mutableStateOf("")
        private set

    var senhaF by mutableStateOf("")
        private set

    fun onFieldChange(campo: String, valor: String) {
        when (campo) {
            "nome" -> nomeF = valor
            "email" -> emailF = valor
            "senhaF" -> senhaF = valor
        }
    }



    fun cadastrarUsuario(
        email: String,
        senha: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        auth.fetchSignInMethodsForEmail(email).addOnSuccessListener { result ->
            val signInMethods = result.signInMethods
            if (!signInMethods.isNullOrEmpty()) {
                onError("Este e-mail já está cadastrado.")
                return@addOnSuccessListener
            }

            auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(nomeF)
                            .build()

                        user?.updateProfile(profileUpdates)
                        user?.sendEmailVerification()

                        val userData = hashMapOf(
                            "nome" to nomeF,
                            "email" to email
                        )

                        user?.uid?.let { uid ->
                            firestore.collection("usuarios").document(uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    val categorias = listOf("Site Web", "Aplicativo", "Teclado de Acesso Físico")

                                    // Lista de tasks para criação das categorias
                                    val tasks = categorias.map { categoria ->
                                        val catData = hashMapOf("nome" to categoria)
                                        firestore.collection("usuarios")
                                            .document(uid)
                                            .collection("categorias")
                                            .document(categoria) // ID do doc = nome da categoria
                                            .set(catData)
                                    }

                                    // Aguarda todas as tasks terminarem
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
