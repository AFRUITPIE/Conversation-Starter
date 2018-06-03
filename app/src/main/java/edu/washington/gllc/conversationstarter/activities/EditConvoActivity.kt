package edu.washington.gllc.conversationstarter.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.ListView
import edu.washington.gllc.conversationstarter.R
import kotlinx.android.synthetic.main.activity_edit_convo.*

class EditConvoActivity : AppCompatActivity() {
    private var prefs: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_convo)
        setSupportActionBar(toolbar)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val conversations: Array<String> = intent.extras["conversations"] as Array<String>
        val listView: ListView = findViewById(R.id.list_convo_edit)
        val mAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, conversations)
        listView.adapter = mAdapter
        listView.setOnItemClickListener { parent, view, position, id ->
            // TODO: Add option to delete
        }

        // TODO: Make this fab open a dialog for adding a conversation
        fab_add_convo.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}