package com.example.makhovyklab2randomgallery

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import ua.cn.stu.randomgallery.GalleryListener
import ua.cn.stu.randomgallery.LocalPhoto
import ua.cn.stu.randomgallery.RandomGalleryClient
import com.example.makhovyklab2randomgallery.databinding.FragmentGalleryBinding

class GalleryFragment :
    Fragment(),
    GalleryListener,
    SyncState.Listener {
    private var _binding: FragmentGalleryBinding? = null

    private val binding
        get() = _binding!!

    private lateinit var galleryClient: RandomGalleryClient
    private lateinit var adapter: GalleryAdapter
    private lateinit var syncState: SyncState

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val app =
            context.applicationContext as App

        syncState =
            app.syncState

        galleryClient =
            app.galleryClient
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentGalleryBinding.inflate(
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

        val isPortrait =
            resources.configuration.orientation ==
                    Configuration.ORIENTATION_PORTRAIT

        val countPerRow =
            if (isPortrait) 2 else 3

        adapter =
            GalleryAdapter { imageView, photo ->
                openDetails(
                    imageView,
                    photo
                )
            }

        binding.galleryRecyclerView.layoutManager =
            GridLayoutManager(
                requireContext(),
                countPerRow
            )

        binding.galleryRecyclerView.adapter =
            adapter

        galleryClient.addListener(this)

        syncState.addListener(this)

        //if (!SyncService.isScheduled(requireContext())) {
        //    SyncService.scheduleUpdate(requireContext())
       // }
    }

    private fun openDetails(
        imageView: View,
        photo: LocalPhoto
    ) {
        val detailsFragment =
            DetailsFragment.newInstance(
                photo.localId,
                imageView.transitionName
            )

        parentFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .addSharedElement(
                imageView,
                imageView.transitionName
            )
            .replace(
                R.id.fragmentContainer,
                detailsFragment
            )
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        galleryClient.removeListener(this)
        syncState.removeListener(this)

        super.onDestroyView()

        _binding = null
    }

    override fun onGotGalleryPhotos(
        photos: MutableList<LocalPhoto>
    ) {
        Handler(Looper.getMainLooper()).post {

            adapter.submitList(photos)

            if (photos.isEmpty()) {

                binding.emptyTextView.visibility =
                    View.VISIBLE

                binding.galleryRecyclerView.visibility =
                    View.GONE

            } else {

                binding.emptyTextView.visibility =
                    View.GONE

                binding.galleryRecyclerView.visibility =
                    View.VISIBLE
            }
        }
    }

    override fun onSyncStateChanged(
        syncState: SyncState
    ) {
        activity?.runOnUiThread {

            if (syncState.isInProgress()) {

                val progress =
                    syncState.getProgressPercentage()

                binding.syncProgressBar.visibility =
                    View.VISIBLE

                binding.syncProgressTextView.visibility =
                    View.VISIBLE

                binding.syncProgressBar.progress =
                    progress

                binding.syncProgressTextView.text =
                    getString(
                        R.string.synchronization_progress,
                        progress
                    )

            } else {

                binding.syncProgressBar.visibility =
                    View.GONE

                binding.syncProgressTextView.visibility =
                    View.GONE
            }
        }
    }

    override fun onSyncFinished() {
        Handler(Looper.getMainLooper()).post {
            binding.emptyTextView.text =
                getString(
                    R.string.synchronization_completed
                )
        }
    }

    override fun onSyncFailed() {
        Handler(Looper.getMainLooper()).post {
            binding.emptyTextView.text =
                getString(
                    R.string.synchronization_failed
                )
        }
    }
}