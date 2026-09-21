package com.example.makhovyklab2randomgallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.makhovyklab2randomgallery.databinding.ItemPhotoBinding
import ua.cn.stu.randomgallery.LocalPhoto

class GalleryAdapter(
    private val onPhotoClick: (View, LocalPhoto) -> Unit
) : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    private var items: List<LocalPhoto> =
        emptyList()

    fun submitList(photos: List<LocalPhoto>) {
        items = photos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val binding =
            ItemPhotoBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ),
                parent,
                false
            )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val photo = items[position]

        holder.binding.nameTextView.text =
            photo.name

        holder.binding.photoImageView.transitionName =
            "photo_$position"

        val app =
            holder.itemView.context
                .applicationContext as App

        app.picasso
            .load(
                GalleryRequestHandler.localPhotoToUrl(
                    photo
                )
            )
            .into(
                holder.binding.photoImageView
            )

        holder.binding.photoImageView
            .setOnClickListener {

                onPhotoClick(
                    holder.binding.photoImageView,
                    photo
                )
            }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(
        val binding: ItemPhotoBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    )
}