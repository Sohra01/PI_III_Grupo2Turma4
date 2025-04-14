package br.edu.puc.testecadastropi.Login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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
    Column (modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
        )
    {
        OutlinedTextField(
            value = viewModel.emailP,
            onValueChange = {viewModel.onFieldChange("email",it)}
        )
        OutlinedTextField(
            value = viewModel.senhaP,
            onValueChange = {viewModel.onFieldChange("senha",it)}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    TesteCadastroPiTheme {
        LoginUi()
    }
}