package com.example.superid

import PreferencesManager
import androidx.compose.runtime.getValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.edu.puc.pi_iii_superid.ui.theme.PI_III_SuperIDTheme
import com.edu.puc.pi_iii_superid.ui.theme.screens.CreateOrEditCategoryScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.CreateOrEditPasswordScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.GuideScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.OnBoardingScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.PasswordScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.RecoveryMailScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.ResetPasswordScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.SendMailScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.TermsOfUseScreen
import com.edu.puc.pi_iii_superid.ui.theme.screens.VerifiedMailScreen
import com.example.superid.ui.theme.screens.CategoryScreen
import com.example.superid.ui.theme.screens.LoginScreen
import com.example.superid.ui.theme.screens.SignUpScreen
import com.example.superid.ui.theme.screens.WelcomeScreen

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
                val startDestination = if (termsAccepted) "welcome" else "onboarding"

                if (termsAcceptedState.value == null) {
                    // Dado ainda não carregou — mostra uma tela de carregamento (pode ser só uma Box simples)
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("welcome") {
                            WelcomeScreen(
                                onLoginClick = { navController.navigate("login") },
                                onSignUpClick = { navController.navigate("signup") }
                            )
                        }
                        composable("login") {
                            LoginScreen(
                                onLoginClick = {
                                    navController.navigate("category")
                                },
                                onSignUpClick = {
                                    navController.navigate("signup") //
                                },
                                onForgotPasswordClick = {
                                    navController.navigate("recoverymail")
                                }
                            )
                        }
                        composable("signup") {
                            SignUpScreen(
                                onSignUpClick = { navController.navigate("login") },
                                onLoginClick = { navController.navigate("login") }
                            )
                        }
                        composable("category") {
                            CategoryScreen(navController)
                        }
                        composable(
                            "CreateOrEditCategoryScreen?isEdit={isEdit}",
                            arguments = listOf(navArgument("isEdit") {
                                defaultValue = true
                                type = NavType.BoolType
                            })
                        ) {
                            val isEdit = it.arguments?.getBoolean("isEdit") != false
                            CreateOrEditCategoryScreen(
                                navController = navController,
                                isEdit = isEdit
                            )
                        }
                        composable("passwords/{categoria}") { backStackEntry ->
                            val categoria = backStackEntry.arguments?.getString("categoria") ?: ""
                            PasswordScreen(navController, categoria)
                        }
                        composable(
                            "CreateOrEditPasswordScreen?isEdit={isEdit}",
                            arguments = listOf(navArgument("isEdit") {
                                defaultValue = true
                                type = NavType.BoolType
                            })
                        ) {
                            val isEdit = it.arguments?.getBoolean("isEdit") != false
                            CreateOrEditPasswordScreen(
                                navController = navController,
                                isEdit = isEdit
                            )
                        }
                        composable("sendmail") {
                            SendMailScreen(navController)
                        }
                        composable("recoverymail") {
                            RecoveryMailScreen(
                                navController,
                                onEnviarClick = { navController.navigate("sendmail") })
                        }
                        composable("resetpassword") {
                            ResetPasswordScreen(
                                onSalvarClick = { navController.navigate("login") },
                                onCancelarClick = { navController.navigate("login") }
                            )
                        }
                        composable("virifiedmail") {
                            VerifiedMailScreen(onProsseguirClick = { navController.navigate("resetpassword") })
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

                    }
                }
            }
        }
    }
}