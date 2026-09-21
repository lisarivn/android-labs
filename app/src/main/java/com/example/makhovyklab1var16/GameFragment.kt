package com.example.makhovyklab1var16

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.makhovyklab1var16.databinding.FragmentGameBinding
import kotlin.random.Random

class GameFragment : Fragment() {

    private var _binding: FragmentGameBinding? = null

    private val binding
        get() = _binding!!

    private var gridSize = 3
    private var gameTime = 15
    private var speedOption = 0

    private var score = 0
    private var activeCellIndex = -1

    private val cells = mutableListOf<TextView>()

    private val handler =
        Handler(Looper.getMainLooper())

    private var gameRunning = false
    private var gameFinished = false

    private var startTimer: CountDownTimer? = null
    private var gameTimer: CountDownTimer? = null

    private val moveCellRunnable = object : Runnable {

        override fun run() {

            if (!gameRunning || gameFinished) {
                return
            }

            moveActiveCell()

            handler.postDelayed(
                this,
                getRandomDelay()
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gridSize = arguments?.getInt(
            ARG_GRID_SIZE,
            3
        ) ?: 3

        gameTime = arguments?.getInt(
            ARG_GAME_TIME,
            15
        ) ?: 15

        speedOption = arguments?.getInt(
            ARG_SPEED_OPTION,
            0
        ) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentGameBinding.inflate(
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
        super.onViewCreated(
            view,
            savedInstanceState
        )

        binding.timeTextView.text =
            gameTime.toString()

        binding.scoreTextView.text =
            score.toString()

        binding.finishButton.setOnClickListener {
            finishGame()
        }

        startCountdown()
    }

    private fun startCountdown() {

        binding.countdownTextView.visibility =
            View.VISIBLE

        startTimer = object : CountDownTimer(
            3000,
            1000
        ) {

            override fun onTick(
                millisUntilFinished: Long
            ) {

                val seconds =
                    kotlin.math.ceil(
                        millisUntilFinished / 1000.0
                    ).toInt()

                binding.countdownTextView.text =
                    seconds.toString()
            }

            override fun onFinish() {

                if (gameFinished) {
                    return
                }

                binding.countdownTextView.visibility =
                    View.GONE

                startGame()
            }
        }

        startTimer?.start()
    }

    private fun startGame() {

        createGrid()

        gameRunning = true

        moveActiveCell()

        handler.postDelayed(
            moveCellRunnable,
            getRandomDelay()
        )

        startGameTimer()
    }

    private fun startGameTimer() {

        gameTimer = object : CountDownTimer(
            gameTime * 1000L,
            1000
        ) {

            override fun onTick(
                millisUntilFinished: Long
            ) {

                val secondsLeft =
                    kotlin.math.ceil(
                        millisUntilFinished / 1000.0
                    ).toInt()

                binding.timeTextView.text =
                    secondsLeft.toString()
            }

            override fun onFinish() {

                binding.timeTextView.text = "0"

                finishGame()
            }
        }

        gameTimer?.start()
    }

    private fun createGrid() {

        binding.gameTable.removeAllViews()

        cells.clear()

        activeCellIndex = -1

        val cellSizeDp = when (gridSize) {
            5 -> 50
            4 -> 60
            else -> 75
        }

        val cellSizePx =
            dpToPx(cellSizeDp)

        val margin =
            dpToPx(4)

        for (row in 0 until gridSize) {

            val tableRow =
                TableRow(requireContext())

            tableRow.gravity =
                Gravity.CENTER

            for (column in 0 until gridSize) {

                val cell =
                    TextView(requireContext())

                val params =
                    TableRow.LayoutParams(
                        cellSizePx,
                        cellSizePx
                    )

                params.setMargins(
                    margin,
                    margin,
                    margin,
                    margin
                )

                cell.layoutParams = params

                cell.setBackgroundColor(
                    Color.LTGRAY
                )

                val index = cells.size

                cell.setOnClickListener {
                    handleCellClick(index)
                }

                cells.add(cell)

                tableRow.addView(cell)
            }

            binding.gameTable.addView(tableRow)
        }
    }

    private fun moveActiveCell() {

        if (cells.isEmpty()) {
            return
        }

        if (
            activeCellIndex >= 0 &&
            activeCellIndex < cells.size
        ) {

            cells[activeCellIndex]
                .setBackgroundColor(
                    Color.LTGRAY
                )
        }

        var newIndex: Int

        do {
            newIndex =
                Random.nextInt(cells.size)

        } while (
            cells.size > 1 &&
            newIndex == activeCellIndex
        )

        activeCellIndex = newIndex

        cells[activeCellIndex]
            .setBackgroundColor(
                Color.rgb(
                    0,
                    180,
                    120
                )
            )
    }

    private fun handleCellClick(
        index: Int
    ) {

        if (!gameRunning || gameFinished) {
            return
        }

        if (index == activeCellIndex) {

            score += 2

            moveActiveCell()

        } else {

            score -= 1
        }

        binding.scoreTextView.text =
            score.toString()
    }

    private fun getRandomDelay(): Long {

        val range = when (speedOption) {

            1 -> 700..1000

            2 -> 500..700

            3 -> 300..600

            else -> 1000..1500
        }

        return Random
            .nextInt(
                range.first,
                range.last + 1
            )
            .toLong()
    }

    private fun finishGame() {

        if (gameFinished) {
            return
        }

        gameFinished = true
        gameRunning = false

        stopGameTasks()

        binding.countdownTextView.visibility =
            View.GONE

        showResultDialog()
    }

    private fun showResultDialog() {

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.game_over)
            .setMessage(
                getString(
                    R.string.your_score,
                    score
                )
            )
            .setCancelable(false)
            .setPositiveButton(
                R.string.ok
            ) { _, _ ->

                parentFragmentManager
                    .popBackStack()
            }
            .show()
    }

    private fun stopGameTasks() {

        handler.removeCallbacks(
            moveCellRunnable
        )

        startTimer?.cancel()
        gameTimer?.cancel()
    }

    override fun onDestroyView() {

        gameRunning = false

        stopGameTasks()

        cells.clear()

        _binding = null

        super.onDestroyView()
    }

    private fun dpToPx(dp: Int): Int {

        val density =
            resources.displayMetrics.density

        return (dp * density).toInt()
    }

    companion object {

        private const val ARG_GRID_SIZE =
            "GRID_SIZE"

        private const val ARG_GAME_TIME =
            "GAME_TIME"

        private const val ARG_SPEED_OPTION =
            "SPEED_OPTION"

        fun newInstance(
            gridSize: Int,
            gameTime: Int,
            speedOption: Int
        ): GameFragment {

            return GameFragment().apply {

                arguments = Bundle().apply {

                    putInt(
                        ARG_GRID_SIZE,
                        gridSize
                    )

                    putInt(
                        ARG_GAME_TIME,
                        gameTime
                    )

                    putInt(
                        ARG_SPEED_OPTION,
                        speedOption
                    )
                }
            }
        }
    }
}