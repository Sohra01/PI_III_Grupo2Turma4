import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

// Objeto responsável por realizar criptografia e descriptografia usando AES com o Android Keystore
object CryptoUtils {

    // Nome do provedor de chaves do Android
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"

    // Alias da chave no Keystore (identificador da chave)
    private const val KEY_ALIAS = "MinhaChaveAES"

    // Especificação da transformação de criptografia: AES no modo GCM sem preenchimento (NoPadding)
    private const val TRANSFORMATION = "AES/GCM/NoPadding"

    // Tamanho do TAG de autenticação do GCM (em bits), usado para integridade dos dados
    private const val GCM_TAG_LENGTH = 128 // bits (16 bytes)

    // Instância do KeyStore, onde as chaves são armazenadas de forma segura no dispositivo
    private val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
        load(null) // Carrega o keystore (sem necessidade de senha, pois é interno do Android)
    }

    // Bloco de inicialização: verifica se a chave já existe, senão cria uma nova
    init {
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            generateKey() // Gera a chave se não existir
        }
    }

    // Função responsável por gerar uma nova chave AES e armazená-la no Android Keystore
    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, // Algoritmo AES
            ANDROID_KEYSTORE // Armazenar no Keystore
        )
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS, // Alias da chave
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT // Permitir criptografar e descriptografar
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM) // Modo GCM (moderno e seguro)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE) // Sem padding
            .setKeySize(256) // Tamanho da chave (256 bits — forte)
            .build()

        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey() // Gera e armazena a chave
    }

    // Recupera a chave secreta armazenada no Keystore
    private fun getSecretKey(): SecretKey {
        return (keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry).secretKey
    }

    // Função para criptografar uma string
    fun encrypt(plainText: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey())

        val iv = cipher.iv // Vetor de Inicialização (IV) gerado automaticamente — 12 bytes para GCM
        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8)) // Texto criptografado em bytes

        // Combinar IV + dados criptografados em um único array
        val combined = ByteArray(iv.size + encryptedBytes.size)
        System.arraycopy(iv, 0, combined, 0, iv.size) // Copia o IV no início
        System.arraycopy(encryptedBytes, 0, combined, iv.size, encryptedBytes.size) // Depois os dados

        // Retorna o resultado como string Base64 (para armazenar ou transmitir)
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    // Função para descriptografar uma string previamente criptografada
    fun decrypt(encryptedData: String): String {
        // Decodifica a string Base64 para obter os bytes
        val combined = Base64.decode(encryptedData, Base64.NO_WRAP)

        // Extrai o IV dos primeiros 12 bytes
        val iv = combined.copyOfRange(0, 12)

        // Extrai os dados criptografados restantes
        val encryptedBytes = combined.copyOfRange(12, combined.size)

        // Configura o Cipher para descriptografia
        val cipher = Cipher.getInstance(TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH, iv) // Passa o IV e a tag de autenticação
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec)

        // Descriptografa os dados e converte para string
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes, Charsets.UTF_8)
    }
}
