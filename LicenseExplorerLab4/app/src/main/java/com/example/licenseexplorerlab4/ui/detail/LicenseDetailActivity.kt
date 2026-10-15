
package com.example.licenseexplorerlab4.ui.detail

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.licenseexplorerlab4.LicenseApplication
import com.example.licenseexplorerlab4.R
import com.example.licenseexplorerlab4.data.local.LicenseEntity
import com.example.licenseexplorerlab4.util.UiState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class LicenseDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LICENSE_KEY = "license_key"
    }

    private val licenseKey: String by lazy {
        intent.getStringExtra(EXTRA_LICENSE_KEY).orEmpty()
    }

    private val viewModel: LicenseDetailViewModel by viewModels {
        LicenseDetailViewModelFactory(
            (application as LicenseApplication).repository,
            licenseKey
        )
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView

    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (licenseKey.isBlank()) {
            finish()
            return
        }

        setContentView(R.layout.activity_license_detail)

        setupWindowInsets()

        progressBar = findViewById(R.id.detailProgressBar)
        tvStatus = findViewById(R.id.tvDetailStatus)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderState(state)
                }
            }
        }
    }


    private fun setupWindowInsets() {
        val rootView = findViewById<View>(R.id.detailRoot)

        val initialLeft = rootView.paddingLeft
        val initialTop = rootView.paddingTop
        val initialRight = rootView.paddingRight
        val initialBottom = rootView.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, insets ->
            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout()
            )

            view.setPadding(
                initialLeft + systemBars.left,
                initialTop + systemBars.top,
                initialRight + systemBars.right,
                initialBottom + systemBars.bottom
            )

            insets
        }

        ViewCompat.requestApplyInsets(rootView)
    }


    private fun renderState(state: UiState<LicenseEntity>) {
        when (state) {
            is UiState.Loading -> {
                progressBar.visibility = View.VISIBLE
                tvStatus.visibility = View.GONE
            }

            is UiState.Success -> {
                progressBar.visibility = View.GONE

                displayLicense(state.data)

                if (state.isOffline) {
                    tvStatus.text =
                        "You're offline. Showing saved data."
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

    private fun displayLicense(license: LicenseEntity) {
        findViewById<TextView>(R.id.tvDetailTitle).text = license.name

        findViewById<TextView>(R.id.tvDetailSpdx).text =
            "SPDX: ${license.spdxId ?: "Unknown"}"

        findViewById<TextView>(R.id.tvDescription).text =
            license.description?.takeIf { it.isNotBlank() }
                ?: "Description not available."

        findViewById<TextView>(R.id.tvPermissions).text =
            formatJsonList(license.permissions)

        findViewById<TextView>(R.id.tvConditions).text =
            formatJsonList(license.conditions)

        findViewById<TextView>(R.id.tvLimitations).text =
            formatJsonList(license.limitations)

        findViewById<TextView>(R.id.tvLicenseBody).text =
            license.body?.takeIf { it.isNotBlank() }
                ?: "Full license text has not been loaded yet."
    }

    private fun formatJsonList(json: String?): String {
        if (json.isNullOrBlank()) {
            return "Data not available."
        }

        return try {
            val type = object : TypeToken<List<String>>() {}.type
            val items: List<String>? = gson.fromJson(json, type)

            if (items.isNullOrEmpty()) {
                "Data not available."
            } else {
                items.joinToString("\n") { "• $it" }
            }
        } catch (e: Exception) {
            "Unable to read the data."
        }
    }
}
