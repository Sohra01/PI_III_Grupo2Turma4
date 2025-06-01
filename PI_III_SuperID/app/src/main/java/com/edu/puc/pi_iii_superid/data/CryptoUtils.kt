// Importações necessárias para uso de criptografia, armazenamento seguro e codificação base64
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

// Objeto utilitário para criptografia e descriptografia de textos usando AES no Android Keystore
object CryptoUtils {

    // Constantes relacionadas à configuração da criptografia
    private const val ANDROID_KEYSTORE = "AndroidKeyStore" // Nome do provedor de chaves seguro do Android
    private const val KEY_ALIAS = "MinhaChaveAES" // Alias único para identificar a chave no Keystore
    private const val TRANSFORMATION = "AES/GCM/NoPadding" // Algoritmo, modo de operação e padding
    private const val GCM_TAG_LENGTH = 128 // Tamanho da tag de autenticação GCM em bits

    // Instância do KeyStore, carregada com null (usando armazenamento padrão do Android)
    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null)
    }

    // Bloco init é executado na primeira vez que o objeto é usado
    init {
        // Se a chave ainda não existe, ela é gerada
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            generateKey()
        }
    }

    // Função privada para gerar a chave AES e armazená-la no Keystore
    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)

        // Define os parâmetros da chave: propósito, modo de operação e padding
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256) // Tamanho da chave em bits (256 para AES forte)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey() // Gera e salva a chave no AndroidKeyStore
    }

    // Função privada para obter a chave secreta a partir do Keystore
    private fun getSecretKey(): SecretKey {
        return (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
    }

    // Função pública para criptografar um texto simples (plainText)
    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)

        // Inicializa o cifrador em modo de criptografia com a chave obtida
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

        val iv = cipher.iv // IV (vetor de inicialização) gerado automaticamente (12 bytes padrão GCM)
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8)) // Criptografa o texto

        // Combina o IV com os bytes criptografados para armazenamento conjunto
        val combined = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, combined, 0, iv.size)
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size)

        // Retorna o resultado como string codificada em Base64 (sem quebras de linha)
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    // Função pública para descriptografar dados criptografados
    fun decrypt(encryptedData: String): String {
        val combined = Base64.decode(encryptedData, Base64.NO_WRAP) // Decodifica de Base64 para bytes

        // Extrai o IV dos primeiros 12 bytes
        val iv = combined.copyOfRange(0, 12)

        // O restante são os dados criptografados propriamente ditos
        val encryptedBytes = combined.copyOfRange(12, combined.size)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv) // Define o parâmetro do GCM com o IV

        // Inicializa o cifrador em modo de descriptografia com a chave e IV
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

        // Realiza a descriptografia e retorna o resultado como string
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
