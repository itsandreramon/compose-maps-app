package de.thb.core.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

object DataStoreManager {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

    fun retrieveDataStore(context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}

interface PrefsStore {
    fun getHasSeenOnboarding(): Flow<Boolean>
    suspend fun setHasSeenOnboarding(hasSeenOnboarding: Boolean)
}

class PrefsStoreImpl(applicationContext: Context) : PrefsStore {

    private val dataStore = DataStoreManager.retrieveDataStore(applicationContext)

    private object PreferencesKeys {
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
    }

    override fun getHasSeenOnboarding(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { it[PreferencesKeys.HAS_SEEN_ONBOARDING] ?: false }
    }

    override suspend fun setHasSeenOnboarding(hasSeenOnboarding: Boolean) {
        dataStore.edit {
            it[PreferencesKeys.HAS_SEEN_ONBOARDING] = hasSeenOnboarding
        }
    }
}
