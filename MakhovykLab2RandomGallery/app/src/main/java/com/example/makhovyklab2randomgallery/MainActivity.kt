package com.example.makhovyklab2randomgallery

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.makhovyklab2randomgallery.databinding.ActivityMainBinding

class MainActivity :
    AppCompatActivity(),
    SyncState.Listener {

    private lateinit var syncState: SyncState

    private lateinit var binding: ActivityMainBinding

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val app = applicationContext as App

        syncState = app.syncState

        binding.actionTextView.setOnClickListener {
            SyncService.scheduleUpdate(this)
        }

        requestNotificationPermission()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->

            val systemBars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars()
            )

            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.fragmentContainer,
                    GalleryFragment()
                )
                .commit()
        }
    }

    override fun onStart() {
        super.onStart()

        syncState.addListener(this)
    }

    override fun onStop() {
        syncState.removeListener(this)

        super.onStop()
    }

    override fun onSyncStateChanged(
        syncState: SyncState
    ) {
        when {
            syncState.isInProgress() -> {
                binding.messageContainer.visibility =
                    View.VISIBLE

                binding.messageTextView.text =
                    getString(R.string.updating_gallery)

                binding.progressTextView.visibility =
                    View.VISIBLE

                binding.progressTextView.text =
                    "${syncState.getProgressPercentage()}%"

                binding.actionTextView.visibility =
                    View.GONE
            }

            syncState.isScheduled() -> {
                binding.messageContainer.visibility =
                    View.VISIBLE

                binding.messageTextView.text =
                    getString(R.string.update_scheduled)

                binding.progressTextView.visibility =
                    View.GONE

                binding.actionTextView.visibility =
                    View.GONE
            }

            syncState.hasUpdates() -> {
                binding.messageContainer.visibility =
                    View.VISIBLE

                binding.messageTextView.text =
                    getString(R.string.update_available)

                binding.progressTextView.visibility =
                    View.GONE

                binding.actionTextView.visibility =
                    View.VISIBLE
            }

            else -> {
                binding.messageContainer.visibility =
                    View.GONE
            }
        }
    }

    override fun onSyncFinished() {
        Toast.makeText(
            this,
            R.string.gallery_updated,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onSyncFailed() {
        Toast.makeText(
            this,
            R.string.gallery_update_failed,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun requestNotificationPermission() {

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.TIRAMISU
        ) {

            val permission =
                Manifest.permission.POST_NOTIFICATIONS

            if (
                ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(
                    permission
                )
            }
        }
    }
}