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

// Criação do DataStore no contexto com o nome "settings"
val Context.dataStore by preferencesDataStore("settings")

// Classe responsável por gerenciar as preferências do app
class PreferencesManager(private val context: Context) {

    companion object {
        // Chave booleana para saber se os termos foram aceitos
        val TERMS_ACCEPTED_KEY = booleanPreferencesKey("terms_accepted")

        // Chave para armazenar a senha mestra como String
        val SENHA_MESTRE_KEY = stringPreferencesKey("senha_mestre")
    }

    // Flow que observa se os termos foram aceitos (padrão: false)
    val termsAccepted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[TERMS_ACCEPTED_KEY] ?: false
        }

    // Função suspensa para salvar se os termos foram aceitos
    suspend fun setTermsAccepted(accepted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[TERMS_ACCEPTED_KEY] = accepted
        }
    }

    // Função suspensa para salvar a senha mestra
    suspend fun setSenhaMestre(senha: String) {
        context.dataStore.edit { preferences ->
            preferences[SENHA_MESTRE_KEY] = senha
        }
    }

    // Função suspensa que retorna a senha mestra armazenada
    suspend fun getSenhaMestre(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[SENHA_MESTRE_KEY] }
            .first() // Obtém o primeiro valor emitido
    }

    // Versão com callback (útil para chamadas fora de corrotinas suspensas)
    fun getSenhaMestre(callback: (String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val senha = getSenhaMestre()
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                callback(senha)
            }
        }
    }

}
