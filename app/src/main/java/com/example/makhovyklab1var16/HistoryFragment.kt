package com.example.makhovyklab1var16

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.makhovyklab1var16.databinding.FragmentHistoryBinding
import android.widget.TextView
import android.content.Intent

class HistoryFragment :
    Fragment(),
    HistoryObserver {

    private var _binding: FragmentHistoryBinding? = null

    private val binding
        get() = _binding!!

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).toInt()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHistoryBinding.inflate(
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

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onHistoryLoaded(
        results: List<GameResult>
    ) {

        if (_binding == null) {
            return
        }

        binding.historyContainer.removeAllViews()

        if (results.isEmpty()) {

            binding.emptyHistoryTextView.visibility =
                View.VISIBLE

            binding.historyScrollView.visibility =
                View.GONE

        } else {

            binding.emptyHistoryTextView.visibility =
                View.GONE

            binding.historyScrollView.visibility =
                View.VISIBLE

            results.reversed().forEachIndexed { index, result ->

                if (index > 0) {

                    val divider = View(requireContext())

                    divider.layoutParams =
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            dpToPx(1)
                        )

                    divider.setBackgroundColor(
                        android.graphics.Color.LTGRAY
                    )

                    binding.historyContainer.addView(divider)
                }

                val resultTextView =
                    TextView(requireContext())

                resultTextView.text = buildString {
                    append("Date: ${result.dateTime}\n")
                    append("Score: ${result.score}\n")
                    append("Grid: ${result.gridSize} × ${result.gridSize}\n")
                    append("Time: ${result.gameTime} sec\n")
                    append(
                        "Speed: ${getSpeedText(result.speedOption)}"
                    )
                }

                resultTextView.textSize = 17f

                resultTextView.setPadding(
                    16,
                    16,
                    16,
                    24
                )

                binding.historyContainer.addView(
                    resultTextView
                )

                val divider = View(requireContext())

                divider.layoutParams =
                    ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1
                    )

                divider.setBackgroundColor(
                    android.graphics.Color.LTGRAY
                )

                binding.historyContainer.addView(divider)
            }
        }
    }

    private fun getSpeedText(
        speedOption: Int
    ): String {

        return when (speedOption) {
            1 -> "0.7–1 sec"
            2 -> "0.5–0.7 sec"
            3 -> "0.3–0.6 sec"
            else -> "1–1.5 sec"
        }
    }

    override fun onStart() {
        super.onStart()

        HistoryObservable.addObserver(this)

        val intent = Intent(
            requireContext(),
            HistoryIntentService::class.java
        )

        requireContext().startService(intent)
    }

    override fun onStop() {

        HistoryObservable.removeObserver(this)

        super.onStop()
    }
}