package com.mikel.filmaffinity.intro

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.mikel.filmaffinity.R
import com.mikel.filmaffinity.utils.px
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_onboarding_step_how_it_works.*

val instructions = listOf(
    R.drawable.netflix_how_to,
    R.drawable.prime_how_to,
    R.drawable.filmin_how_to
)

class OnboardingStepHowItWorks : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_onboarding_step_how_it_works, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpTabLayout(view)
        loadInstructions(0)
    }

    private fun loadInstructions(index: Int) {
        val imageView = requireView().findViewById<ImageView>(R.id.instructions_image)
        Glide.with(requireContext()).load(instructions[index]).into(imageView)
    }

    private fun setUpTabLayout(view: View) {
        val tabLayout = view.findViewById<TabLayout>(R.id.shareTabLayout)

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                loadInstructions(tab.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }

        })
        val tabs = listOf(
            R.drawable.netflix,
            R.drawable.prime,
            R.drawable.filmin
        )

        for(i in 0 until tabLayout.tabCount) {
            val tab: TabLayout.Tab = tabLayout.getTabAt(i)!!
            val imageView = AppCompatImageView(requireContext())
            val layoutParams = LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 30.px)
            layoutParams.gravity = Gravity.TOP
            imageView.layoutParams = layoutParams
            imageView.setImageResource(tabs[i])
            tab.customView = imageView
        }
    }
}