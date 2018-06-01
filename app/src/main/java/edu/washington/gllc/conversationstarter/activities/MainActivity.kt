package edu.washington.gllc.conversationstarter.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import edu.washington.gllc.conversationstarter.R


class MainActivity : AppCompatActivity() {
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        setText()
    }

    override fun onResume() {
        super.onResume()
        // Gets the preferences again
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        setText()
    }

    /**
     * TODO: Remove this useless function
     */
    private fun setText() {
        findViewById<TextView>(R.id.hello).text = prefs?.getBoolean("evil_mode", false).toString()
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
