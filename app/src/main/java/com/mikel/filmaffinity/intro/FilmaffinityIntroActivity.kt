package com.mikel.filmaffinity.intro

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.app.NavigationPolicy
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide
import com.mikel.filmaffinity.BuildConfig
import com.mikel.filmaffinity.PermissionChecker
import com.mikel.filmaffinity.R


class FilmaffinityIntroActivity : IntroActivity() {
    private val checker = PermissionChecker(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val introSlide = basePage()
            .title(R.string.app_headline)
            .description(R.string.app_short_description)
            .image(R.drawable.intro_logos)
            .build()

        val permissionSlide = basePage()
            .title(R.string.permission_title)
            .description(R.string.permission_description)
            .buttonCtaLabel(R.string.permission_cta)
            .image(R.drawable.permission)
            .buttonCtaClickListener {
                openPermissions()
            }
            .build()

        val instructionsDetails = basePage()
            .title(R.string.how_it_works_title)
            .image(R.drawable.instruction_1)
            .descriptionHtml(getString(R.string.how_it_works_description))
            .build()

        val instructionShare = basePage()
            .title(R.string.share_link_title)
            .image(R.drawable.more_options)
            .descriptionHtml(getString(R.string.share_link_description))
            .build()

        val instructionRating = basePage()
            .title(R.string.rating_title)
            .image(R.drawable.rating_screen)
            .descriptionHtml(getString(R.string.rating_description))
            .build()

        isButtonBackVisible = false

        addSlide(introSlide)

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
                        openPermissions()
                    }
                    .setNegativeButton(getString(R.string.permission_draw_declined_cancel)) { dialog, _ -> dialog.cancel() }
                    .show()
            }
        }
    }

    private fun openPermissions() {
        val intent = checker.createRequiredPermissionIntent()
        startActivityForResult(
            intent,
            PermissionChecker.REQUIRED_PERMISSION_REQUEST_CODE
        )
    }

    private fun basePage(): SimpleSlide.Builder {
        return SimpleSlide.Builder()
            .scrollable(true)
            .backgroundDark(R.color.white)
            .background(R.color.colorBackground)
    }
}
