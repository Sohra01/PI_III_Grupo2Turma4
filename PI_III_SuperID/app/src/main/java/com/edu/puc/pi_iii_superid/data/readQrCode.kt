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


@SuppressLint("RememberReturnType")
@Composable
fun CameraPreview(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val scannedCodeState = remember { mutableStateOf<String?>(null) }

    AndroidView(
        factory = { ctx ->
            PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(
                            ContextCompat.getMainExecutor(context),
                            QrCodeAnalyzer { result ->
                                if (scannedCodeState.value == null) {
                                    scannedCodeState.value = result
                                    processLoginToken(context, result, navController)
                                    cameraProvider.unbindAll()
                                }
                            }
                        )
                    }

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                } catch (e: Exception) {
                    Log.e("CameraPreview", "Erro ao iniciar câmera", e)
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

fun processLoginToken(context: Context, loginToken: String, navController: NavController) {
    val user = FirebaseAuth.getInstance().currentUser

    if (user == null) {
        Toast.makeText(context, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
        return
    }

    val db = FirebaseFirestore.getInstance()
    val loginDocRef = db.collection("login").document(loginToken)

    loginDocRef.update(
        mapOf(
            "user" to user.uid,
            "loginAt" to FieldValue.serverTimestamp()
        )
    ).addOnSuccessListener {
        Toast.makeText(context, "Login confirmado com sucesso!", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).post {
            navController.navigate("category") {
                popUpTo("camera") { inclusive = true }
            }
        }
    }.addOnFailureListener { e ->
        Toast.makeText(context, "Erro ao confirmar login", Toast.LENGTH_SHORT).show()
        Log.e("QRCodeLogin", "Erro ao atualizar Firestore", e)
    }
}