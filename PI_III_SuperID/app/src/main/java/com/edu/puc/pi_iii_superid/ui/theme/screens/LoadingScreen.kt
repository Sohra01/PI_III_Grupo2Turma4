package com.edu.puc.pi_iii_superid.ui.theme.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

// Função composable que representa uma tela de carregamento (loading)
@Composable
fun LoadingScreen() {

    // Um container que ocupa toda a tela e centraliza seu conteúdo
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()// Indicador de carregamento circular
    }
}
