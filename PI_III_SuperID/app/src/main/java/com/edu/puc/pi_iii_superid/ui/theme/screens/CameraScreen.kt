package com.edu.puc.pi_iii_superid.ui.theme.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.edu.puc.pi_iii_superid.data.CameraPreview
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(navController: NavController) {
    // Estado da permissão da câmera
    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)
   
    // Efeito que será executado uma vez quando o Composable for lançado
    LaunchedEffect(Unit) {
        // Se a permissão não estiver concedida, solicita a permissão
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
    // Se a permissão foi concedida, mostra a visualização da câmera
    if (cameraPermissionState.status.isGranted) {
        CameraPreview(navController)
    } else {
        // Caso a permissão não tenha sido concedida, mostra uma mensagem com um botão para solicitar a permissão novamente
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Permissão para usar a câmera é necessária.")
            Button(onClick = { cameraPermissionState.launchPermissionRequest() }) {
                Text("Conceder permissão")
            }
        }
    }
}
