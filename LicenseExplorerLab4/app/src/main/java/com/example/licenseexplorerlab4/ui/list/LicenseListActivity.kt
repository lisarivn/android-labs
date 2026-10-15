package com.example.licenseexplorerlab4.ui.list

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.core.view.ViewCompat
import android.content.Intent
import com.example.licenseexplorerlab4.ui.detail.LicenseDetailActivity
import androidx.core.view.WindowInsetsCompat
import com.example.licenseexplorerlab4.LicenseApplication
import com.example.licenseexplorerlab4.R
import com.example.licenseexplorerlab4.util.UiState
import kotlinx.coroutines.launch

class LicenseListActivity : AppCompatActivity() {

    private val viewModel: LicenseListViewModel by viewModels {
        LicenseListViewModelFactory(
            (application as LicenseApplication).repository
        )
    }

    private lateinit var adapter: LicenseAdapter

    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license_list)
        val rootView = findViewById<View>(android.R.id.content)

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
        progressBar = findViewById(R.id.progressBar)
        tvStatus = findViewById(R.id.tvStatus)

        val recyclerView = findViewById<RecyclerView>(R.id.rvLicenses)
        val btnRefresh = findViewById<Button>(R.id.btnRefresh)

        adapter = LicenseAdapter { license ->
            val intent = Intent(this, LicenseDetailActivity::class.java).apply {
                putExtra(LicenseDetailActivity.EXTRA_LICENSE_KEY, license.key)
            }

            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnRefresh.setOnClickListener {
            viewModel.refreshLicenses()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.uiState.collect { state ->
                        renderState(state)
                    }
                }

                launch {
                    viewModel.isRefreshing.collect { isRefreshing ->
                        btnRefresh.isEnabled = !isRefreshing

                        if (isRefreshing) {
                            btnRefresh.text = "Оновлення..."
                        } else {
                            btnRefresh.text = "Оновити список"
                        }
                    }
                }
            }
        }
    }

    private fun renderState(state: UiState<List<com.example.licenseexplorerlab4.data.local.LicenseEntity>>) {
        when (state) {
            is UiState.Loading -> {
                progressBar.visibility = View.VISIBLE
                tvStatus.visibility = View.GONE
            }

            is UiState.Success -> {
                progressBar.visibility = View.GONE

                adapter.submitList(state.data)

                if (state.isOffline) {
                    tvStatus.text = "Немає підключення. Показано збережені дані."
                    tvStatus.visibility = View.VISIBLE
                } else {
                    tvStatus.visibility = View.GONE
                }
            }

            is UiState.Error -> {
                progressBar.visibility = View.GONE
                tvStatus.text = state.message
                tvStatus.visibility = View.VISIBLE
            }
        }
    }
}