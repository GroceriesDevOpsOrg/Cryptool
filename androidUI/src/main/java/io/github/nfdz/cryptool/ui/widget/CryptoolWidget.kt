package io.github.nfdz.cryptool.ui.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.widget.WidgetConst.CLEAR
import io.github.nfdz.cryptool.ui.widget.WidgetConst.DECRYPT
import io.github.nfdz.cryptool.ui.widget.WidgetConst.ENCRYPT
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.sqrt

/**
 * Implementation of App Widget functionality.
 */

@RequiresApi(Build.VERSION_CODES.N)
class CryptoolWidget : AppWidgetProvider() {

    companion object {
        const val TAG = "CryptoolWidget"
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        remoteViews: RemoteViews
    ) {

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        val views = RemoteViews(context.packageName, R.layout.cryptool_widget)

        val sharedPreferences =
            context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
        val updatedValueP =
            sharedPreferences?.getString("broadcastPasswordValue", "Password") ?: "Password"
        val updatedValueM =
            sharedPreferences?.getString("broadcastMessageValue", "Message") ?: "Message"

        //Nfa7CUjRV6CapE8i5sjAf39f53Y.OckwJeN21jXZHVu0yveWqQ.128.phsbfCV2pJbHZ5gQIPb_oydmI8H226cuez4WgvAHlMghPeBkc6aU7eJXP5h4xdtuZGN0UjsQ

        views.setTextViewText(R.id.passwordInput, updatedValueP)
        views.setTextViewText(R.id.messageInput, updatedValueM)
//        Log.d(TAG, "onUpdateTextValue: $message")

        if (updatedValueM.isCipherText()) {
            views.setViewVisibility(R.id.decryptButton, View.VISIBLE)
            views.setViewVisibility(R.id.actionButton, View.GONE)
        } else {
            views.setViewVisibility(R.id.decryptButton, View.GONE)
            views.setViewVisibility(R.id.actionButton, View.VISIBLE)
        }

        appWidgetIds.forEach {
            updateAppWidget(context, appWidgetManager, it, views)
        }

        appWidgetIds.forEach { appWidgetId ->

            views.setOnClickPendingIntent(
                R.id.passwordLayout,
                getPendingSelfIntentPassword(context, "com.widget.receiver.password")
            )
            views.setOnClickPendingIntent(
                R.id.messageLayout,
                getPendingSelfIntentMessage(context, "com.widget.receiver.message")
            )
            views.setOnClickPendingIntent(
                R.id.clearButton,
                getPendingSelfIntent(context, action = CLEAR)
            )
            views.setOnClickPendingIntent(
                R.id.actionButton,
                getPendingSelfIntent(context, action = ENCRYPT)
            )
            views.setOnClickPendingIntent(
                R.id.decryptButton,
                getPendingSelfIntent(context, action = DECRYPT)
            )
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun onReceive(context: Context?, intent: Intent?) {

        val action = intent?.action
        val remoteViews = RemoteViews(context!!.packageName, R.layout.cryptool_widget)
        val componentName = ComponentName(context, CryptoolWidget::class.java)

        val sharedPreferences =
            context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        when (action) {
            AppWidgetManager.ACTION_APPWIDGET_UPDATE -> {
                val ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                context.let {
                    ids?.let { it1 ->
                        onUpdate(
                            it, AppWidgetManager.getInstance(context), it1
                        )
                    }
                }
            }

            CLEAR -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(
                        context,
                        CryptoolWidget::class.java
                    )
                )

                remoteViews.setTextViewText(R.id.passwordInput, "Password")
                remoteViews.setTextViewText(R.id.messageInput, "Message")

                remoteViews.setViewVisibility(R.id.decryptButton, View.GONE)
                remoteViews.setViewVisibility(R.id.actionButton, View.VISIBLE)

                remoteViews.setViewVisibility(R.id.errorBackground, View.GONE)
                remoteViews.setViewVisibility(R.id.errorText, View.GONE)

                editor.remove("broadcastPasswordValue").apply()
                editor.remove("broadcastMessageValue").apply()
                for (appWidgetId in appWidgetIds) {
                    appWidgetManager.updateAppWidget(componentName, remoteViews)
                }
            }

            ENCRYPT -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(
                        context,
                        CryptoolWidget::class.java
                    )
                )
                val sharedPreferences2 =
                    context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
                val updatedValueP =
                    sharedPreferences2.getString("broadcastPasswordValue", "-Password-") ?: ""
                val updatedValueM =
                    sharedPreferences2.getString("broadcastMessageValue", "-Message-") ?: ""

                CoroutineScope(Dispatchers.IO).launch {
                    val encryptMessage = createEncryptedText(updatedValueP, updatedValueM)
                    Log.d(TAG, "onReceiveEncrypt: $encryptMessage")

                    sharedPreferences2.edit().putString("broadcastMessageValue", encryptMessage)
                        .apply()

                    withContext(Dispatchers.Main) {
                        remoteViews.setTextViewText(R.id.passwordInput, updatedValueP)
                        remoteViews.setTextViewText(R.id.messageInput, encryptMessage.plus(" "))

                        remoteViews.setViewVisibility(R.id.actionButton, View.GONE)
                        remoteViews.setViewVisibility(R.id.decryptButton, View.VISIBLE)

                        for (appWidgetId in appWidgetIds) {
                            appWidgetManager.updateAppWidget(componentName, remoteViews)
                        }
                    }
                }

            }

            DECRYPT -> {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val appWidgetIds = appWidgetManager.getAppWidgetIds(
                    ComponentName(
                        context,
                        CryptoolWidget::class.java
                    )
                )
                val sharedPreferences2 =
                    context.getSharedPreferences("WidgetPrefs", Context.MODE_PRIVATE)
                val updatedValueP =
                    sharedPreferences2.getString("broadcastPasswordValue", "-Password-") ?: ""
                val updatedValueM =
                    sharedPreferences2.getString("broadcastMessageValue", "-Message-") ?: ""

                CoroutineScope(Dispatchers.IO).launch {
                    var errorState = false
                    // try block catches inccorect password
                    try {
                        val decryptMessage = createDecryptedText(updatedValueP, updatedValueM)
                        Log.d(TAG, "onReceiveDecrypt: $decryptMessage")
                        sharedPreferences2.edit().putString("broadcastMessageValue", decryptMessage)
                            .apply()

                        withContext(Dispatchers.Main) {
                            remoteViews.setTextViewText(R.id.passwordInput, updatedValueP)
                            remoteViews.setTextViewText(R.id.messageInput, decryptMessage)

                            remoteViews.setViewVisibility(R.id.actionButton, View.VISIBLE)
                            remoteViews.setViewVisibility(R.id.decryptButton, View.GONE)

                            for (appWidgetId in appWidgetIds) {
                                appWidgetManager.updateAppWidget(componentName, remoteViews)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.d(TAG, "onReceiveDecrypt: ${e.message}")
                        errorState = true

                    }
                    if (errorState) {
                        remoteViews.setViewVisibility(R.id.errorBackground, View.VISIBLE)
                        remoteViews.setViewVisibility(R.id.errorText, View.VISIBLE)
                    } else {
                        remoteViews.setViewVisibility(R.id.errorBackground, View.GONE)
                        remoteViews.setViewVisibility(R.id.errorText, View.GONE)

                    }
                    for (appWidgetId in appWidgetIds) {
                        appWidgetManager.updateAppWidget(componentName, remoteViews)
                    }
                }
            }

        }

        super.onReceive(context, intent)
    }

    private suspend fun createEncryptedText(password: String, message: String): String {
        val cryptography = AlgorithmVersion.V2.createCryptography()
        return cryptography.encrypt(password = password, text = message)
            ?: throw IllegalStateException("Cannot receive message")
    }

    private suspend fun createDecryptedText(password: String, message: String): String {
        val cryptography = AlgorithmVersion.V2.createCryptography()
        return cryptography.decrypt(password = password, encryptedText = message)
            ?: throw IllegalStateException("Cannot receive message")

    }

    private fun String.isCipherText(): Boolean {

        // Additional heuristics can be added based on specific encryption formats or algorithms.

        // For example, if you know the ciphertext is Base64 encoded, you can check for its format:

        // You can add more specific checks based on the encryption algorithms or formats you expect.

        return this.matches("[A-Za-z0-9+/_.]+[=]{0,2}\$".toRegex())
    }


    private fun isNotCipherText(text: String): Boolean {
        val charFrequency = mutableMapOf<Char, Int>()

        // Count the frequency of each character
        for (char in text) {
            charFrequency[char] = charFrequency.getOrDefault(char, 0) + 1
        }

        // Calculate the total number of characters (excluding spaces)
        val totalChars = text.filter { it != ' ' }.length

        // Calculate the frequency percentage for each character
        val frequencyPercentage = charFrequency.mapValues { (_, count) ->
            (count.toDouble() / totalChars) * 100
        }

        // Calculate the mean and standard deviation of frequency percentages
        val mean = frequencyPercentage.values.average()
        val standardDeviation =
            sqrt(frequencyPercentage.values.map { (it - mean) * (it - mean) }.average())

        // If the standard deviation is low, it's likely not ciphertext
        // Adjust this threshold based on your specific needs
        val threshold = 5.0

        return standardDeviation < threshold
    }

    private fun getPendingSelfIntent(
        context: Context?,
        action: String?
    ): PendingIntent? {
        val intent = Intent(context, CryptoolWidget::class.java)
        intent.setAction(action)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getPendingSelfIntentMessage(
        context: Context?,
        action: String?
    ): PendingIntent? {
        val intent = Intent(context, MessageClickReceiver::class.java)
        intent.setAction(action)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getPendingSelfIntentPassword(
        context: Context?,
        action: String?
    ): PendingIntent? {
        val intent = Intent(context, PasswordClickReceiver::class.java)
        intent.setAction(action)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}
