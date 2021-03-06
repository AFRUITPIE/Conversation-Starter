package edu.washington.gllc.conversationstarter.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.ContactsContract
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import edu.washington.gllc.conversationstarter.ConversationStarterApp
import edu.washington.gllc.conversationstarter.R
import edu.washington.gllc.conversationstarter.classes.ConversationStarterData
import kotlinx.android.synthetic.main.activity_start_conversation.*
import java.util.*


class StartConversationActivity : AppCompatActivity() {

    private var prefs: SharedPreferences? = null
    private var convoStartersArray: Array<String>? = null
    private var contactDisplayName: String? = null
    private var contactPhoneNum: String? = null
    private var msgContents: String? = null
    private var timestamp: String? = null
    private var appInstance = ConversationStarterApp.getSingletonInstance()



    override fun onCreate(savedInstanceState: Bundle?) {
        // Set up
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        var isEvil = prefs!!.getBoolean("evil_mode", false)
        if (isEvil) {
            setTheme(R.style.DarkAppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_conversation)
        setSupportActionBar(toolbar)
        val smsManager = SmsManager.getDefault()

        // Disable floating action button until contact is chosen
        fab.isEnabled = false
        fab.hide()

        // Get conversation starters
        convoStartersArray = if (prefs!!.getBoolean("evil_mode", false)) {
            appInstance.repository.getAllEvilModeStarters()
        } else {
            if (prefs!!.getBoolean("ignore_prebaked", false)) {
                (appInstance.repository.getRepoStarters() + appInstance.repository.getLocalStarters())
            } else {
                appInstance.repository.getAllStarters()
            }
        }

        // Set up floating action button message
        fab.setOnClickListener { view ->
            Log.i(TAG, Gson().toJson(appInstance.repository.getLocalStarters()))
            // Show the "Message Sent!" snackbar
//            Snackbar.make(view, "Message sent!", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
            // Set the .random() function on a range
            fun ClosedRange<Int>.random() =
                    Random().nextInt(endInclusive - start) + start
            // Get a random conversation starter from the saved array
            val convoStartersArrayLength = convoStartersArray?.size
            if (convoStartersArrayLength == null || convoStartersArrayLength == 0) {
                Toast.makeText(this,
                        "Uh-oh! It looks like you don't have any conversation starters! Go back to the main menu and add some starters, or enable the built-in starters.",
                        Toast.LENGTH_LONG)
                        .show()
            } else {
                msgContents = if (convoStartersArrayLength == 1) {
                    convoStartersArray?.get(0)
                } else {
                    val randomMsgIndex = (0..(convoStartersArrayLength)).random()
                    Log.i(TAG, randomMsgIndex.toString())
                    val randomMsg = convoStartersArray?.get(randomMsgIndex)
                    randomMsg
                }

                // If evil mode is off, send normally.
                // If evil mode is on, send it to a random contact 50% of the time
                if (prefs!!.getBoolean("evil_mode", false) && Random().nextBoolean()) {
                    Log.i(localClassName, "Sending a message to the wrong contact")
                    //  Get a random phone number from sent messages in the scariest way possible
                    val uri = Uri.parse("content://sms")
                    val cursor = this.contentResolver.query(uri, null, null, null, null)
                    val randomIndex = Random().nextInt(cursor.count)
                    if (cursor.moveToFirst()) {
                        for (i in 0..cursor.count) {
                            val phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("address")).toString()
                            if (i == randomIndex) {
                                smsManager.sendTextMessage(
                                        phoneNumber,
                                        null,
                                        msgContents,
                                        null,
                                        null
                                )
                            }
                        }
                    }
                } else {
                    // Send the random message to the chosen contact
                    Log.i(localClassName, "Sending a message to the correct contact")
                    smsManager.sendTextMessage(
                            contactPhoneNum,
                            null,
                            msgContents,
                            null,
                            null
                    )
                }

                Toast.makeText(this, "Conversation started with $contactDisplayName. Message: \"$msgContents\"", Toast.LENGTH_SHORT).show()

                // If using Evil Mode, if recipient does not respond in 5 seconds, another text is sent.
                if (prefs!!.getBoolean("evil_mode", false)){
                    if(Random().nextInt(10) == 4)
                    Handler().postDelayed({
                        smsManager.sendTextMessage(
                                contactPhoneNum,
                                null,
                                "Answer me pls",
                                null,
                                null
                        )
                        Toast.makeText(this, "Conversation restarted with $contactDisplayName. Message: \"$msgContents\"", Toast.LENGTH_SHORT).show()

                    }, 1000 * 15 * 60)
                }

                // Save the message to data class
                timestamp = Calendar.getInstance().time.toString()
                val newLogEntry = ConversationStarterData(contactDisplayName as String, contactPhoneNum as String, msgContents as String, timestamp as String)
                Log.i(TAG, newLogEntry.toString())
                // Get existing log from SharedPrefs
//                if (!prefs!!.contains("convo_log")) {
//                    with(prefs!!.edit()) {
//                        putString("convo_log", "[]")
//                        commit()
//                    }
//                }

                // Add the new log entry and save it to sharedprefs
//                var convoLogSerialized = prefs!!.getString("convo_log", "default")
//                val collectionType: Type = object : TypeToken<Collection<ConversationStarterData>>() { }.type
//                val convoLogList: MutableList<ConversationStarterData> = Gson().fromJson(convoLogSerialized, collectionType)
//                convoLogList.add(newLogEntry)
//                val newConvoLogArray = convoLogList.toTypedArray()
//                convoLogSerialized = Gson().toJson(newConvoLogArray)
//                Log.i(TAG, convoLogSerialized)
//                with(prefs!!.edit()) {
//                    putString("convo_log", convoLogSerialized)
//                    commit()
//                }
                appInstance.repository.addToLog(this, newLogEntry)

                // Return to main activity
                val intent = Intent(this, MainActivity::class.java)
                // startActivity(intent)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // If the contact selection was successful (if the request code matches PICK_CONTACT)
        if (requestCode == PICK_CONTACT && resultCode == Activity.RESULT_OK) {
            // Get the contact URI, then use a cursor to extract the contact Display Name and Phone Number
            val contactUri = data?.data
            val cursor = contentResolver.query(contactUri!!, null, null, null, null)
            cursor!!.moveToFirst()
            val phoneNumColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val nameColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            contactPhoneNum = cursor.getString(phoneNumColumn)
            contactDisplayName = cursor.getString(nameColumn)
            Log.i(TAG, contactPhoneNum)
            Log.i(TAG, contactDisplayName)
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
        const val TAG: String = "StartConversation"
    }
}
