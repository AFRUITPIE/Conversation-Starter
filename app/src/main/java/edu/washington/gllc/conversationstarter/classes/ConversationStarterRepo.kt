package edu.washington.gllc.conversationstarter.classes

import com.google.gson.Gson

class ConversationStarterRepo {
    private var _localStarters = mutableListOf<String>()
    private var _repoStarters = mutableListOf<String>()
    private var _evilRepoStarters = mutableListOf<String>()
    private var _bakedInStarters = listOf<String>("One", "Two", "Freddy's coming for you")

    public fun getLocalStarters(): Array<String> {
        return _localStarters.toTypedArray()
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

    public fun getRepoStarters() : Array<String> {
        return _repoStarters.toTypedArray()
    }

    public fun setRepoStarters(repoAsString: String): Array<String> {
        val givenRepoAsArray = Gson().fromJson(repoAsString, Array<String>::class.java)
        _repoStarters = givenRepoAsArray.toMutableList()

        return givenRepoAsArray
    }

    public fun getEvilRepoStarters() : Array<String> {
        return _evilRepoStarters.toTypedArray()
    }

    public fun setEvilRepoStarters(repoAsString: String): Array<String> {
        val givenRepoAsArray = Gson().fromJson(repoAsString, Array<String>::class.java)
        _evilRepoStarters = givenRepoAsArray.toMutableList()

        return givenRepoAsArray
    }

    public fun getBakedInStarters(): Array<String> {
        return _bakedInStarters.toTypedArray()
    }

    companion object {
        const val TAG = "ConversationStarterRepo"
    }
}