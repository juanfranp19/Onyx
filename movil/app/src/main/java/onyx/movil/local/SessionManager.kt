package onyx.movil.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore by preferencesDataStore("session")

class SessionManager(private val context: Context) {

    companion object {
        // clave del ID del usuario
        private val USER_ID = longPreferencesKey("user_id")
    }

    // guarda el ID del usuario
    suspend fun saveSession(userId: Long) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = userId
        }
    }

    // lee el ID del usuario
    suspend fun getUserId(): Long? {
        val prefs = context.dataStore.data.first()
        return prefs[USER_ID]
    }

    // borra la sesión
    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
