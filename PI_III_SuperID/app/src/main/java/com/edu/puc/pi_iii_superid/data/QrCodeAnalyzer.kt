package com.edu.puc.pi_iii_superid.data

import com.google.mlkit.vision.barcode.common.Barcode
import android.annotation.SuppressLint
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage: Image = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val scanner = BarcodeScanning.getClient()

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    if (barcode.valueType == Barcode.TYPE_TEXT && barcode.rawValue != null) {
                        onQrCodeScanned(barcode.rawValue!!)
                        break
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("QrCodeAnalyzer", "Erro ao escanear QR code", e)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }
}
