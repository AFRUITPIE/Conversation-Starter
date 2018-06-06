package edu.washington.gllc.conversationstarter.activities

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.ArrayAdapter
import android.widget.ListView
import edu.washington.gllc.conversationstarter.R
import org.json.JSONObject

class ViewConvoActivity : AppCompatActivity() {
    private var prefs: SharedPreferences? = null

    private var JSONMessagesList: ArrayList<JSONObject>? = null

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
            read all keys following some convention (msg1, msg2, ...., msg#)
            convert values (String) to JSON object (already JSON formatted)
                var messagesList = ArrayList<JSONObject>()
                SharedPreferences sharedPrefs = this.getSharedPreferences( .... )
                String defValue = "";
                int i = 0;
                while (defValue != "done")
                    String msg = sharedPrefs.getString("msg" + i, "done"); // returns "done" if msg does not exist
                    JSONObject jsonMsg = JSONObject(msg)
                    messagesList.add(jsonMsg)
                    i++;

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
        val listView: ListView = findViewById(R.id.list_convo_edit)
        val JSONMessageList = getMessages()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, getMessages())
        listView.adapter = adapter

        listView.setOnItemClickListener { parent, view, position, id ->

        }
    }

    private fun getMessages(): List<String> {
        var messagesList = ArrayList<String>()
        var defValue = ""
        var i = 0
        while (defValue != "done") {
            val msg = prefs?.getString("msg" + i, "done")
            val jsonMsg = JSONObject(msg)
            JSONMessagesList?.add(jsonMsg)
            i++
        }
        return messagesList
    }
}
