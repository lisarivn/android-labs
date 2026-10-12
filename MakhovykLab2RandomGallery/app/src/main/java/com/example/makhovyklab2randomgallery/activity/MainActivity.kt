package com.example.makhovyklab2randomgallery.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.util.Log
import com.example.makhovyklab2randomgallery.task.Result
import com.example.makhovyklab2randomgallery.task.Status
import com.example.makhovyklab2randomgallery.task.TaskListener
import com.example.makhovyklab2randomgallery.task.TaskSubject
import com.example.makhovyklab2randomgallery.task.LocalSyncTask
import com.example.makhovyklab2randomgallery.service.SyncService
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.makhovyklab2randomgallery.App
import com.example.makhovyklab2randomgallery.fragment.GalleryFragment
import com.example.makhovyklab2randomgallery.R
import com.example.makhovyklab2randomgallery.state.SyncState
import com.example.makhovyklab2randomgallery.databinding.ActivityMainBinding

class MainActivity :
    AppCompatActivity(),
    SyncState.Listener {

    private lateinit var syncState: SyncState

    private lateinit var binding: ActivityMainBinding

    private val localSyncSubject = TaskSubject<Int>()

    private val localSyncListener = object : TaskListener<Int> {

        override fun onTaskResult(result: Result<Int>) {
            when (result.status) {

                Status.PENDING -> {
                    Log.d("LocalGallerySync", "PENDING")
                }

                Status.RUNNING -> {
                    Log.d("LocalGallerySync", "RUNNING")
                }

                Status.SUCCESS -> {
                    syncState.setInProgress(false)
                    syncState.setScheduled(false)

                    Log.d(
                        "LocalGallerySync",
                        "SUCCESS: copied ${result.data} photos"
                    )

                    syncState.onSyncFinished()
                }

                Status.ERROR -> {
                    syncState.setInProgress(false)
                    syncState.setScheduled(false)

                    Log.e(
                        "LocalGallerySync",
                        "ERROR",
                        result.error
                    )

                    syncState.onSyncFailed()
                }
            }
        }
    }

    private fun startLocalSync() {

        Log.d("LocalGallerySync", "startLocalSync() CALLED")

        val app = applicationContext as App

        if (syncState.isInProgress()) {
            return
        }

        syncState.setProgressPercentage(0)
        syncState.setInProgress(true)

        val task = LocalSyncTask(
            context = app,
            storage = app.galleryStorage,
            onProgress = { percentage ->

                syncState.setProgressPercentage(percentage)

                Log.d(
                    "LocalGallerySync",
                    "Progress: $percentage%"
                )
            }
        )

        app.taskManager.execute(
            task,
            localSyncSubject
        )
    }

    companion object {
        private const val TAG = "GalleryCheckUpdates"
    }

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
            Log.d("LocalGallerySync", "Update button CLICKED")
            startLocalSync()
        }

        requestNotificationPermission()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->

            val safeInsets = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or
                        WindowInsetsCompat.Type.displayCutout()
            )

            view.setPadding(
                safeInsets.left,
                safeInsets.top,
                safeInsets.right,
                safeInsets.bottom
            )

            insets
        }

        ViewCompat.requestApplyInsets(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.fragmentContainer,
                    GalleryFragment()
                )
                .commit()
        }
        SyncService.scheduleUpdate(this)
    }

    override fun onStart() {
        super.onStart()

        syncState.addListener(this)
        localSyncSubject.addListener(localSyncListener)

    }

    override fun onStop() {
        localSyncSubject.removeListener(localSyncListener)
        syncState.removeListener(this)

        super.onStop()
    }

    override fun onSyncStateChanged(
        syncState: SyncState
    ) {

        Log.d(
            "LocalGallerySync",
            "UI state: " +
                    "scheduled=${syncState.isScheduled()}, " +
                    "inProgress=${syncState.isInProgress()}, " +
                    "hasUpdates=${syncState.hasUpdates()}"
        )

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
                binding.messageContainer.visibility = View.VISIBLE

                binding.messageTextView.text = "Локальна галерея готова"

                binding.progressTextView.visibility = View.GONE

                binding.actionTextView.visibility = View.VISIBLE
                binding.actionTextView.text = "Оновити галерею"
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