package com.edu.puc.pi_iii_superid.data

import com.google.mlkit.vision.barcode.common.Barcode
import android.annotation.SuppressLint
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

// Classe responsável por analisar os frames da câmera e detectar QR Codes
class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit // Callback chamado quando um QR code válido é detectado
) : ImageAnalysis.Analyzer {

    // Função chamada automaticamente para cada frame capturado pela câmera
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        // Obtém a imagem do frame atual. Se for nula, fecha o frame e sai.
        val mediaImage: Image = imageProxy.image ?: run {
            imageProxy.close()
            return
        }

        // Converte a imagem da câmera para o formato aceito pela ML Kit
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        // Cria uma instância do leitor de código de barras (inclui QR codes)
        val scanner = BarcodeScanning.getClient()

        // Processa a imagem em busca de QR codes
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                // Para cada código detectado, verifica se é do tipo texto e tem valor
                for (barcode in barcodes) {
                    if (barcode.valueType == Barcode.TYPE_TEXT && barcode.rawValue != null) {
                        // Chama o callback com o valor do QR code
                        onQrCodeScanned(barcode.rawValue!!)
                        break // Para após encontrar o primeiro válido
                    }
                }
            }
            .addOnFailureListener { e ->
                // Loga erro caso falhe ao escanear o QR code
                Log.e("QrCodeAnalyzer", "Erro ao escanear QR code", e)
            }
            .addOnCompleteListener {
                // Fecha o frame após o processamento (sucesso ou erro)
                imageProxy.close()
            }
    }
}
