package com.lordapps.wallpaper.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.datastore: DataStore<Preferences> by preferencesDataStore(name = "prefs")

class DataStoreManager(private val dataStore: DataStore<Preferences>) {

    val isFirstTime:Flow<Boolean>
        get() = dataStore.data.map {
            it[firstTime]?:true
        }

    private companion object {
        val firstTime= booleanPreferencesKey(name="firstTime_")
    }

    suspend fun setNotFirstTime(firstTimef:Boolean){
        dataStore.edit {
            it[firstTime]=firstTimef
        }
    }

}