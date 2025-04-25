/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.uvbuddy.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.uvbuddy.databinding.ActivityMainBinding
import com.example.uvbuddy.presentation.viewmodels.UVViewModel

class MainActivity : AppCompatActivity() {
     val binding: ActivityMainBinding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
     }
    private val viewModel: UVViewModel by lazy{
        ViewModelProvider(this)[UVViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Observa LiveData e atualiza TextView
        viewModel.uvStatus.observe(this) { status ->
            binding.tvUvStatus.text = status
        }

        // Chama requisição
        viewModel.fetchUVLevel()
    }
}

