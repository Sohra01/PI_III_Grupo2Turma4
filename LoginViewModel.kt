package br.edu.puc.testecadastropi.Login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import br.edu.puc.testecadastropi.usuarioclass
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.callbackFlow

class LoginViewModel : ViewModel()
{
    var emailP by mutableStateOf("")
        private set

    var senhaP by mutableStateOf("")
        private set

    fun onFieldChange(field : String, texto : String)
    {
        when(field)
        {
            "email" -> emailP = texto
            "senha" -> senhaP = texto
        }
    }

    //pode ser que isso tenha que retornar booleano
    fun fazerLogin(email : String, senha : String, callback : (Boolean) -> Unit)
    {
        val db = FirebaseFirestore.getInstance()

        db.collection("usuarios")
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty){
                    val usuario = result.documents[0].toObject(usuarioclass::class.java)

                    if (usuario != null && usuario.senha == senha){
                        callback(true)
                    } else {
                        callback(false)
                        //senha incorreta
                    }
                }else {
                    callback(false)
                    //nenhum usuário com o email
                }
            }
            .addOnFailureListener { e ->
                println("Erro ao buscar usuário: $e")
            }
    }
}
