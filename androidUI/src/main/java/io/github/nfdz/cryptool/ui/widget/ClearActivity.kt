package io.github.nfdz.cryptool.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.nfdz.cryptool.ui.R

class ClearActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clear)

        val sharedPreferences = getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("broadcastMessageValue", "Message").apply()
        sharedPreferences.edit().putString("broadcastPasswordValue", "Password").apply()
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