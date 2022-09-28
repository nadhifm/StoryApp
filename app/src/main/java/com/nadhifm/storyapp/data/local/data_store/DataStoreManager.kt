package com.nadhifm.storyapp.data.local.data_store

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nadhifm.storyapp.utils.Constans.DATA_STORE
import com.nadhifm.storyapp.utils.Constans.NAME
import com.nadhifm.storyapp.utils.Constans.TOKEN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE)

    companion object {
        val KEY_TOKEN = stringPreferencesKey(TOKEN)
        val KEY_NAME = stringPreferencesKey(NAME)
    }

    suspend fun saveName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NAME] = name
        }
    }

    val name: Flow<String>
        get() = context.dataStore.data.map { preferences ->
            preferences[KEY_NAME] ?: ""
        }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TOKEN] = token
        }
    }

    val token: Flow<String>
        get() = context.dataStore.data.map { preferences ->
            preferences[KEY_TOKEN] ?: ""
        }
}