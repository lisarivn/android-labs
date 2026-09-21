package com.example.makhovyklab1var16

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.makhovyklab1var16.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applySystemBarInsets()

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragmentContainer,
                    SettingsFragment()
                )
                .commit()
        }
    }

    private fun applySystemBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) {
                view, windowInsets ->

            val insets = windowInsets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                insets.left,
                insets.top,
                insets.right,
                insets.bottom
            )

            windowInsets
        }
    }
}