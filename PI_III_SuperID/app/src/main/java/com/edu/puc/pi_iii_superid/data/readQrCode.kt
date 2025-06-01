package com.edu.puc.pi_iii_superid.data

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.camera.core.Preview
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.mutableStateOf
import android.os.Handler
import android.os.Looper
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

// Composable que exibe a visualização da câmera e analisa o QR Code
@SuppressLint("RememberReturnType")
@Composable
fun CameraPreview(navController: NavController) {
    val context = LocalContext.current // contexto atual do app
    val lifecycleOwner = LocalLifecycleOwner.current // ciclo de vida da tela

    // Estado para armazenar o valor do QR Code escaneado (evita múltiplas leituras)
    val scannedCodeState = remember { mutableStateOf<String?>(null) }

    // Exibe uma visualização da câmera usando um AndroidView com PreviewView
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER // tipo de escala da imagem
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Criação do Preview da câmera
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA // usa a câmera traseira

                // Configura a análise de imagem com ML Kit
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(
                            ContextCompat.getMainExecutor(context),
                            QrCodeAnalyzer { result ->
                                // Se ainda não escaneou, processa o QR Code
                                if (scannedCodeState.value == null) {
                                    scannedCodeState.value = result
                                    processLoginToken(context, result, navController)
                                    cameraProvider.unbindAll() // Para a análise após leitura
                                }
                            }
                        )
                    }

                try {
                    cameraProvider.unbindAll() // Desvincula qualquer uso anterior da câmera
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    ) // Vincula o preview e a análise ao ciclo de vida da tela
                } catch (e: Exception) {
                    Log.e("CameraPreview", "Erro ao iniciar câmera", e)
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

// Função que processa o login a partir do QR Code escaneado
fun processLoginToken(context: Context, loginToken: String, navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user == null) {
        Toast.makeText(context, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
        return
    }

    val db = FirebaseFirestore.getInstance()
    val loginDocRef = db.collection("login").document(loginToken)

    // Atualiza o documento de login com o UID do usuário e timestamp
    loginDocRef.update(
        mapOf(
            "user" to user.uid,
            "loginAt" to FieldValue.serverTimestamp()
        )
    ).addOnSuccessListener {
        Toast.makeText(context, "Login confirmado com sucesso!", Toast.LENGTH_SHORT).show()

        // Navega para a tela de categorias após o sucesso
        Handler(Looper.getMainLooper()).post {
            navController.navigate("category") {
                popUpTo("camera") { inclusive = true } // Remove a tela da câmera da pilha
            }
        }
    }.addOnFailureListener { e ->
        Toast.makeText(context, "Erro ao confirmar login", Toast.LENGTH_SHORT).show()
        Log.e("QRCodeLogin", "Erro ao atualizar Firestore", e)
    }
}
