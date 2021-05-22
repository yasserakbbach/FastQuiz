package com.ressay.fastquiz.ui.quiz

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ressay.fastquiz.databinding.QuizItemBinding

class QuizAdapter(private val presenter : Presenter) : ListAdapter<String, QuizAdapter.QuizHolder>(QuizComparator()) {

    class QuizHolder(private val binding: QuizItemBinding, private val presenter : Presenter) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {

        fun bind(option: String) {

            binding.option.text = option
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            presenter.onItemClick(adapterPosition, itemView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuizHolder {

        return QuizHolder(
            QuizItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
            presenter
        )
    }

    override fun onBindViewHolder(holder: QuizHolder, position: Int) {

        getItem(position)?.let {
            holder.bind(it)
        }
    }

    class QuizComparator : DiffUtil.ItemCallback<String>() {

        override fun areItemsTheSame(oldItem: String, newItem: String) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String) =
            oldItem == newItem

    }

    override fun getItemId(position: Int) = position.toLong()

    override fun getItemViewType(position: Int) = position

    interface Presenter {
        fun onItemClick(position: Int, itemView: View)
    }
}