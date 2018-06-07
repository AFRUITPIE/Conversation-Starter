package edu.washington.gllc.conversationstarter.activities

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.washington.gllc.conversationstarter.ConversationStarterApp
import edu.washington.gllc.conversationstarter.R
import edu.washington.gllc.conversationstarter.classes.ConversationStarterData
import kotlinx.android.synthetic.main.activity_start_conversation.*
import java.lang.reflect.Type

class ViewConvoActivity : AppCompatActivity() {
    private var prefs: SharedPreferences? = null
    private var appInstance = ConversationStarterApp.getSingletonInstance()

    private lateinit var conversationLog : List<ConversationStarterData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_convo)
        setSupportActionBar(toolbar)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val listView: ListView = findViewById(R.id.list_view_convo)

        // list reversed so that most recent messages appear on top in list view
        conversationLog = getConvoLog().reversed()

        // when conversationLog is empty, "There are no messages" is displayed
        // otherwise we want to populate the list view
        if (conversationLog.isNotEmpty()) {
            findViewById<TextView>(R.id.textView_no_messages).visibility = View.GONE
            val conversationsInfo = getConversationsInfo()
            val messages = conversationsInfo.messages
            val messageInfo = conversationsInfo.messageInfo
            val list = ArrayList<Map<String, String>>()

            // list view to have item (message) and subitem (contact, timestamp)
            for (i in messages.indices) {
                val item = HashMap<String, String>(2)
                item["line1"] = messages[i]
                item["line2"] = messageInfo[i]
                list.add(item)
            }
            val adapter = SimpleAdapter(this, list, R.layout.two_line_list_item,
                    arrayOf("line1", "line2"), intArrayOf(R.id.line1, R.id.line2))
            listView.adapter = adapter

            // send user to SMS app, specifically to messaging thread with given contact
            listView.setOnItemClickListener { _, _, position, _ ->
                val phoneNum = conversationLog!![position].recipientPhoneNum
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("smsto:" + phoneNum)))
            }
        }

        // Set up back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun getConvoLog() : List<ConversationStarterData> {
        // conversation log in shared preferences, retrieve and deserialize into a list
//        var convoLogSerialized = prefs!!.getString("convo_log", "default")
//        if (convoLogSerialized == "default") {
//            return ArrayList<ConversationStarterData>()
//        } else {
//            val collectionType: Type = object : TypeToken<Collection<ConversationStarterData>>() {}.type
//            return Gson().fromJson(convoLogSerialized, collectionType)
//        }
        return appInstance.repository.getLog().toList()
    }

    private fun getConversationsInfo() : MessagesInfo {
        var messages = ArrayList<String>()
        var messageInfo = ArrayList<String>()
        for (msg: ConversationStarterData in conversationLog) {
            messages.add(msg.messageContents)
            messageInfo.add(msg.recipientName + ": " + msg.recipientPhoneNum + "\n" + msg.timestamp)
        }
        return MessagesInfo(messages, messageInfo)
    }

    data class MessagesInfo(val messages : List<String>, val messageInfo : List<String>)
}
