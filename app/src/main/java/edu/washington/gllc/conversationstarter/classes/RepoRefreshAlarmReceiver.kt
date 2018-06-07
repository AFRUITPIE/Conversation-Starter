package edu.washington.gllc.conversationstarter.classes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import edu.washington.gllc.conversationstarter.ConversationStarterApp

class RepoRefreshAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val appInstance = ConversationStarterApp.getSingletonInstance()
        val url: String = intent!!.extras["url"].toString()

        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(context)
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    // Resolves odd error where response would sometimes be null from Firebase
                    if (response != "null") {
                        if (prefs!!.getBoolean("evil_mode", false)) {
                            appInstance.repository.setEvilRepoStarters(response)
                            Log.i("AlarmReceiver", "Refreshed the evil repo")
                            Toast.makeText(context, "Updated evil repository", Toast.LENGTH_SHORT).show()
                        } else {
                            appInstance.repository.setRepoStarters(response)
                            Log.i("AlarmReceiver", "Refreshed the normal repo")
                            Toast.makeText(context, "Udpated normal repository", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.i("AlarmReceiver", "JSON returned null")
                        Toast.makeText(context, "Online JSON was null, Firebase does this sometimes", Toast.LENGTH_SHORT).show()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(context, "The URL you provided doesn't work :(", Toast.LENGTH_SHORT).show()
                    Log.i("AlarmReceiver", "Unable to load online repo")
                })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }
}