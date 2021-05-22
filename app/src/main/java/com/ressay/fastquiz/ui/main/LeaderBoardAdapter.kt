package com.ressay.fastquiz.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ressay.fastquiz.data.models.User
import com.ressay.fastquiz.databinding.LeaderboardItemBinding

class LeaderBoardAdapter : ListAdapter<User, LeaderBoardAdapter.LeaderBoardHolder>(LeaderBoardComparator()) {

    class LeaderBoardHolder(private val binding : LeaderboardItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user : User) {

            binding.apply {
                username.text = user.username
                score.text = user.score.toString()
            }
        }

    }

    class LeaderBoardComparator : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: User, newItem: User) =
            oldItem.email == newItem.email

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderBoardHolder {

        return LeaderBoardHolder(
            LeaderboardItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: LeaderBoardHolder, position: Int) {

        getItem(position)?.let {
            holder.bind(it)
        }
    }
}