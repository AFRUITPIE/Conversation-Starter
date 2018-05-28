package edu.washington.gllc.conversationstarter.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.washington.gllc.conversationstarter.R

/**
 * A placeholder fragment containing a simple view.
 */
class EditConvoActivityFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_convo, container, false)

    }
}
