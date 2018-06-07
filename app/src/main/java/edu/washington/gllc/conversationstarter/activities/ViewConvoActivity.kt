package edu.washington.gllc.conversationstarter.activities

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.washington.gllc.conversationstarter.R
import edu.washington.gllc.conversationstarter.classes.ConversationStarterData
import java.lang.reflect.Type

class ViewConvoActivity : AppCompatActivity() {
    private var prefs: SharedPreferences? = null

    private lateinit var conversationLog : List<ConversationStarterData>

    /*
    Start Conversation: file containing sent messages saved in Shared Preferences
        File format:
            {
                recipientName: String
                recipientPhoneNumber: String
                messageContent: String
                timestamp: datetime
            }

    View Conversations: Read the file and display in a list view
        Reading the file:
            read conversation log

        ListView of messages in order of timestamp (assuming ordered already in list msg# -> msg1, most recent to oldest)
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, messagesList)
            listView.adapter = adapter

        Tapping on an item opens up the default SMS app and thread for the contact
            .setOnClickListener {
                val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("smsto:#phonenumber"))
                startActivity(smsIntent)
            }
        Should return to app from SMS thread
*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_convo)

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val listView: ListView = findViewById(R.id.list_view_convo)

        conversationLog = getConvoLog()
        if (conversationLog.isNotEmpty()) {
            findViewById<TextView>(R.id.textView_no_messages).visibility = View.GONE
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, getMessages())
            listView.adapter = adapter

            listView.setOnItemClickListener { _, _, position, _ ->
                val index = conversationLog!!.size - 1 - position
                val phoneNum = conversationLog!![index].recipientPhoneNum
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("smsto:" + phoneNum)))
            }
        }
    }

    private fun getConvoLog() : List<ConversationStarterData> {
        var convoLogSerialized = prefs!!.getString("convo_log", "default")
        if (convoLogSerialized == "default") {
            return ArrayList<ConversationStarterData>()
        } else {
            val collectionType: Type = object : TypeToken<Collection<ConversationStarterData>>() {}.type
            return Gson().fromJson(convoLogSerialized, collectionType)
        }
    }

    private fun getMessages() : List<String> {
        var messages = ArrayList<String>()
        for (msg: ConversationStarterData in conversationLog) {
            messages.add(msg.messageContents)
        }
        return messages
    }
}
