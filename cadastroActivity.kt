package br.edu.puc.testecadastropi.Cadastro

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.edu.puc.testecadastropi.Cadastro.ui.theme.TesteCadastroPiTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import org.checkerframework.checker.units.qual.Current

// Aqui é onde vai ser a tela de cadastro
class cadastroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TesteCadastroPiTheme {
                Cadastro()
            }
        }
    }
}

@Composable
fun Cadastro(viewModel: CadastroViewModel = viewModel()) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        OutlinedTextField(
            value = viewModel.nomeF,
            onValueChange = { viewModel.onFieldChange("nome", it) },
            label = { Text("Nome") },
            modifier = Modifier
                .padding(bottom = 10.dp)
        )
        OutlinedTextField(
            value = viewModel.emailF,
            onValueChange = { viewModel.onFieldChange("email", it) },
            label = { Text("Email") },
            modifier = Modifier
                .padding(top = 10.dp)
                .padding(bottom = 10.dp)
        )
        OutlinedTextField(
            value = viewModel.senhaF,
            onValueChange = { viewModel.onFieldChange("senha", it) },
            label = { Text("Senha") },
            modifier = Modifier
                .padding(top = 10.dp)
                .padding(bottom = 10.dp)
        )
        Button(
            onClick = {
                viewModel.cadastrarUsuario(
                    email = viewModel.emailF,
                    senha = viewModel.senhaF,
                    onSuccess = {
                        Toast.makeText(
                            context,
                            "Cadastro realizado. Verifique seu e-mail!",
                            Toast.LENGTH_LONG
                        ).show()
                    },
                    onError = { erro ->
                        Toast.makeText(context, erro, Toast.LENGTH_LONG).show()
                    }
                )
                viewModel.salvarUsuario(viewModel.nomeF, viewModel.emailF, viewModel.senhaF)
            },
            modifier = Modifier.padding(all = 16.dp),
            enabled = (viewModel.nomeF.isNotBlank() && viewModel.emailF.isNotBlank() && viewModel.senhaF.isNotBlank())
        )
        {
            Text("Realizar cadastro")
        }

        //isso aqui ta deletando só do auth, mas posso fazer deletar do firestore tambem pra testar
        Button(onClick = {
            FirebaseAuth.getInstance().currentUser?.delete()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Usuário deletado com sucesso!", Toast.LENGTH_LONG)
                            .show()
                    } else {
                        Toast.makeText(
                            context,
                            "Erro ao deletar usuário: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }) {
            Text("Deletar usuário logado")
        }
    }
}
