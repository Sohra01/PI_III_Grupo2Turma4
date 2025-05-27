package com.edu.puc.pi_iii_superid.data

import android.content.Context
import android.util.Log
import androidx.camera.core.Preview
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

fun startQrCodeScanner(context: Context, lifecycleOwner: LifecycleOwner) {
    val previewView = PreviewView(context)

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
                        processLoginToken(context, result)
                        cameraProvider.unbindAll()  
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
            Log.e("CameraError", "Erro ao iniciar câmera: ${e.message}")
        }
    }, ContextCompat.getMainExecutor(context))
}

fun processLoginToken(context: Context, loginToken: String) {
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
    }.addOnFailureListener { e ->
        Toast.makeText(context, "Erro ao confirmar login", Toast.LENGTH_SHORT).show()
        Log.e("QRCodeLogin", "Erro ao atualizar Firestore", e)
    }
}