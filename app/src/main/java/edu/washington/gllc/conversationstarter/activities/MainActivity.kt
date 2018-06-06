package edu.washington.gllc.conversationstarter.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import edu.washington.gllc.conversationstarter.R

class MainActivity : AppCompatActivity() {
    private var prefs: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
//        setText()

        val initConvoFab = findViewById<FloatingActionButton>(R.id.fab_mainFragment_startConversation)
        initConvoFab.setOnClickListener {
            Toast.makeText(this, "click", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ControllerActivity::class.java)

            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        // Gets the preferences again
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
//        setText()
    }

    /**
     * TODO: Remove this useless function
     */
//    private fun setText() {
//        findViewById<TextView>(R.id.hello).text = prefs?.getBoolean("evil_mode", false).toString()
//    }

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
            else -> super.onOptionsItemSelected(item)
        }
    }
}
