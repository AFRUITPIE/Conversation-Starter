package edu.washington.gllc.conversationstarter.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import edu.washington.gllc.conversationstarter.R

class MainActivity : AppCompatActivity() {
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        start()
    }

    override fun onResume() {
        super.onResume()
        start()
    }

    // Handles all the starting stuff like getting preferences and setting conversations
    private fun start() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Ensures this preference is not null, that causes runtime errors for first launch
        // handleConvoJson("[\"Placeholder conversation\"]")


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

    // Resets conversations to this ugly placeholder value
    private fun resetConversations() {
        handleConvoJson("[\"Hello\", \"Hey, long time no see! What's up?\", \"Lol what's up kiddo\", \"Hey what's up?\", \"You want to go get dinner or something soon?\", \"The mitochondria is the powerhouse of the cell\", \"Android development is pretty cool\", \"Want to get coffee tomorrow?\", \"This is from the PLACEHOLDERS!\"]")
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
            R.id.action_edit -> {
                val intent = Intent(this, EditConvoActivity::class.java)
                // intent.putExtra("conversations", conversations) // Add conversations to intent
                if (prefs?.getString("convo_repo", "") != "") {
                    // Snackbar to warn user of online repo being set,
                    // also allows for clearing of the online repo
                    val mySnackbar = Snackbar.make(findViewById<View>(R.id.activity_main_coordinator),
                            R.string.snackbar_repo, Snackbar.LENGTH_LONG)
                    mySnackbar.setAction(R.string.snackbar_action, {
                        prefs?.edit()?.putString("convo_repo", "")?.apply()
                        resetConversations()
                        startActivity(intent)
                    })
                    mySnackbar.show()
                } else {
                    startActivity(intent)
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
