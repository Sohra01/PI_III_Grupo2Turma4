package com.example.superid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.edu.puc.pi_iii_superid.ui.theme.PI_III_SuperIDTheme
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
                NavHost(navController = navController, startDestination = "welcome") {
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
                                // TODO: Navegar para tela de "esqueci a senha"
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
                        CategoryScreen()
                    }

                }
            }
        }
    }
}

