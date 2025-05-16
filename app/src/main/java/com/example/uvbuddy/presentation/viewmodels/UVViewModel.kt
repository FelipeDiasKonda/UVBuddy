package com.example.uvbuddy.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uvbuddy.presentation.repositories.UVRepository
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.launch

class UVViewModel : ViewModel() {
    private val repo = UVRepository()

    private val _uvStatus = MutableLiveData<String>("Carregando UV...")
    val uvStatus: LiveData<String> = _uvStatus

    private val _backgroundColor = MutableLiveData<Int>()
    val backgroundColor: LiveData<Int> = _backgroundColor

    private val _tip = MutableLiveData<String>("Carregando dica...")
    val tip: LiveData<String> = _tip

    fun fetchUVLevel() {
        viewModelScope.launch {
            val uv = repo.fetchLatestUV()
            val (status, color) = when {
                uv == null -> "Erro ao obter UV" to android.graphics.Color.GRAY
                uv >= 8.0f -> "UV Muito Alto" to android.graphics.Color.RED
                uv >= 5.0f -> "UV Alto" to android.graphics.Color.rgb(255, 165, 0) // Laranja
                uv >= 2.0f -> "UV Moderado" to android.graphics.Color.YELLOW
                uv >  0.0f -> "UV Baixo" to android.graphics.Color.GREEN
                else -> "Sem radiação UV" to android.graphics.Color.GRAY
            }
            _uvStatus.value = status
            _backgroundColor.value = color

             _tip.value = when {
                uv == null -> null
                uv >= 8.0f -> "Proteção máxima"
                uv >= 5.0f -> "Use protetor solar"
                uv >= 2.0f -> "Use óculos e boné"
                uv >  0.0f -> "Não se preocupe"
                else -> null
            }
        }
    }
}