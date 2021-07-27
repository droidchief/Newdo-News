package com.example.newdo.helperfile

import android.content.Context
import android.widget.Toast
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.edit
import androidx.datastore.preferences.preferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ThemeManager(context: Context) {
    private val dataStore = context.createDataStore(name = "user_prefs")

    companion object {
        val THEME_KEY = preferencesKey<Int>("THEME")
    }

    //save data
    suspend fun saveCurrentTheme(theme: Int) {
          dataStore.edit {
              it[THEME_KEY] = theme
          }
    }

    //retrieve data
    val themeFlow: Flow<Int> = dataStore.data.map {
         val currentTheme = it[THEME_KEY] ?: 0

//        when(currentTheme) {
//            0 -> Toast.makeText(context, "Theme: Light", Toast.LENGTH_SHORT).show()
//            1 -> Toast.makeText(context, "Theme: Dark", Toast.LENGTH_SHORT).show()
//        }

        currentTheme
    }

}