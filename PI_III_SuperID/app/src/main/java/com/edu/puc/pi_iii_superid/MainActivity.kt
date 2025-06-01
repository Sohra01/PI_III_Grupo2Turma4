package com.example.superid

// Importações necessárias para funcionamento da MainActivity e das telas
import PreferencesManager
import android.annotation.SuppressLint
import androidx.compose.runtime.getValue
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.edu.puc.pi_iii_superid.ui.theme.PI_III_SuperIDTheme
import com.edu.puc.pi_iii_superid.ui.theme.screens.*
import com.example.superid.ui.theme.screens.*
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Ativa a funcionalidade de layout edge-to-edge
        enableEdgeToEdge()

        // Define o conteúdo da tela com Jetpack Compose
        setContent {
            // Aplica o tema do app
            PI_III_SuperIDTheme {
                setContent {
                    // Instância do FirebaseAuth
                    val auth = FirebaseAuth.getInstance()
                    // Controlador de navegação
                    val navController = rememberNavController()
                    // Contexto atual do app
                    val context = LocalContext.current
                    // Instância do PreferencesManager para acesso a preferências
                    val preferencesManager = remember { PreferencesManager(context) }
                    // Observa o estado da aceitação dos termos de uso
                    val termsAcceptedState =
                        preferencesManager.termsAccepted.collectAsState(initial = null)
                    val termsAccepted by preferencesManager.termsAccepted.collectAsState(initial = false)
                    // Estado do usuário autenticado
                    val userState = remember { mutableStateOf(auth.currentUser) }

                    // Efeito que escuta mudanças de autenticação do Firebase
                    DisposableEffect(Unit) {
                        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                            userState.value = firebaseAuth.currentUser
                        }
                        auth.addAuthStateListener(listener)
                        onDispose {
                            auth.removeAuthStateListener(listener)
                        }
                    }

                    // Define a tela inicial com base no estado atual
                    val startDestination = when {
                        termsAcceptedState.value == null -> "loading" // Ainda carregando
                        userState.value != null -> "senhaapp" // Usuário autenticado
                        termsAccepted -> "welcome" // Termos aceitos mas sem login
                        else -> "onboarding" // Primeiro acesso
                    }

                    // Se o destino inicial está definido, inicia o NavHost
                    if (startDestination != null) {
                        NavHost(
                            navController = navController,
                            startDestination = startDestination
                        ) {
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

                            // Tela de listagem de categorias
                            composable("category") {
                                CategoryScreen(navController)
                            }

                            // Tela de criação ou edição de categoria
                            composable(
                                route = "CreateOrEditCategoryScreen?isEdit={isEdit}&categoriaId={categoriaId}&nome={nome}",
                                arguments = listOf(
                                    navArgument("isEdit") {
                                        defaultValue = false; type = NavType.BoolType
                                    },
                                    navArgument("categoriaId") {
                                        defaultValue = ""; type = NavType.StringType
                                    },
                                    navArgument("nome") {
                                        defaultValue = ""; type = NavType.StringType
                                    }
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

                            // Tela de senhas por categoria
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

                            // Tela de envio de email de recuperação
                            composable(
                                "sendmail/{email}",
                                arguments = listOf(navArgument("email") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val email = backStackEntry.arguments?.getString("email") ?: ""
                                SendMailScreen(navController, email)
                            }

                            // Tela para inserir e-mail de recuperação
                            composable("recoverymail") {
                                RecoveryMailScreen(navController)
                            }

                            // Tela de introdução (primeiro acesso)
                            composable("onboarding") {
                                OnBoardingScreen(navController)
                            }

                            // Tela com instruções ou guia de uso
                            composable("guide") {
                                GuideScreen(navController)
                            }

                            // Tela dos termos de uso
                            composable("termsofuse") {
                                TermsOfUseScreen(navController, preferencesManager)
                            }

                            // Tela de carregamento
                            composable("loading") {
                                LoadingScreen()
                            }

                            // Tela da câmera (provavelmente para leitura de QR Code)
                            composable("camera") {
                                CameraScreen(navController)
                            }

                            // Tela inicial com verificação de senha
                            composable("senhaapp") {
                                PasswordAppScreen(
                                    onSenhaCorreta = { navController.navigate("category") },
                                    onErro = { /* mostrar erro ou feedback */ }
                                )
                            }
                        }
                    } else {
                        // Caso o destino inicial esteja nulo, exibe a tela de carregamento
                        LoadingScreen()
                    }
                }
            }
        }
    }
}
