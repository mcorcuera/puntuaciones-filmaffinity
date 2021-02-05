package com.mikel.filmaffinity.intro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.app.NavigationPolicy
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide
import com.mikel.filmaffinity.BuildConfig
import com.mikel.filmaffinity.PermissionChecker
import com.mikel.filmaffinity.R


class FilmaffinityIntroActivity : IntroActivity() {
    private val checker = PermissionChecker(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val skipFirst = intent.getBooleanExtra("skipFirst", false)

        val introSlide = fragmentPage()
            .fragment(OnboardingStepIntro.newInstance(showAllReady = false))
            .build()

        val permissionSlide = fragmentPage()
            .fragment(OnboardingStepPermission())
            .build()


        val instructionsDetails = fragmentPage()
            .fragment(OnboardingStepHowItWorks())
            .build()

        val instructionShare = fragmentPage()
            .fragment(OnboardingStepShare())
            .build()

        val instructionRating = fragmentPage()
            .fragment(OnboardingFinalStep())
            .build()

        isButtonBackVisible = false

        if (!skipFirst) {
            addSlide(introSlide)
        }

        if (!checker.isRequiredPermissionGranted && BuildConfig.BUILD_TYPE != "firebaseTest") {
            addSlide(permissionSlide)
            setNavigationPolicy(object : NavigationPolicy {
                override fun canGoForward(position: Int): Boolean {
                    if (getSlide(position) == permissionSlide) {
                        return checker.isRequiredPermissionGranted
                    }

                    return true
                }

                override fun canGoBackward(position: Int): Boolean {
                    return getSlide(position).canGoBackward()
                }
            })
        }

        addSlide(instructionsDetails)
        addSlide(instructionShare)
        addSlide(instructionRating)

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorAccent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PermissionChecker.REQUIRED_PERMISSION_REQUEST_CODE) {
            if (checker.isRequiredPermissionGranted) {
                if (slides.size > 0) {
                    nextSlide()
                }
            } else {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.permission_draw_declined_title))
                    .setMessage(getString(R.string.permission_draw_declined_description))
                    .setPositiveButton(getString(R.string.permission_draw_declined_try_again)) { _, _ ->
                        checker.openPermissions(this)
                    }
                    .setNegativeButton(getString(R.string.permission_draw_declined_cancel)) { dialog, _ -> dialog.cancel() }
                    .show()
            }
        }
    }

    private fun fragmentPage(): FragmentSlide.Builder {
        return FragmentSlide.Builder()
            .backgroundDark(R.color.colorBackgroundDark)
            .background(R.color.colorBackground)
    }
}
