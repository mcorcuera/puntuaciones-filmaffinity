package com.mikel.filmaffinity.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import com.mikel.filmaffinity.R
import kotlinx.android.synthetic.main.fragment_onboarding_step_intro.*

private const val ARG_SHOW_ALL_READY = "showAllReady"

class OnboardingStepIntro : Fragment() {

    private var showAllReady: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            showAllReady = it.getBoolean(ARG_SHOW_ALL_READY, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_onboarding_step_intro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (showAllReady) {
            allReadyText.visibility = VISIBLE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(showAllReady: Boolean) =
                OnboardingStepIntro().apply {
                    arguments = Bundle().apply {
                        putBoolean(ARG_SHOW_ALL_READY, showAllReady)
                    }
                }
    }
}
