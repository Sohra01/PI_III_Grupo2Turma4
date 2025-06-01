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

val Context.dataStore by preferencesDataStore("settings")

class PreferencesManager(private val context: Context) {

    companion object {
        val TERMS_ACCEPTED_KEY = booleanPreferencesKey("terms_accepted")
        val SENHA_MESTRE_KEY = stringPreferencesKey("senha_mestre")
    }

    val termsAccepted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[TERMS_ACCEPTED_KEY] ?: false
        }

    suspend fun setTermsAccepted(accepted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[TERMS_ACCEPTED_KEY] = accepted
        }
    }

    suspend fun setSenhaMestre(senha: String) {
        context.dataStore.edit { preferences ->
            preferences[SENHA_MESTRE_KEY] = senha
        }
    }

    suspend fun getSenhaMestre(): String? {
        return context.dataStore.data
            .map { preferences -> preferences[SENHA_MESTRE_KEY] }
            .first()
    }

    fun getSenhaMestre(callback: (String?) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val senha = getSenhaMestre()
            kotlinx.coroutines.withContext(Dispatchers.Main) {
                callback(senha)
            }
        }
    }

}

