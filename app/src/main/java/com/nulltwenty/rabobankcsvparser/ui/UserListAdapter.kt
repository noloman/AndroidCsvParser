package com.nulltwenty.rabobankcsvparser.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.nulltwenty.rabobankcsvparser.R
import com.nulltwenty.rabobankcsvparser.databinding.UserItemRowBinding
import com.nulltwenty.rabobankcsvparser.ui.model.UserModel

class UserListAdapter : ListAdapter<UserModel, UserListAdapter.UserViewHolder>(UserDiffCallback) {
    inner class UserViewHolder(private val binding: UserItemRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserModel) {
            with(binding) {
                name.text =
                    String.format(binding.root.resources.getString(R.string.name), user.fullName)
                birthdate.text = String.format(
                    binding.root.resources.getString(R.string.date_of_birth), user.birthdate
                )
                issueCount.text = String.format(
                    binding.root.resources.getString(R.string.issue_count), user.issueCount
                )
                avatar.load(user.avatarUrl) {
                    crossfade(true)
                    transformations(CircleCropTransformation())
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): UserListAdapter.UserViewHolder = UserViewHolder(
        UserItemRowBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object UserDiffCallback : DiffUtil.ItemCallback<UserModel>() {
        override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean =
            oldItem.fullName == newItem.fullName

        override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean =
            oldItem == newItem
    }
}