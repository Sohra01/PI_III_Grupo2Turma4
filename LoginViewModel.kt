package br.edu.puc.testecadastropi.Login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

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

}