package edu.washington.gllc.conversationstarter.activities

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import com.google.gson.Gson
import edu.washington.gllc.conversationstarter.ConversationStarterApp
import edu.washington.gllc.conversationstarter.R
import kotlinx.android.synthetic.main.activity_tabbed_convo.*

class TabbedConvoActivity : AppCompatActivity() {
    private var prefs: SharedPreferences? = null
    private var appInstance = ConversationStarterApp.getSingletonInstance()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.tab_tabbedConvoActivity_local -> {
                setListAdapter("convo_local")
                fab_add_convo.show()
                setListViewToDelete()
                return@OnNavigationItemSelectedListener true
            }
            R.id.tab_tabbedConvoActivity_online -> {
                setListAdapter("convo_online")
                fab_add_convo.hide()
                return@OnNavigationItemSelectedListener true
            }
            R.id.tab_tabbedConvoActivity_included -> {
                setListAdapter("convo_included")
                fab_add_convo.hide()
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        var isEvil = prefs!!.getBoolean("evil_mode", false)
        if (isEvil) {
            setTheme(R.style.DarkAppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tabbed_convo)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        if(prefs?.getBoolean("evil_mode", false) == true) {
            fab_add_convo.hide()
            setListAdapter("convo_online")
            val mBottomNavView = findViewById<BottomNavigationView>(R.id.navigation)
            mBottomNavView.menu.findItem(R.id.tab_tabbedConvoActivity_local).isVisible = false
            mBottomNavView.findViewById<View>(R.id.tab_tabbedConvoActivity_local).visibility = View.GONE
            mBottomNavView.selectedItemId = R.id.tab_tabbedConvoActivity_online
        } else {
            setListAdapter("convo_local") // default to local conversations
        }
        // Allow the fab to add to the local conversations
        fab_add_convo.setOnClickListener { view ->
            val builder = AlertDialog.Builder(this)
            builder.setView(R.layout.dialog_add_convo)
                    .setTitle("Add conversation starter") //TODO: Use string resource

            // Add message to the list
            builder.setPositiveButton("Add message", DialogInterface.OnClickListener { dialog, dialogId ->
                // Add the new string
                setConversations("convo_local", Gson().fromJson(prefs?.getString("convo_local", "[]"), Array<String>::class.java)
                        + (dialog as AlertDialog).findViewById<EditText>(R.id.txt_add_convo)?.text.toString())
                appInstance.repository.addLocalStarter((dialog).findViewById<EditText>(R.id.txt_add_convo)?.text.toString())
                // Reset listView adapter to reflect the new changes
                setListAdapter("convo_local")
            })

            // Blank cancel button
            builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, dialogId ->
                // Do nothing, this is fine
            })

            builder.create().show()
        }
    }

    // Sets the adapter of the list to whatever the string key value is
    private fun setListAdapter(key: String) {
        if(prefs?.getBoolean("evil_mode", false) == true) {
            var conversations = appInstance.repository.getEvilRepoStarters()
            if (key == "convo_included") {
                conversations = appInstance.repository.getBakedInEvilStarters()
            }
            findViewById<ListView>(R.id.list_convo_edit).setOnItemClickListener { parent, view, position, id -> /* Do Nothing */ }
            findViewById<ListView>(R.id.list_convo_edit).adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, conversations)
        } else {
            var conversations = Gson().fromJson(prefs?.getString(key, "[]"), Array<String>::class.java)
            if (key == "convo_included") {
                conversations = appInstance.repository.getBakedInStarters()
            }
            if (key == "convo_local") {
                setListViewToDelete()
            } else {
                findViewById<ListView>(R.id.list_convo_edit).setOnItemClickListener { parent, view, position, id -> /* Do Nothing */ }
            }
            findViewById<ListView>(R.id.list_convo_edit).adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, conversations)
        }
    }

    // Updates the preference value of the conversation at key
    private fun setConversations(key: String, conversations: Array<String>) {
        prefs?.edit()?.putString(key, Gson().toJson(conversations))?.apply()
    }

    private fun setListViewToDelete() {
        findViewById<ListView>(R.id.list_convo_edit).setOnItemClickListener { parent, view, position, id ->
            val builder = AlertDialog.Builder(this)
            val tempLocalConversations = Gson().fromJson(prefs?.getString("convo_local", "[]"), Array<String>::class.java)
            builder.setMessage(R.string.delete_dialog_message)
                    .setTitle("${getString(R.string.delete_dialog_title)} \"${tempLocalConversations[position]}\"?")

            // Button for confirming deletion of the conversation
            builder.setPositiveButton(R.string.delete_ok, DialogInterface.OnClickListener { dialog, dialogId ->
                var newConversations = emptyArray<String>()
                for ((index, conversation) in tempLocalConversations.withIndex()) {
                    // Create new conversation list without the deleted one... this is in O(n) :(
                    if (index != position) {
                        newConversations += conversation
                    }
                    setConversations("convo_local", newConversations) // Override conversations
                    setListAdapter("convo_local") // Update list adapter to reflect changes
                }
            })
            // Cancel button for not deleting
            builder.setNegativeButton(R.string.delete_cancel, DialogInterface.OnClickListener { dialog, dialogId ->
                // Do nothing, this is fine
            })
            builder.create().show()
        }
    }

}
