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
import com.edu.puc.pi_iii_superid.ui.theme.screens.PasswordScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.RecoveryMailScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.SendMailScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.TermsOfUseScreen
import com.example.superid.ui.theme.screens.CategoryScreen
import com.example.superid.ui.theme.screens.LoginScreen
import com.example.superid.ui.theme.screens.SignUpScreen
import com.example.superid.ui.theme.screens.WelcomeScreen
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            PI_III_SuperIDTheme  {
                val navController = rememberNavController()
                val context = LocalContext.current
                val preferencesManager = remember { PreferencesManager(context) }
                val termsAcceptedState = preferencesManager.termsAccepted.collectAsState(initial = null)
                val termsAccepted by preferencesManager.termsAccepted.collectAsState(initial = false)
                val user = FirebaseAuth.getInstance().currentUser

                // Determina a tela inicial com base no estado do usuário
                val startDestination = when {
                    termsAcceptedState.value == null -> "loading"
                    user != null && user.isEmailVerified -> "category"
                    termsAccepted -> "welcome"
                    else -> "onboarding"
                }

                if (startDestination != null) {
                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("welcome") {
                            WelcomeScreen(
                                onLoginClick = { navController.navigate("login") },
                                onSignUpClick = { navController.navigate("signup") }
                            )
                        }
                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                onSignUpClick = {
                                    navController.navigate("signup") //
                                },
                                onForgotPasswordClick = {
                                    navController.navigate("recoverymail")
                                }
                            )
                        }
                        composable("signup") {
                            SignUpScreen(navController = navController)
                        }
                        composable("category") {
                            CategoryScreen(navController)
                        }
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


                        composable(
                            "sendmail/{email}",
                            arguments = listOf(navArgument("email") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email") ?: ""
                            SendMailScreen(navController, email)
                        }

                        composable("recoverymail") {
                            RecoveryMailScreen(navController)
                        }

                        composable("onboarding") {
                            OnBoardingScreen(navController)
                        }
                        composable("guide") {
                            GuideScreen(navController)
                        }
                        composable("termsofuse") {
                            TermsOfUseScreen(navController, preferencesManager)
                        }
                        composable("loading") {
                            LoadingScreen()
                        }
                        composable("camera") {
                            CameraScreen(navController)
                        }

                    }

                } else {
                    LoadingScreen()
                }
            }
        }
    }
}