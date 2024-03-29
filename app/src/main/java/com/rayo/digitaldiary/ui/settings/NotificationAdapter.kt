package com.rayo.digitaldiary.ui.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rayo.digitaldiary.databinding.ItemNotificationBinding

/**
 * Created by Mittal Varsani on 02/03/23.
 *
 * @author Mittal Varsani
 */
class NotificationAdapter(
    private val notificationList: List<NotificationData>,
    private val presenter: NotificationPresenter
) :
    RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notificationList[position])
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    inner class ViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: NotificationData) {
            with(binding) {
                notificationData = item
                position = absoluteAdapterPosition
                notificationPresenter = this@NotificationAdapter.presenter
                executePendingBindings()
            }
        }
    }
}