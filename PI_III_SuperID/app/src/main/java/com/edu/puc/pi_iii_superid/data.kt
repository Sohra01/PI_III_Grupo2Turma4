import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("settings") // "settings" é o nome do arquivo gerado

class PreferencesManager(private val context: Context) {

    companion object {
        val TERMS_ACCEPTED_KEY = booleanPreferencesKey("terms_accepted")
    }

    // 2. Lê se os termos foram aceitos
    val termsAccepted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[TERMS_ACCEPTED_KEY] ?: false
        }

    // 3. Salva o aceite dos termos
    suspend fun setTermsAccepted(accepted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[TERMS_ACCEPTED_KEY] = accepted
        }
    }
}
