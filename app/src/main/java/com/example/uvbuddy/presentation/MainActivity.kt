/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter to find the
 * most up to date changes to the libraries and their usages.
 */

package com.example.uvbuddy.presentation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.uvbuddy.R
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
        createNotificationChannel(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
            }
        }

        // Observa LiveData e atualiza TextView
        viewModel.uvStatus.observe(this) { status ->
            binding.tvUvStatus.text = status
        }

        viewModel.backgroundColor.observe(this) { color ->
            binding.boxLayout.setBackgroundColor(color)
        }

        viewModel.tip.observe(this) { tip ->
            binding.tvTip.text = tip
        }

        viewModel.alertUv.observe(this){ status ->
            status?.let { sendUVNotification(this, it) }
        }

        // Chama requisição
        viewModel.restartScreen()
    }

    fun createNotificationChannel(context: Context) {
        val name = "UV Alert"
        val descriptionText = "Notificações de alerta UV"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("uv_alerts", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun sendUVNotification(context: Context, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Sem permissão, retorna
            return
        }

        val builder = NotificationCompat.Builder(context, "uv_alerts")
            .setSmallIcon(R.drawable.ic_uv_alert)
            .setContentTitle("Alerta de UV")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        NotificationManagerCompat.from(context).notify(1001, builder.build())
    }
}

