package edu.washington.gllc.conversationstarter

import android.app.Application
import android.util.Log
import edu.washington.gllc.conversationstarter.classes.ConversationStarterRepo

class ConversationStarterApp : Application() {
    val repository = ConversationStarterRepo()

    override fun onCreate() {
        super.onCreate()

        Log.i(TAG, "ConversationStarter was loaded successfully.")
    }

    companion object {
        const val TAG = "ConversationStarterApp"
        private var instance: ConversationStarterApp? = null

        fun getSingletonInstance(): ConversationStarterApp {
            return instance!!
        }
    }

    init {
        instance = this
    }
}