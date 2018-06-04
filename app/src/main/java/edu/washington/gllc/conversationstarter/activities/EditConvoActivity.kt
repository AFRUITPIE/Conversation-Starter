package edu.washington.gllc.conversationstarter.activities

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import com.google.gson.Gson
import edu.washington.gllc.conversationstarter.R
import kotlinx.android.synthetic.main.activity_edit_convo.*


class EditConvoActivity : AppCompatActivity() {
    private var prefs: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_convo)
        setSupportActionBar(toolbar)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val listView: ListView = findViewById(R.id.list_convo_edit)
        val mAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, getConversations())
        listView.adapter = mAdapter

        // Creates a warning dialog for deleting a conversation starter
        listView.setOnItemClickListener { parent, view, position, id ->
            val builder = AlertDialog.Builder(this)
            builder.setMessage(R.string.delete_dialog_message)
                    .setTitle("${getString(R.string.delete_dialog_title)} \"${getConversations()[position]}\"?")

            // Button for confirming deletion of the converation
            builder.setPositiveButton(R.string.delete_ok, DialogInterface.OnClickListener { dialog, dialogId ->
                var newConversations = emptyArray<String>()
                for ((index, conversation) in getConversations().withIndex()) {
                    // Create new conversation list without the deleted one... this is in O(n) :(
                    if (index != position) {
                        newConversations += conversation
                    }
                    setConversations(newConversations)
                }
                // Reset listView adapter to reflect the new changes
                listView.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, getConversations())
            })

            // Cancel button for not deleting
            builder.setNegativeButton(R.string.delete_cancel, DialogInterface.OnClickListener { dialog, dialogId ->
                // Do nothing, this is fine
            })
            builder.create().show()
        }

        fab_add_convo.setOnClickListener { view ->
            val builder = AlertDialog.Builder(this)
            builder.setView(R.layout.dialog_add_convo)
                    .setTitle("Add conversation starter") //TODO: Use string resource

            // Add message to the list
            builder.setPositiveButton("Add message", DialogInterface.OnClickListener { dialog, dialogId ->
                // TODO: Use String resources
                setConversations(getConversations() + (dialog as AlertDialog).findViewById<EditText>(R.id.txt_add_convo)?.text.toString())
                // Reset listView adapter to reflect the new changes
                listView.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, getConversations())
            })

            // Blank cancel button
            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, dialogId ->
                // Do nothing, this is fine
            })

            builder.create().show()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getConversations(): Array<String> {
        return Gson().fromJson(prefs?.getString("convo_array", ""), Array<String>::class.java)
    }

    private fun setConversations(conversations: Array<String>) {
        prefs?.edit()?.putString("convo_array", Gson().toJson(conversations))?.apply()
    }
}
