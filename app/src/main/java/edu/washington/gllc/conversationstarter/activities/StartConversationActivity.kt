package edu.washington.gllc.conversationstarter.activities

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import edu.washington.gllc.conversationstarter.R

import kotlinx.android.synthetic.main.activity_start_conversation.*
import android.provider.ContactsContract
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.preference.PreferenceManager
import android.telephony.SmsManager
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import edu.washington.gllc.conversationstarter.classes.ConversationStarterData
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.util.*


class StartConversationActivity : AppCompatActivity() {

    private var prefs: SharedPreferences? = null
    private var convoStartersArray: Array<String>? = null
    private var contactDisplayName: String? = null
    private var contactPhoneNum: String? = null
    private var msgContents: String? = null
    private var timestamp: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        // Set up
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_conversation)
        setSupportActionBar(toolbar)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val smsManager = SmsManager.getDefault()

        // Disable floating action button until contact is chosen
        fab.isEnabled = false
        fab.hide()

        // Get conversation starters
        val convoStarters = prefs!!.getString("convo_array", "default")
        convoStartersArray = Gson().fromJson(convoStarters, Array<String>::class.java)

        // Set up floating action button message
        fab.setOnClickListener { view ->
            // Show the "Message Sent!" snackbar
            Snackbar.make(view, "Message sent!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            // Set the .random() function on a range
            fun ClosedRange<Int>.random() =
                    Random().nextInt(endInclusive - 1) + start
            // Get a random conversation starter from the saved array
            var convoStartersArrayLength = convoStartersArray?.size
            if (convoStartersArrayLength == null) {
                convoStartersArrayLength = 0
            }
            val randomMsgIndex = (0..(convoStartersArrayLength - 1)).random()
            val randomMsg = convoStartersArray?.get(randomMsgIndex)
            msgContents = randomMsg
            Toast.makeText(this, randomMsg, Toast.LENGTH_SHORT).show()
            // Send the random message to the chosen contact
            smsManager.sendTextMessage(
                    contactPhoneNum,
                    null,
                    msgContents,
                    null,
                    null
            )

            // Save the message to data class
            timestamp = Calendar.getInstance().time.toString()
            val newLogEntry = ConversationStarterData(contactDisplayName as String, contactPhoneNum as String, msgContents as String, timestamp as String)
            Log.i("startConversation", newLogEntry.toString())
            // Get existing log from SharedPrefs
            if (!prefs!!.contains("convo_log")) {
                with(prefs!!.edit()) {
                    putString("convo_log", "[]")
                    commit()
                }
            }

            // Add the new log entry and save it to sharedprefs
            var convoLogSerialized = prefs!!.getString("convo_log", "default")
            val collectionType: Type = object : TypeToken<Collection<ConversationStarterData>>() { }.type
            val convoLogList: MutableList<ConversationStarterData> = Gson().fromJson(convoLogSerialized, collectionType)
            convoLogList.add(newLogEntry)
            val newConvoLogArray = convoLogList.toTypedArray()
            convoLogSerialized = Gson().toJson(newConvoLogArray)
            Log.i("startConversation", convoLogSerialized)
            with(prefs!!.edit()) {
                putString("convo_log", convoLogSerialized)
                commit()
            }
        }

        // Start an intent to choose a contact via the device's default phone/contacts app
        val chooseContactBtn = findViewById<Button>(R.id.button_startConversationFragment_chooseContact)
        chooseContactBtn.setOnClickListener {
            val i = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(i, PICK_CONTACT)
        }

        // Set up back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        // If the contact selection was successful (if the request code matches PICK_CONTACT)
        if (requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            // Get the contact URI, then use a cursor to extract the contact Display Name and Phone Number
            val contactUri = data.data
            val cursor = contentResolver.query(contactUri!!, null, null, null, null)
            cursor!!.moveToFirst()
            val phoneNumColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val nameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            contactPhoneNum = cursor.getString(phoneNumColumn)
            contactDisplayName = cursor.getString(nameColumn)
            Log.i("startConversation", contactPhoneNum)
            Log.i("startConversation", contactDisplayName)
            cursor.close()

            // Update the TextViews with the selected contact's info
            val nameTextView = findViewById<TextView>(R.id.textView_startConversationFragment_contactName)
            nameTextView.text = contactDisplayName
            nameTextView.setTypeface(null, Typeface.BOLD_ITALIC)
            nameTextView.setTextColor(Color.BLACK)
            val phoneNumTextView = findViewById<TextView>(R.id.textView_startConversationFragment_contactPhoneNum)
            phoneNumTextView.text = contactPhoneNum
            phoneNumTextView.setTypeface(null, Typeface.BOLD_ITALIC)
            phoneNumTextView.setTextColor(Color.BLACK)

            // Enable the floating action bar to send the message
            fab.isEnabled = true
            fab.show()
        }
    }


    companion object {
        const val PICK_CONTACT: Int = 1337
    }
}
