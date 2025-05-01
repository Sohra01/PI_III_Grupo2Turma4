package br.edu.puc.testecadastropi.Cadastro

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import br.edu.puc.testecadastropi.usuarioclass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class CadastroViewModel : ViewModel()
{
    var nomeF by mutableStateOf("")
        private set

    var emailF by mutableStateOf("")
        private set

    var senhaF by mutableStateOf("")
        private set

    fun onFieldChange (field: String, texto: String)
    {
        when (field)
        {
            "nome" -> nomeF = texto
            "email" -> emailF = texto
            "senha" -> senhaF = texto
        }
    }

    private val auth = FirebaseAuth.getInstance()
    fun cadastrarUsuario(email: String, senha: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //pega o usuário logado
                    val user = FirebaseAuth.getInstance().currentUser

                    //envia o e-mail de verificação
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { emailTask ->
                            if (emailTask.isSuccessful) {
                                onSuccess()
                            } else {
                                onError("Cadastro feito, mas erro ao enviar e-mail de verificação: ${emailTask.exception?.message}")
                            }
                        }

                } else {
                    onError(task.exception?.message ?: "Erro desconhecido ao cadastrar.")
                }
            }
    }

    fun salvarUsuario(nome:String, email:String, senha:String)
    {
        val db = FirebaseFirestore.getInstance()

        val usuariocadastro =  usuarioclass(nome,email,senha)

        db.collection("usuarios")
            .add(usuariocadastro)
            .addOnSuccessListener { documentReference ->
                println("Usuário adicionado com ID: ${documentReference.id}")
            }
            .addOnFailureListener{ e ->
                println("Erro ao adicionar usuário: $e")
            }
    }

}

