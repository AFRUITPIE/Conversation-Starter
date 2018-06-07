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
import edu.washington.gllc.conversationstarter.ConversationStarterApp
import edu.washington.gllc.conversationstarter.R

class MainActivity : AppCompatActivity() {
    private var appInstance = ConversationStarterApp.getSingletonInstance()
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set up
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        // Sets up the first menu item's (starting a conversation's) button
        val initConvoFab = findViewById<FloatingActionButton>(R.id.fab_mainFragment_startConversation)
        initConvoFab.setOnClickListener {
            val intent = Intent(this, StartConversationActivity::class.java)
            startActivity(intent)
        }

        // Sets up the second main menu item's button (editing conversation starters)
        findViewById<FloatingActionButton>(R.id.fab_mainFragment_editConversationStarters)
        val editConvoStartersFab = findViewById<FloatingActionButton>(R.id.fab_mainFragment_editConversationStarters)
        editConvoStartersFab.setOnClickListener {
            startActivity(Intent(this, TabbedConvoActivity::class.java))
        }

        // Sets up the third main menu item's button (viewing conversation history)
        val viewConvoFab = findViewById<FloatingActionButton>(R.id.fab_mainFragment_viewConversations)
        viewConvoFab.setOnClickListener {
            startActivity(Intent(this, ViewConvoActivity::class.java))
        }

        // Initialize application
        start()
    }

    override fun onResume() {
        super.onResume()
        start()
    }

    // Handles all the starting stuff like getting preferences and setting conversations
    private fun start() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        // Ensures there are some garbage placeholders
//        handleConvoJson("convo_included", "[\"Hello\", \"Hey, long time no see! What's up?\", \"Lol what's up kiddo\", \"Hey what's up?\", \"You want to go get dinner or something soon?\", \"The mitochondria is the powerhouse of the cell\", \"Android development is pretty cool\", \"Want to get coffee tomorrow?\", \"This is from the PLACEHOLDERS!\"]")

        // Load online repo if one is set
        if (prefs?.getString("convo_repo", "") != "") {
            loadOnlineConvo(prefs!!.getString("convo_repo", ""))
        }

        // Log the current state of the array
        Log.i(localClassName, "Current conversation array is: ${prefs?.getString("convo_array", "ERROR LOADING ARRAY")}")
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
                    handleConvoJson("convo_online", response)
                    Log.i(localClassName, "Set new normal conversation successfully")
                },
                Response.ErrorListener {
                    Log.i(localClassName, "Unable to load online repo")
                })
        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    /**
     * Parses conversations from an Array<String>
     * @param key key of the starter repo you want to add to
     * @param convoArray Array<String> of conversation starters
     */
//    private fun handleConvoArray(key: String, convoArray: Array<String>) {
//        when(key) {
//            "convo_local" -> {
//                appInstance.repository.setLocalStarters(convoArray)
//            }
//        }
//        prefs?.edit()?.putString(key, convoJson)?.apply() // Override with new conversations from internet
//        // Re-compile all three conversation sources
//        var allConvos: Array<String> = Gson().fromJson(prefs?.getString("convo_local", "[]"), Array<String>::class.java) +
//                Gson().fromJson(prefs?.getString("convo_online", "[]"), Array<String>::class.java) +
//                Gson().fromJson(prefs?.getString("convo_included", "[]"), Array<String>::class.java)
//        // Override the master list with conversations
//        prefs?.edit()?.putString("convo_array", Gson().toJson(allConvos))?.apply()
//    }

    /**
     * Parses conversations from a JSON string
     * @param key key of the conversation you want to add to
     * @param convoJson string version of the conversation (FROM JSON)
     */
    private fun handleConvoJson(key: String, convoJson: String) {
        // Update the repo
        when(key) {
            "convo_local" -> appInstance.repository.setLocalStarters(convoJson)
            "convo_online" -> appInstance.repository.setRepoStarters(convoJson)
        }
        prefs?.edit()?.putString(key, convoJson)?.apply() // Override with new conversations from internet
        // Re-compile all three conversation sources
        var allConvos: Array<String> = appInstance.repository.getAllStarters()
//                Gson().fromJson(prefs?.getString("convo_local", "[]"), Array<String>::class.java) +
//                Gson().fromJson(prefs?.getString("convo_online", "[]"), Array<String>::class.java) +
//                Gson().fromJson(prefs?.getString("convo_included", "[]"), Array<String>::class.java)
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
