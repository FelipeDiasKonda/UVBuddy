package com.example.uvbuddy.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uvbuddy.presentation.repositories.UVRepository

import kotlinx.coroutines.launch

class UVViewModel : ViewModel() {
    private val repo = UVRepository()

    private val _uvStatus = MutableLiveData<String>("Carregando UV...")
    val uvStatus: LiveData<String> = _uvStatus

    fun fetchUVLevel() {
        viewModelScope.launch {
            val uv = repo.fetchLatestUV()
            _uvStatus.value = when {
                uv == null        -> "Erro ao obter UV"
                uv >= 8.0f        -> "UV Muito Alto"
                uv >= 5.0f        -> "UV Alto"
                uv >= 2.0f        -> "UV Baixo"
                else              -> "UV Moderado"
            }
        }
    }
}