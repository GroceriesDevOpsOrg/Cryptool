package io.github.nfdz.cryptool.ui.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PasswordClickReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == "com.widget.receiver.password") {
            val dialogIntent = Intent(context, InputPasswordActivity::class.java)
            dialogIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(dialogIntent)
        }
    }
}

class MessageClickReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == "com.widget.receiver.message") {
            val dialogIntent = Intent(context, InputMessageActivity::class.java)
            dialogIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(dialogIntent)

        }
    }
}

class ErrorClickReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == "com.widget.receiver.error") {
            val dialogIntent = Intent(context, ErrorActivity::class.java)
            dialogIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(dialogIntent)
        }
    }
}

class ClearClickReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == "com.widget.receiver.clear") {
            val dialogIntent = Intent(context, ClearActivity::class.java)
            dialogIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(dialogIntent)
        }
    }
}