package edu.washington.gllc.conversationstarter.activities

import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.washington.gllc.conversationstarter.R

/**
 * A placeholder fragment containing a simple view.
 */
class StartConversationActivityFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_start_conversation, container, false)
    }
}
