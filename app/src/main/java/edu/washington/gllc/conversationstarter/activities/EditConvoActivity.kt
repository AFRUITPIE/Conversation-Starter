package edu.washington.gllc.conversationstarter.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import edu.washington.gllc.conversationstarter.R
import kotlinx.android.synthetic.main.activity_edit_convo.*

class EditConvoActivity : AppCompatActivity() {
    private var prefs: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_convo)
        setSupportActionBar(toolbar)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // TODO: Get this convo list from SharedPreferences, probably as the form of a JSON string
        val convoList = ArrayList<String>()

        // TODO: Make this display a list where clicking on one of the convos allows for deleting

        // TODO: Make this fab open a dialog for adding a conversation
        fab_add_convo.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
