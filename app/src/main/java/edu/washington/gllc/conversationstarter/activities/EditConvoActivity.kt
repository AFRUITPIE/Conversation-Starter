package edu.washington.gllc.conversationstarter.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import edu.washington.gllc.conversationstarter.R
import kotlinx.android.synthetic.main.activity_edit_convo.*

class EditConvoActivity : AppCompatActivity() {
    private var prefs: SharedPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_convo)
        setSupportActionBar(toolbar)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        // Get list of subjects from the application
//        val listOfSubjects = ArrayList<String>()
//        for ((index, quiz) in app.quizRepo.Quizzes.withIndex()) {
//            listOfSubjects.add(index, quiz.title)
//        }
//
//        val subjectList: ListView = view.findViewById(R.id.list_subject)
//        val mAdapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, android.R.id.text1, listOfSubjects)
//        subjectList.adapter = mAdapter
//        app.listAdapter = mAdapter
//        subjectList.setOnItemClickListener { parent, view, position, id -> openQuiz(view, position, app) }

        val convoList = ArrayList<String>()

        fab_add_convo.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
