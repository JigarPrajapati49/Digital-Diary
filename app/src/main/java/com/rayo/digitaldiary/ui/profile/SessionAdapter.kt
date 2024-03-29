package com.rayo.digitaldiary.ui.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.util.Util
import com.rayo.digitaldiary.databinding.ItemLoginDeviceBinding
import com.rayo.digitaldiary.helper.Utils

class SessionAdapter(
    private val sessionList: MutableList<SessionData>,
    private var presenter: LogoutPresenter
) : RecyclerView.Adapter<SessionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionAdapter.ViewHolder {
        val binding =
            ItemLoginDeviceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SessionAdapter.ViewHolder, position: Int) {
        holder.bind(sessionList[position])
    }

    override fun getItemCount(): Int {
        return sessionList.size
    }

    inner class ViewHolder(private var binding: ItemLoginDeviceBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SessionData) {
            with(binding) {
                sessionData = item
                logoutPresenter = presenter
            }
        }
    }
}