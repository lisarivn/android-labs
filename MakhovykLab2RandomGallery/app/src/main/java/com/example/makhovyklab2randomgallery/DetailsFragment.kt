package com.example.makhovyklab2randomgallery

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ua.cn.stu.randomgallery.GalleryListener
import ua.cn.stu.randomgallery.LocalPhoto
import ua.cn.stu.randomgallery.RandomGalleryClient
import com.google.android.material.transition.MaterialContainerTransform
import com.example.makhovyklab2randomgallery.databinding.FragmentDetailsBinding

class DetailsFragment :
    Fragment(),
    GalleryListener {
    private var _binding: FragmentDetailsBinding? = null

    private val binding
        get() = _binding!!

    private lateinit var galleryClient: RandomGalleryClient

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val app = context.applicationContext as App
        galleryClient = app.galleryClient
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        sharedElementEnterTransition =
            MaterialContainerTransform().apply {
                duration = 300
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding =
            FragmentDetailsBinding.inflate(
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

        binding.photoImageView.transitionName =
            arguments?.getString(
                ARG_TRANSITION_NAME
            )

        galleryClient.addListener(this)
    }

    override fun onDestroyView() {
        galleryClient.removeListener(this)

        super.onDestroyView()

        _binding = null
    }

    override fun onGotGalleryPhotos(
        photos: MutableList<LocalPhoto>
    ) {
        if (!isAdded) {
            return
        }

        val photoId =
            arguments?.getString(ARG_LOCAL_PHOTO_ID)
                ?: return

        val photo =
            photos.find {
                it.localId == photoId
            }

        if (photo != null) {
            showPhoto(photo)
        }
    }

    private fun showPhoto(photo: LocalPhoto) {
        activity?.runOnUiThread {

            binding.titleTextView.text = photo.name

            val app =
                requireContext()
                    .applicationContext as App

            app.picasso
                .load(
                    GalleryRequestHandler.localPhotoToUrl(
                        photo
                    )
                )
                .into(binding.photoImageView)
        }
    }

    companion object {

        private const val ARG_LOCAL_PHOTO_ID =
            "local_photo_id"

        private const val ARG_TRANSITION_NAME =
            "transition_name"

        fun newInstance(
            localPhotoId: String,
            transitionName: String
        ): DetailsFragment {

            return DetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(
                        ARG_LOCAL_PHOTO_ID,
                        localPhotoId
                    )

                    putString(
                        ARG_TRANSITION_NAME,
                        transitionName
                    )
                }
            }
        }
    }
}