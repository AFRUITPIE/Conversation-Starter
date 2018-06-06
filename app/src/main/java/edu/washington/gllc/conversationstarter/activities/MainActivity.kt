package edu.washington.gllc.conversationstarter.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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

        start()
    }

    override fun onResume() {
        super.onResume()
        start()

    }

    // Handles all the starting stuff like getting preferences and setting conversations
    private fun start() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Ensure included conversations are set
        updateConversationAtKey("convo_included", "[\"Hello\", \"Hey, long time no see! What's up?\", \"Lol what's up kiddo\", \"Hey what's up?\", \"You want to go get dinner or something soon?\", \"The mitochondria is the powerhouse of the cell\", \"Android development is pretty cool\", \"Want to get coffee tomorrow?\", \"This is from the PLACEHOLDERS!\"]")

        // Add empty arrays if not already set
        if (prefs?.getString("convo_included", "") == "") {
            updateConversationAtKey("convo_included", "[]")
        }
        if (prefs?.getString("convo_online", "") == "") {
            updateConversationAtKey("convo_online", "[]")
        }

        // Load online repo if there is a repo set
        if (prefs?.getString("convo_repo", "") != "") {
            loadOnlineConvo(prefs!!.getString("convo_repo", ""))
        }

        Log.i("Test", prefs?.getString("convo_array", ""))
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
                    updateConversationAtKey("convo_online", response)
                    Log.i(localClassName, "Set new normal conversation successfully")
                },
                Response.ErrorListener {
                    Log.i(localClassName, "Unable to load online repo")
                })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    /**
     * Parses conversations from a JSON string
     * @param convoJson string version of the conversation (FROM JSON)
     */
    private fun updateConversationAtKey(key: String, convoJson: String) {
        prefs?.edit()?.putString(key, convoJson)?.apply() // Override with new conversations from the key

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
