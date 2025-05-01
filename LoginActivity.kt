package br.edu.puc.testecadastropi.Login

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.puc.testecadastropi.ui.theme.TesteCadastroPiTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TesteCadastroPiTheme {
                    LoginUi()
                }
            }
        }
    }

@Composable
fun LoginUi(viewModel: LoginViewModel = viewModel())
{
    val context = LocalContext.current
    val recuperarIntent = Intent(context, )
    Column (modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        )
    {
        OutlinedTextField(
            value = viewModel.emailP,
            onValueChange = {viewModel.onFieldChange("email",it)},
            label = { Text("Email") },
            modifier = Modifier
                .padding(all = 16.dp)

        )
        OutlinedTextField(
            value = viewModel.senhaP,
            onValueChange = {viewModel.onFieldChange("senha",it)},
            label = { Text("Senha") },
            modifier = Modifier
                .padding(all = 16.dp)
        )
        TextButton(onClick = {

        },
            modifier = Modifier
                .padding(10.dp),
        ){Text("Esqueci minha senha") }
    }
        Button(onClick = {
            //pode ser que isso tenha que retornar booleano
            viewModel.fazerLogin(viewModel.emailP, viewModel.senhaP) {sucesso ->
                if (sucesso) {
                    Toast.makeText(context, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    //aqui o intent pra próxima activity
                } else{
                    Toast.makeText(context,"Email ou senha inválidos", Toast.LENGTH_SHORT).show()
                }
            }
        },
            modifier = Modifier
                .padding(all = 30.dp)
        ) {
            Text("Login")
        }
    }
