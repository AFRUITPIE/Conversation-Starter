package edu.washington.gllc.conversationstarter.classes

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type

class ConversationStarterRepo {
//    private var appInstance = ConversationStarterApp.getSingletonInstance()
    private var _log = mutableListOf<ConversationStarterData>()
    private var _localStarters = mutableListOf<String>()
    private var _repoStarters = mutableListOf<String>()
    private var _evilRepoStarters = mutableListOf<String>()
    private var _bakedInStarters = listOf<String>("Hello",
            "Hey, long time no see! What's up?",
            "Lol what's up kiddo",
            "Hey what's up?",
            "You want to go get dinner or something soon?",
            "The mitochondria is the powerhouse of the cell",
            "Android development is pretty cool",
            "Want to get coffee tomorrow?"
    )
    private var _bakedInEvilStarters = listOf<String>("We need to talk.",
            "Hey, my parents aren't home ;)",
            "I hate you",
            "Why do you hate me",
            "What are you wearing",
            "Let's talk about politics",
            "I disagree with your views on religion",
            "I fart in your general direction",
            "Have you heard of Info 466?",
            "Yarr I'm a pirate an' I'ma comin' for yer booty",
            "I know it's 11am on a Monday, but do you want to go to a strip club?",
            "Hey can I buy a g?",
            "Don't tell your mom, but I'm pretty upset with her...",
            "Hey check this out https://bit.ly/IqT6zt")

    public fun getLocalStarters(): Array<String> {
        return _localStarters.toTypedArray()
    }

    public fun getLocalStartersAsJson(): String {
        return Gson().toJson(_localStarters)
    }

    public fun setLocalStarters(startersAsString: String): Array<String> {
        val givenStartersAsArray = Gson().fromJson(startersAsString, Array<String>::class.java)
        _localStarters = givenStartersAsArray.toMutableList()

        return givenStartersAsArray
    }

    public fun addLocalStarter(newStarter: String): Array<String> {
        _localStarters.add(newStarter)

        return _localStarters.toTypedArray()
    }

    public fun removeLocalStarter(starterToRemove: String, context: Context): Boolean {
        return try {
            _localStarters.remove(starterToRemove)
            saveLocalDataToStorage(context)

            true
        }
        catch(e: Exception) {
            Log.i(TAG, e.toString())

            false
        }
    }

    public fun getRepoStarters() : Array<String> {
        return _repoStarters.toTypedArray()
    }

    public fun getRepoStartersAsJson(): String {
        return Gson().toJson(_repoStarters)
    }

    public fun setRepoStarters(repoAsString: String): Array<String> {
        val givenRepoAsArray = Gson().fromJson(repoAsString, Array<String>::class.java)
        _repoStarters = givenRepoAsArray.toMutableList()

        return givenRepoAsArray
    }

    public fun getEvilRepoStarters() : Array<String> {
        return _evilRepoStarters.toTypedArray()
    }

    public fun getEvilRepoStartersAsJson(): String {
        return Gson().toJson(_evilRepoStarters)
    }

    public fun setEvilRepoStarters(repoAsString: String): Array<String> {
        val givenRepoAsArray = Gson().fromJson(repoAsString, Array<String>::class.java)
        _evilRepoStarters = givenRepoAsArray.toMutableList()

        return givenRepoAsArray
    }

    public fun getBakedInStarters(): Array<String> {
        return _bakedInStarters.toTypedArray()
    }

    public fun getBakedInStartersAsJson(): String {
        return Gson().toJson(_bakedInStarters)
    }

    public fun getBakedInEvilStarters(): Array<String> {
        return _bakedInEvilStarters.toTypedArray()
    }

    public fun getBakedInEvilStartersAsJson(): String {
        return Gson().toJson(_bakedInEvilStarters)
    }

    public fun getAllEvilModeStarters(): Array<String> {
        return (_evilRepoStarters + _bakedInEvilStarters).toTypedArray()
    }

    public fun getAllStarters(): Array<String> {
        return (_bakedInStarters + _repoStarters + _localStarters).toTypedArray()
    }

    public fun saveLocalDataToStorage(context: Context) {
        try {
            val intStorageFile = File(context.applicationContext.filesDir, "cs_local")
            if (intStorageFile.exists()) {
                intStorageFile.delete()
            }
            val stringToSave = getLocalStartersAsJson()
            intStorageFile.createNewFile()
            intStorageFile.writeText(stringToSave)
        }
        catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    public fun expandLocalDataFromStorage(context: Context) {
        try {
            val intStorageFile = File(context.applicationContext.filesDir, "cs_local")
            if (intStorageFile.exists()) {
                val stringFromStorage = intStorageFile.readText()
                setLocalStarters(stringFromStorage)
            }
        }
        catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    public fun saveLogDataToStorage(context: Context, logArray: Array<ConversationStarterData>?) {
        try {
            val intStorageFile = File(context.applicationContext.filesDir, "cs_log")
            if (intStorageFile.exists()) {
                intStorageFile.delete()
            }
            var stringToSave = ""
            if (logArray != null) {
                stringToSave = Gson().toJson(logArray)
            } else {
                stringToSave = Gson().toJson(_log)
            }
            intStorageFile.createNewFile()
            intStorageFile.writeText(stringToSave)
        }
        catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    public fun expandLogDataFromStorage(context: Context) {
        try {
            val intStorageFile = File(context.applicationContext.filesDir, "cs_log")
            if (intStorageFile.exists()) {
                val stringFromStorage = intStorageFile.readText()
                val collectionType: Type = object : TypeToken<Collection<ConversationStarterData>>() { }.type
                val convoLogList: MutableList<ConversationStarterData> = Gson().fromJson(stringFromStorage, collectionType)
                _log = convoLogList
            }
        }
        catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    public fun getLog(): Array<ConversationStarterData> {
        return _log.toTypedArray()
    }

    public fun addToLog(context: Context, newEntry: ConversationStarterData) {
        _log.add(newEntry)
        saveLogDataToStorage(context, null)
    }

    companion object {
        const val TAG = "ConversationStarterRepo"
    }
}