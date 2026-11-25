package com.example.navigationlab.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(
    private val context: Context
) {
    private object Keys {
        val LAST_SEARCH_QUERY = stringPreferencesKey("last_search_query")
        val SORT_ASCENDING = booleanPreferencesKey("sort_ascending")
    }

    val lastSearchQuery: Flow<String> = context.userPreferencesDataStore.data.map { preferences ->
        preferences[Keys.LAST_SEARCH_QUERY] ?: ""
    }

    val sortAscending: Flow<Boolean> = context.userPreferencesDataStore.data.map { preferences ->
        preferences[Keys.SORT_ASCENDING] ?: true
    }

    suspend fun saveLastSearchQuery(query: String) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.LAST_SEARCH_QUERY] = query
        }
    }

    suspend fun saveSortAscending(ascending: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[Keys.SORT_ASCENDING] = ascending
        }
    }
}