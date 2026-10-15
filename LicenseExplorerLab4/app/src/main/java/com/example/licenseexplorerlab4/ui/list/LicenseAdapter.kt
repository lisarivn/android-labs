package com.example.licenseexplorerlab4.ui.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.licenseexplorerlab4.R
import com.example.licenseexplorerlab4.data.local.LicenseEntity

class LicenseAdapter(
    private val onLicenseClick: (LicenseEntity) -> Unit
) : ListAdapter<LicenseEntity, LicenseAdapter.LicenseViewHolder>(DiffCallback) {

    class LicenseViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val name: TextView = itemView.findViewById(R.id.tvLicenseName)
        val spdxId: TextView = itemView.findViewById(R.id.tvSpdxId)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LicenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_license, parent, false)

        return LicenseViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: LicenseViewHolder,
        position: Int
    ) {
        val license = getItem(position)

        holder.name.text = license.name
        holder.spdxId.text = "SPDX: ${license.spdxId ?: "Uknown"}"

        holder.itemView.setOnClickListener {
            onLicenseClick(license)
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<LicenseEntity>() {

            override fun areItemsTheSame(
                oldItem: LicenseEntity,
                newItem: LicenseEntity
            ): Boolean {
                return oldItem.key == newItem.key
            }

            override fun areContentsTheSame(
                oldItem: LicenseEntity,
                newItem: LicenseEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}