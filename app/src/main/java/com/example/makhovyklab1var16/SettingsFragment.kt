package com.example.makhovyklab1var16

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.makhovyklab1var16.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(
            inflater,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.startButton.setOnClickListener {
            startGame()
        }
    }

    private fun startGame() {

        val gridSize = when (
            binding.gridSizeSpinner.selectedItemPosition
        ) {
            1 -> 4
            2 -> 5
            else -> 3
        }

        val gameTime = when (
            binding.timerSpinner.selectedItemPosition
        ) {
            1 -> 30
            2 -> 45
            else -> 15
        }

        val speedOption =
            binding.speedSpinner.selectedItemPosition

        val gameFragment =
            GameFragment.newInstance(
                gridSize,
                gameTime,
                speedOption
            )

        parentFragmentManager
            .beginTransaction()
            .replace(
                R.id.fragmentContainer,
                gameFragment
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}