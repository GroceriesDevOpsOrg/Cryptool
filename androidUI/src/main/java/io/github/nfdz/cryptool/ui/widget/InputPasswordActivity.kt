package io.github.nfdz.cryptool.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import io.github.nfdz.cryptool.ui.R

class InputPasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input_password)

        val editText = findViewById<EditText>(R.id.password)
        val button = findViewById<ImageView>(R.id.enterButton)
        val background = findViewById<FrameLayout>(R.id.background)

        editText.requestFocus()
        WindowCompat.getInsetsController(window, editText).show(WindowInsetsCompat.Type.ime())

        button.setOnClickListener {
            val password = editText.text.toString()
            if (password.isNotEmpty() || password.isNotBlank()){
                val sharedPreferences = getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
                sharedPreferences.edit().putString("broadcastPasswordValue", editText.text.toString()).apply()

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
                intent.putExtra("password", password)
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
