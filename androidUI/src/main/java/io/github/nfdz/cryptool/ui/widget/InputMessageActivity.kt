package io.github.nfdz.cryptool.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import io.github.nfdz.cryptool.ui.R

class InputMessageActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_message)

        val editText = findViewById<EditText>(R.id.message)
        val button = findViewById<ImageView>(R.id.enterButton)
        val background = findViewById<FrameLayout>(R.id.background)

        editText.requestFocus()
        WindowCompat.getInsetsController(window, editText).show(WindowInsetsCompat.Type.ime())

        button.setOnClickListener {
            val messageText = editText.text.toString()
            if (messageText.isNotEmpty() && messageText.isNotBlank()) {
                val sharedPreferences = getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit()
                    .putString("broadcastMessageValue", messageText).apply()
                //
                val intent = Intent(this, CryptoolWidget::class.java)
                intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                val ids: IntArray = AppWidgetManager.getInstance(application)
                    .getAppWidgetIds(
                        android.content.ComponentName(
                            application,
                            CryptoolWidget::class.java
                        )
                    )
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                sendBroadcast(intent)
                finish()
            }
        }

        background.setOnClickListener {
            finish()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}
