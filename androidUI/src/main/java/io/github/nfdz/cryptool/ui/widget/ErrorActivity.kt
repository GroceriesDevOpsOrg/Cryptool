package io.github.nfdz.cryptool.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.nfdz.cryptool.ui.R

class ErrorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_error)

        val sharedPreferences = getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
        if (!sharedPreferences.getBoolean("errorVisible", true)){
            sharedPreferences.edit().putBoolean("errorVisible", true).apply()
        }else{
            sharedPreferences.edit().putBoolean("errorVisible", false).apply()
        }

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