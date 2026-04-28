package com.example.soyle.ui.screens.home

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private val Context.notesDataStore by preferencesDataStore("soyle_notes")
private val NOTES_KEY = stringPreferencesKey("notes_list")
private const val SEPARATOR = "||NOTE||"

@HiltViewModel
class NotesViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _notes = MutableStateFlow<List<String>>(emptyList())
    val notes: StateFlow<List<String>> = _notes.asStateFlow()

    init {
        viewModelScope.launch {
            context.notesDataStore.data
                .map { prefs ->
                    val raw = prefs[NOTES_KEY] ?: ""
                    if (raw.isBlank()) emptyList()
                    else raw.split(SEPARATOR).filter { it.isNotBlank() }.reversed()
                }
                .collect { _notes.value = it }
        }
    }

    fun addNote(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            context.notesDataStore.edit { prefs ->
                val existing = prefs[NOTES_KEY]?.split(SEPARATOR)?.filter { it.isNotBlank() } ?: emptyList()
                val updated  = existing + text.trim()
                prefs[NOTES_KEY] = updated.joinToString(SEPARATOR)
            }
        }
    }

    fun deleteNote(note: String) {
        viewModelScope.launch {
            context.notesDataStore.edit { prefs ->
                val existing = prefs[NOTES_KEY]?.split(SEPARATOR)?.filter { it.isNotBlank() } ?: emptyList()
                val updated  = existing.filter { it != note }
                prefs[NOTES_KEY] = updated.joinToString(SEPARATOR)
            }
        }
    }
}
