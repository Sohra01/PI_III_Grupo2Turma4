package br.edu.puc.testecadastropi.Login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import br.edu.puc.testecadastropi.usuarioclass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.callbackFlow

class LoginViewModel : ViewModel() {
    var emailP by mutableStateOf("")
        private set

    var senhaP by mutableStateOf("")
        private set

    fun onFieldChange(field: String, texto: String) {
        when (field) {
            "email" -> emailP = texto
            "senha" -> senhaP = texto
        }
    }

    fun fazerLogin(email: String, senha: String, callback: (Boolean, String?) -> Unit) {
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null && user.isEmailVerified) {
                        callback(true, null) // login OK
                    } else {
                        callback(
                            false,
                            "E-mail não verificado. Verifique seu e-mail antes de fazer login."
                        )
                    }
                } else {
                    val errorMessage = task.exception?.message ?: "Erro ao fazer login."
                    callback(false, errorMessage)
                }
            }
    }
}
