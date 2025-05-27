package com.example.uvbuddy.presentation.viewmodels

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.uvbuddy.presentation.repositories.UVRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.round

class UVViewModel : ViewModel() {
    private val repo = UVRepository()

    private val _uvStatus = MutableLiveData<SpannableString>("     Carregando UV...".toSpannable())
    val uvStatus: LiveData<SpannableString> = _uvStatus

    private val _backgroundColor = MutableLiveData<Int>()
    val backgroundColor: LiveData<Int> = _backgroundColor

    private val _tip = MutableLiveData<String>("     Carregando dica...")
    val tip: LiveData<String> = _tip

    private val _alertUv = MutableLiveData<String?>()
    val alertUv: LiveData<String?> = _alertUv

    private fun fetchUVLevel() {
        viewModelScope.launch {
            try {
                val uv = repo.fetchLatestUV()

                val roundedUv = uv?.let { round(it).toInt().toString() }

                val (status, color) = when {
                    uv == null -> "Erro ao obter UV" to Color.GRAY
                    uv >= 8.0f -> "$roundedUv\nMuito Alto" to Color.RED
                    uv >= 5.0f -> "$roundedUv\nAlto" to Color.rgb(255, 165, 0)
                    uv >= 2.0f -> "$roundedUv\nModerado" to Color.YELLOW
                    uv > 0.0f -> "$roundedUv\nBaixo" to Color.GREEN
                    else -> "Sem radiação UV" to Color.GRAY
                }

                val spannableStatus = status.toSpannable()

                roundedUv?.let {
                    val uvStart = status.indexOf(it)
                    val uvEnd = uvStart + it.length
                    spannableStatus.setSpan(
                        AbsoluteSizeSpan(40, true),
                        uvStart, uvEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }

                _uvStatus.value = spannableStatus
                _backgroundColor.value = color

                if (uv != null && uv >= 5.0f) {
                    _alertUv.value = status
                }

                _tip.value = when {
                    uv == null -> "     Sem dados"
                    uv >= 8.0f -> "     Proteção máxima"
                    uv >= 5.0f -> "     Use protetor solar"
                    uv >= 2.0f -> "     Use óculos e boné"
                    uv > 0.0f -> "     Não se preocupe"
                    else -> "     Sem dica"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uvStatus.value = "     Erro de conexão".toSpannable()
                _backgroundColor.value = Color.GRAY
                _tip.value = "     Verifique sua conexão"
            }
        }
    }

    fun restartScreen() {
        val time: Long = 1800000
        viewModelScope.launch {
            while (true) {
                fetchUVLevel()
                delay(time)
            }
        }
    }
}

fun String.toSpannable(): SpannableString {
    return SpannableString(this)
}
