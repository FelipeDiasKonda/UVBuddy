package com.example.uvbuddy.presentation.viewmodels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uvbuddy.R
import com.example.uvbuddy.presentation.repositories.UVRepository
import kotlinx.coroutines.delay

import kotlinx.coroutines.launch


class UVViewModel : ViewModel() {
    private val repo = UVRepository()

    private val _uvStatus = MutableLiveData<String>("Carregando UV...")
    val uvStatus: LiveData<String> = _uvStatus

    private val _backgroundColor = MutableLiveData<Int>()
    val backgroundColor: LiveData<Int> = _backgroundColor

    private val _tip = MutableLiveData<String>("Carregando dica...")
    val tip: LiveData<String> = _tip

    private val _alertUv = MutableLiveData<String?>()
    val alertUv: LiveData<String?> = _alertUv

    private fun fetchUVLevel() {
        viewModelScope.launch {
            val uv = repo.fetchLatestUV()
            val (status, color) = when {
                uv == null -> "Erro ao obter UV" to android.graphics.Color.GRAY
                uv >= 8.0f -> "Índice UV $uv\n   Muito Alto" to android.graphics.Color.RED
                uv >= 5.0f -> "Índice UV $uv\n        Alto" to android.graphics.Color.rgb(255, 165, 0) // Laranja
                uv >= 2.0f -> "Índice UV $uv\n   Moderado" to android.graphics.Color.YELLOW
                uv >  0.0f -> "Índice UV $uv\n       Baixo" to android.graphics.Color.GREEN
                else -> "Sem radiação UV" to android.graphics.Color.GRAY
            }
            _uvStatus.value = status
            _backgroundColor.value = color

            if (uv != null && uv >= 5.0f) {
                _alertUv.value = status
            }

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

    fun restartScreen() {
        val time : Long = 1800000
        viewModelScope.launch {
            while (true) {
                fetchUVLevel()
                delay(time)
            }
        }
    }

}