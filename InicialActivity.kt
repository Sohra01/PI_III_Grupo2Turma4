package br.edu.puc.testecadastropi.telaInicial

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import br.edu.puc.testecadastropi.Cadastro.cadastroActivity
import br.edu.puc.testecadastropi.Login.LoginActivity
import br.edu.puc.testecadastropi.telaInicial.ui.theme.TesteCadastroPiTheme
import org.checkerframework.checker.units.qual.Current

class InicialActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TesteCadastroPiTheme {
                TelaInicial()
            }
        }
    }
}


//Aqui está a tela de inicio que mostra o como usar, termos de uso e a apresentação.

@Composable
fun TelaInicial()
{
    val context = LocalContext.current
    val cadastroIntent = Intent(context, cadastroActivity::class.java)
    val loginIntent = Intent(context, LoginActivity::class.java)
    //val loginIntent = Intent(context, ) terminar o da login aqui
    Column (modifier = Modifier
        .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        Button(onClick =
        {
            context.startActivity(cadastroIntent)
        },
        modifier = Modifier.padding(all = 16.dp)
        )
        {
        Text("Cadastro")
        }

        Button(onClick =
            {
                context.startActivity(loginIntent)
            },
            modifier = Modifier.padding(all = 16.dp))
        {
            Text("Login")
        }
    }
}