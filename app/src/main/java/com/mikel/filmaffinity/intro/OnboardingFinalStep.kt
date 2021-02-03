package com.mikel.filmaffinity.intro

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mikel.filmaffinity.R
import kotlinx.android.synthetic.main.fragment_onboarding_step_permission.*

class OnboardingFinalStep : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_onboarding_final_step, container, false)
    }
}