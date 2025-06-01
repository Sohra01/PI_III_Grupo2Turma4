import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

// Cria uma instância de DataStore associada ao contexto, chamada "settings"
val Context.dataStore by preferencesDataStore("settings")

// Classe responsável por gerenciar as preferências salvas no DataStore
class PreferencesManager(private val context: Context) {

    // Declaração de chaves usadas para armazenar e recuperar valores do DataStore
    companion object {
        // Chave booleana que indica se os termos foram aceitos
        val TERMS_ACCEPTED_KEY = booleanPreferencesKey("terms_accepted")

        // Chave string para armazenar a senha mestre
        val SENHA_MESTRE_KEY = stringPreferencesKey("senha_mestre")
    }

    // Flow que observa continuamente se os termos foram aceitos
    val termsAccepted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            // Retorna o valor salvo, ou 'false' se não existir
            preferences[TERMS_ACCEPTED_KEY] ?: false
        }

    // Função suspensa para salvar se os termos foram aceitos ou não
    suspend fun setTermsAccepted(accepted: Boolean) {
        context.dataStore.edit { preferences ->
            // Salva o valor booleano no DataStore
            preferences[TERMS_ACCEPTED_KEY] = accepted
        }
    }

    // Função suspensa para salvar a senha mestre
    suspend fun setSenhaMestre(senha: String) {
        context.dataStore.edit { preferences ->
            // Salva a senha mestre no DataStore
            preferences[SENHA_MESTRE_KEY] = senha
        }
    }

    // Função suspensa que recupera a senha mestre de forma síncrona (com suspensão)
    suspend fun getSenhaMestre(): String? {
        return context.dataStore.data
            // Mapeia o DataStore e recupera o valor associado à chave SENHA_MESTRE_KEY
            .map { preferences -> preferences[SENHA_MESTRE_KEY] }
            // Pega o primeiro valor emitido pelo Flow e encerra
            .first()
    }

    // Função que recupera a senha mestre de forma assíncrona usando callback
    fun getSenhaMestre(callback: (String?) -> Unit) {
        // Cria uma corrotina no Dispatchers.IO (thread de operações de I/O)
        CoroutineScope(Dispatchers.IO).launch {
            // Chama a função suspensa que busca a senha
            val senha = getSenhaMestre()

            // Volta para a thread principal para executar o callback
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                callback(senha)
            }
        }
    }
}
