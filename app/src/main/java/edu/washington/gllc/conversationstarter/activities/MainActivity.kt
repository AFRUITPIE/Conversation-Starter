package edu.washington.gllc.conversationstarter.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import edu.washington.gllc.conversationstarter.R

class MainActivity : AppCompatActivity() {
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        findViewById<FloatingActionButton>(R.id.fab_mainFragment_editConversationStarters).setOnClickListener { startActivity(Intent(this, TabbedConvoActivity::class.java)) }
        // Sets up the first menu item's (starting a conversation's) button
        val initConvoFab = findViewById<FloatingActionButton>(R.id.fab_mainFragment_startConversation)
        initConvoFab.setOnClickListener {
//            Toast.makeText(this, "click", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, StartConversationActivity::class.java)

            startActivity(intent)
        }

        start()
    }

    override fun onResume() {
        super.onResume()
        start()
    }

    // Handles all the starting stuff like getting preferences and setting conversations
    private fun start() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Create empty value JUST for the first launch of the app
        if (prefs?.getString("convo_array", "") == "") {
            handleConvoJson("[]")
        }

        // Load local conversations just in case no online repo is not set
        loadLocalConvo()

        // Load online repo
        if (prefs?.getString("convo_repo", "") != "") {
            loadOnlineConvo(prefs!!.getString("convo_repo", ""))
        }

        // If there's no local conversation, reset it to some placeholder conversations
        if (prefs?.getString("convo_array", "") == "[]") {
            resetConversations()
        }
    }

    /**
     * Loads conversations from an online source
     * @param url url of the json file
     */
    private fun loadOnlineConvo(url: String) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    handleConvoJson(response)
                    Log.i(localClassName, "Set new normal conversation successfully")
                },
                Response.ErrorListener {
                    Log.i(localClassName, "Unable to load online repo")
                })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    /**
     * Loads the conversations from the built-in ones
     */
    private fun loadLocalConvo() {
        handleConvoJson(prefs!!.getString("convo_array", ""))
    }

    /**
     * Parses conversations from a JSON string
     * @param convoJson string version of the conversation (FROM JSON)
     */
    private fun handleConvoJson(convoJson: String) {
        prefs?.edit()?.putString("convo_array", convoJson)?.apply() // Override with new conversations from internet
    }

        // Re-compile all three conversation sources
        var allConvos: Array<String> = Gson().fromJson(prefs?.getString("convo_local", "[]"), Array<String>::class.java) +
                Gson().fromJson(prefs?.getString("convo_online", "[]"), Array<String>::class.java) +
                Gson().fromJson(prefs?.getString("convo_included", "[]"), Array<String>::class.java)
        // Override the master list with conversations
        prefs?.edit()?.putString("convo_array", Gson().toJson(allConvos))?.apply()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
