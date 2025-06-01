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


@SuppressLint("RememberReturnType") // Suprime o aviso relacionado ao uso de remember no tipo de retorno
@Composable
fun CameraPreview(navController: NavController) {
    val context = LocalContext.current // Obtém o contexto atual
    val lifecycleOwner = LocalLifecycleOwner.current // Obtém o dono do ciclo de vida atual (Activity ou Composable)

    // Estado que armazena o QR Code lido, inicialmente nulo
    val scannedCodeState = remember { mutableStateOf<String?>(null) }

    // Cria uma View nativa (PreviewView) para exibir a câmera dentro do Compose
    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER // Ajusta o preenchimento da câmera
            }
        },
        modifier = Modifier.fillMaxSize(), // Ocupa todo o tamanho disponível
        update = { previewView -> // Callback chamado para atualizar a view

            // Obtém o provedor da câmera (CameraX)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

            // Listener quando o cameraProvider estiver pronto
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                // Cria a visualização da câmera
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider) // Define onde a câmera será desenhada
                }

                // Seleciona a câmera traseira
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Configura o analisador de imagem para ler QR Codes
                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST) // Mantém só o frame mais recente
                    .build()
                    .also {
                        it.setAnalyzer(
                            ContextCompat.getMainExecutor(context), // Executor principal (Main Thread)
                            QrCodeAnalyzer { result -> // Usa o analisador criado para detectar QR Codes
                                // Verifica se já não leu algum QR antes
                                if (scannedCodeState.value == null) {
                                    scannedCodeState.value = result // Salva o QR code escaneado
                                    processLoginToken(context, result, navController) // Processa o login via QR
                                    cameraProvider.unbindAll() // Desativa a câmera após ler
                                }
                            }
                        )
                    }

                try {
                    // Desvincula qualquer uso anterior da câmera
                    cameraProvider.unbindAll()

                    // Vincula a câmera ao ciclo de vida da tela (para que pare automaticamente)
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    // Loga erro caso algo dê errado ao iniciar a câmera
                    Log.e("CameraPreview", "Erro ao iniciar câmera", e)
                }
            }, ContextCompat.getMainExecutor(context)) // Executor principal
        }
    )
}

// Função que processa o QR Code lido para confirmar o login
fun processLoginToken(context: Context, loginToken: String, navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser // Obtém o usuário atual logado

    // Se não houver usuário autenticado, exibe uma mensagem e cancela
    if (user == null) {
        Toast.makeText(context, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
        return
    }

    val db = FirebaseFirestore.getInstance() // Obtém instância do Firestore
    val loginDocRef = db.collection("login").document(loginToken) // Referência ao documento do login via QR

    // Atualiza o documento no Firestore com o UID do usuário e o timestamp do login
    loginDocRef.update(
        mapOf(
            "user" to user.uid,
            "loginAt" to FieldValue.serverTimestamp() // Data e hora no servidor
        )
    ).addOnSuccessListener {
        // Sucesso na confirmação do login
        Toast.makeText(context, "Login confirmado com sucesso!", Toast.LENGTH_SHORT).show()

        // Navega para a tela de categorias e remove a tela da câmera da pilha
        Handler(Looper.getMainLooper()).post {
            navController.navigate("category") {
                popUpTo("camera") { inclusive = true } // Remove a câmera da pilha
            }
        }
    }.addOnFailureListener { e ->
        // Erro ao atualizar o Firestore
        Toast.makeText(context, "Erro ao confirmar login", Toast.LENGTH_SHORT).show()
        Log.e("QRCodeLogin", "Erro ao atualizar Firestore", e)
    }
}
