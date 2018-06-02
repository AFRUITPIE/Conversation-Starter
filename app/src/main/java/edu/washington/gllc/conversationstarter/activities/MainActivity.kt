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
    private var conversations: Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        conversations = loadLocalConvo()
        if (prefs?.getString("convo_repo", "") != "") {
            loadOnlineConvo(prefs!!.getString("convo_repo", ""))
        }
    }

    override fun onResume() {
        super.onResume()
        // Gets the preferences again
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
    }

    /**
     * Loads conversations from an online source
     */
    private fun loadOnlineConvo(url: String): Array<String> {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    conversations = handleConvoJson(response)
                    Log.i(localClassName, "Set new normal conversation successfully")
                },
                Response.ErrorListener {
                    Log.i(localClassName, "Unable to load online repo")
                })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
        return handleConvoJson("Hello!") // TODO: Replace placeholder with response
    }

    /**
     * Loads the conversations from the built-in ones
     */
    private fun loadLocalConvo(): Array<String> {
        val repoString = prefs!!.getString("convo_list", "")
        return handleConvoJson(repoString)
    }

    /**
     * Parses conversations from a JSON string
     */
    private fun handleConvoJson(convoJson: String): Array<String> {
        return arrayOf("Hello!")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                return true
            }
            R.id.action_edit -> {
                if (prefs?.getString("convo_repo", "") != "") {

                    val mySnackbar = Snackbar.make(findViewById<View>(R.id.activity_main_coordinator),
                            R.string.snackbar_repo, Snackbar.LENGTH_LONG)
                    mySnackbar.setAction(R.string.snackbar_action, {
                        prefs?.edit()?.putString("convo_repo", "")?.apply()
                        startActivity(Intent(this, EditConvoActivity::class.java))
                    })
                    mySnackbar.show()
                } else {
                    startActivity(Intent(this, EditConvoActivity::class.java))
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
