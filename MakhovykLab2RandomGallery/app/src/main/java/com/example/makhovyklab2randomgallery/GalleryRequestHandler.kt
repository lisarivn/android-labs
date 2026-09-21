package com.example.makhovyklab2randomgallery

import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestHandler
import okio.Okio
import ua.cn.stu.randomgallery.GalleryListener
import ua.cn.stu.randomgallery.LocalPhoto
import java.io.FileNotFoundException
import java.io.IOException
import java.util.TreeMap

class GalleryRequestHandler :
    RequestHandler(),
    GalleryListener {

    private val photos =
        TreeMap<String, LocalPhoto>()

    override fun canHandleRequest(data: Request): Boolean {
        return SCHEME == data.uri?.scheme
    }

    @Throws(IOException::class)
    @Synchronized
    override fun load(
        request: Request,
        networkPolicy: Int
    ): Result {

        val localId = request.uri?.host

        val localPhoto = photos[localId]
            ?: throw FileNotFoundException(
                "Unknown id: $localId"
            )

        val source =
            Okio.source(localPhoto.openImage())

        return Result(
            source,
            Picasso.LoadedFrom.DISK
        )
    }

    @Synchronized
    override fun onGotGalleryPhotos(
        photos: MutableList<LocalPhoto>
    ) {
        this.photos.clear()

        for (localPhoto in photos) {
            this.photos[localPhoto.localId] =
                localPhoto
        }
    }

    companion object {

        private const val SCHEME = "gallery"

        fun localPhotoToUrl(
            localPhoto: LocalPhoto
        ): String {
            return "$SCHEME://${localPhoto.localId}"
        }
    }
}