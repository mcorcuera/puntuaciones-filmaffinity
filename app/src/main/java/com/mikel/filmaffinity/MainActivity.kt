package com.mikel.filmaffinity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.mikel.filmaffinity.intro.FilmaffinityIntroActivity


const val REQUEST_CODE_INTRO = 1234

class MainActivity : AppCompatActivity() {

    private lateinit var sp: SharedPreferences
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var isIntroOpen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        sp = getSharedPreferences("FilmaffinityPreferences", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)

        val content = findViewById<View>(R.id.main_content)
        content.visibility = INVISIBLE

        findViewById<Button>(R.id.howItWorksButton).setOnClickListener {
            if (!isIntroOpen) {
                showIntro(skipFirst = true)
                isIntroOpen = true
            }
        }

        if (!hasCompletedIntro()) {
            showIntro()
        } else {
            startMainApp()
        }
    }

    private fun showIntro(skipFirst: Boolean = false) {
        val intent =
            Intent(this, FilmaffinityIntroActivity::class.java) // Call the AppIntro java class
        intent.putExtra("skipFirst", skipFirst);
        startActivityForResult(intent, REQUEST_CODE_INTRO)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_INTRO) {
            isIntroOpen = false
            if (resultCode == RESULT_OK) {
                completeIntro()
            }
        }
    }

    private fun hasCompletedIntro(): Boolean {
        return sp.getBoolean("introCompleted", false)
    }

    private fun completeIntro() {
        val editor = sp.edit()
        editor.putBoolean("introCompleted", true)
        editor.apply()
        startMainApp()
    }

    private fun startMainApp() {
        val content = findViewById<View>(R.id.main_content)
        content.visibility = VISIBLE
    }
}
