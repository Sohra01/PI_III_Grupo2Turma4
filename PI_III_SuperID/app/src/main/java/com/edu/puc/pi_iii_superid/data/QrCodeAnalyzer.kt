package com.edu.puc.pi_iii_superid.data

import com.google.mlkit.vision.barcode.common.Barcode
import android.annotation.SuppressLint
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

// Classe responsável por analisar as imagens da câmera em tempo real e identificar QR Codes
class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit // Callback chamado quando um QR Code é detectado (retorna o valor lido)
) : ImageAnalysis.Analyzer { // Implementa a interface Analyzer da CameraX para processar imagens

    @SuppressLint("UnsafeOptInUsageError") // Suprime o alerta de API experimental usada (InputImage.fromMediaImage)

    // Função chamada automaticamente a cada frame capturado pela câmera
    override fun analyze(imageProxy: ImageProxy) {
        // Obtém a imagem do frame atual
        val mediaImage: Image = imageProxy.image ?: run {
            imageProxy.close() // Fecha o frame caso não consiga obter a imagem
            return
        }

        // Converte a imagem para o formato que o ML Kit entende, considerando a rotação do dispositivo
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

        // Cria uma instância do scanner de códigos de barra (inclui QR Code) do ML Kit
        val scanner = BarcodeScanning.getClient()

        // Processa a imagem procurando códigos de barra ou QR Codes
        scanner.process(image)
            .addOnSuccessListener { barcodes -> // Quando a análise é bem-sucedida
                for (barcode in barcodes) { // Percorre todos os códigos detectados
                    // Verifica se o código é do tipo texto (pode ser QR Code ou outro formato textual)
                    if (barcode.valueType == Barcode.TYPE_TEXT && barcode.rawValue != null) {
                        // Se encontrou um QR Code com texto, chama o callback passando o valor lido
                        onQrCodeScanned(barcode.rawValue!!)
                        break // Encerra após encontrar o primeiro QR Code válido
                    }
                }
            }
            .addOnFailureListener { e -> // Se ocorrer algum erro durante a leitura
                Log.e("QrCodeAnalyzer", "Erro ao escanear QR code", e)
            }
            .addOnCompleteListener {
                // Sempre fecha o frame após finalizar (sucesso ou erro), liberando o recurso da câmera
                imageProxy.close()
            }
    }
}
