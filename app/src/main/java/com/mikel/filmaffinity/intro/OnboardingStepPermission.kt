package com.mikel.filmaffinity.intro

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.mikel.filmaffinity.PermissionChecker
import com.mikel.filmaffinity.R
import kotlinx.android.synthetic.main.fragment_onboarding_step_permission.*

class OnboardingStepPermission : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_onboarding_step_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        permissionButton.setOnClickListener { PermissionChecker(requireContext()).openPermissions(requireActivity()) }

        var instructionGif = R.drawable.permission_10

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            instructionGif = R.drawable.permission_11
        }

        Glide.with(requireContext()).load(instructionGif).into(permissionImage)
    }
}