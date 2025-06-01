package com.example.superid

import PreferencesManager
import androidx.compose.runtime.getValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.edu.puc.pi_iii_superid.ui.theme.PI_III_SuperIDTheme
import com.edu.puc.pi_iii_superid.ui.theme.screens.CameraScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.CreateOrEditCategoryScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.CreateOrEditPasswordScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.GuideScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.LoadingScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.OnBoardingScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.PasswordAppScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.PasswordScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.RecoveryMailScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.SendMailScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.TermsOfUseScreen
import com.example.superid.ui.theme.screens.CategoryScreen
import com.example.superid.ui.theme.screens.LoginScreen
import com.example.superid.ui.theme.screens.SignUpScreen
import com.example.superid.ui.theme.screens.WelcomeScreen
import com.google.firebase.auth.FirebaseAuth

// Classe principal da aplicação, herdando de ComponentActivity
class MainActivity : ComponentActivity() {

    // Função chamada quando a Activity é criada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ativa o suporte para Edge-to-Edge (tela cheia)
        enableEdgeToEdge()

        // Define o conteúdo da tela usando Compose
        setContent {
            // Aplica o tema personalizado da aplicação
            PI_III_SuperIDTheme {
                // Cria o controlador de navegação
                val navController = rememberNavController()

                // Obtém o contexto atual
                val context = LocalContext.current

                // Instancia o gerenciador de preferências
                val preferencesManager = remember { PreferencesManager(context) }

                // Observa o estado de aceite dos termos (valor pode ser nulo enquanto carrega)
                val termsAcceptedState = preferencesManager.termsAccepted.collectAsState(initial = null)

                // Observa se os termos foram aceitos (valor booleano)
                val termsAccepted by preferencesManager.termsAccepted.collectAsState(initial = false)

                // Verifica se há um usuário logado no Firebase
                val user = FirebaseAuth.getInstance().currentUser

                // Define a tela inicial com base nas condições do usuário
                val startDestination = when {
                    termsAcceptedState.value == null -> "loading" // Se ainda está carregando as preferências
                    user != null -> "senhaapp"                   // Se o usuário está logado, vai para a senha do app
                    termsAccepted -> "welcome"                   // Se aceitou os termos, mostra a tela de boas-vindas
                    else -> "onboarding"                         // Caso contrário, vai para o onboarding
                }

                // Verifica se há uma tela inicial definida
                if (startDestination != null) {
                    // Define o NavHost com o controlador de navegação e a tela inicial
                    NavHost(navController = navController, startDestination = startDestination) {

                        // Tela de boas-vindas
                        composable("welcome") {
                            WelcomeScreen(
                                onLoginClick = { navController.navigate("login") },
                                onSignUpClick = { navController.navigate("signup") }
                            )
                        }

                        // Tela de login
                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                onSignUpClick = { navController.navigate("signup") },
                                onForgotPasswordClick = { navController.navigate("recoverymail") }
                            )
                        }

                        // Tela de cadastro
                        composable("signup") {
                            SignUpScreen(navController = navController)
                        }

                        // Tela de categorias
                        composable("category") {
                            CategoryScreen(navController)
                        }

                        // Tela de criar ou editar categoria
                        composable(
                            route = "CreateOrEditCategoryScreen?isEdit={isEdit}&categoriaId={categoriaId}&nome={nome}",
                            arguments = listOf(
                                navArgument("isEdit") { defaultValue = false; type = NavType.BoolType },
                                navArgument("categoriaId") { defaultValue = ""; type = NavType.StringType },
                                navArgument("nome") { defaultValue = ""; type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val isEdit = backStackEntry.arguments?.getBoolean("isEdit") ?: false
                            val categoriaId = backStackEntry.arguments?.getString("categoriaId")
                            val nome = backStackEntry.arguments?.getString("nome") ?: ""

                            CreateOrEditCategoryScreen(
                                navController = navController,
                                isEdit = isEdit,
                                categoriaId = categoriaId,
                                nomeCategoriaInicial = nome
                            )
                        }

                        // Tela que exibe as senhas de uma categoria específica
                        composable(
                            route = "passwords/{categoriaId}/{categoriaNome}",
                            arguments = listOf(
                                navArgument("categoriaId") { type = NavType.StringType },
                                navArgument("categoriaNome") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val categoriaId = backStackEntry.arguments?.getString("categoriaId") ?: ""
                            val categoriaNome = backStackEntry.arguments?.getString("categoriaNome") ?: ""

                            PasswordScreen(
                                navController = navController,
                                categoriaNome = categoriaNome,
                                categoria = categoriaId
                            )
                        }

                        // Tela de criação ou edição de senha
                        composable(
                            "CreateOrEditPasswordScreen?isEdit={isEdit}&categoria={categoria}&categoriaNome={categoriaNome}&senhaId={senhaId}",
                            arguments = listOf(
                                navArgument("isEdit") {
                                    type = NavType.BoolType
                                    defaultValue = false
                                },
                                navArgument("categoria") {
                                    type = NavType.StringType
                                    defaultValue = "Site"
                                },
                                navArgument("categoriaNome") {
                                    type = NavType.StringType
                                    defaultValue = "Site"
                                },
                                navArgument("senhaId") {
                                    type = NavType.StringType
                                    defaultValue = ""
                                    nullable = true
                                }
                            )
                        ) { backStackEntry ->
                            val isEdit = backStackEntry.arguments?.getBoolean("isEdit") ?: false
                            val categoria = backStackEntry.arguments?.getString("categoria") ?: "Site"
                            val categoriaNome = backStackEntry.arguments?.getString("categoriaNome") ?: "Site"
                            val senhaId = backStackEntry.arguments?.getString("senhaId")

                            CreateOrEditPasswordScreen(
                                navController = navController,
                                isEdit = isEdit,
                                categoria = categoria,
                                categoriaNome = categoriaNome,
                                senhaId = senhaId
                            )
                        }

                        // Tela de envio de email para recuperação de senha
                        composable(
                            "sendmail/{email}",
                            arguments = listOf(navArgument("email") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            SendMailScreen(navController, email)
                        }

                        // Tela para inserir o email de recuperação
                        composable("recoverymail") {
                            RecoveryMailScreen(navController)
                        }

                        // Tela de introdução (onboarding)
                        composable("onboarding") {
                            OnBoardingScreen(navController)
                        }

                        // Tela de guia de uso
                        composable("guide") {
                            GuideScreen(navController)
                        }

                        // Tela dos termos de uso
                        composable("termsofuse") {
                            TermsOfUseScreen(navController, preferencesManager)
                        }

                        // Tela de carregamento (loading)
                        composable("loading") {
                            LoadingScreen()
                        }

                        // Tela da câmera (provavelmente para leitura de QR Code ou algo similar)
                        composable("camera") {
                            CameraScreen(navController)
                        }

                        // Tela de senha do aplicativo (antes de acessar as senhas salvas)
                        composable("senhaapp") {
                            PasswordAppScreen(
                                onSenhaCorreta = { navController.navigate("category") }, // Se senha correta, vai para categorias
                                onErro = { /* Aqui pode ser exibido algum feedback de erro */ }
                            )
                        }
                    }
                } else {
                    // Se não tiver uma rota inicial, exibe tela de carregamento
                    LoadingScreen()
                }
            }
        }
    }
}
