package cn.deepink.booker.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

open class ListAdapter<T>(private val layoutResId: Int, val sameItem: (T, T) -> Boolean = { old, new -> old == new }, val sameContent: (T, T) -> Boolean = { old, new -> old == new }, val onBindViewHolder: (VH, T) -> Unit) : ListAdapter<T, VH>(object : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return sameItem(oldItem, newItem)
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return sameContent(oldItem, newItem)
    }

}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context).inflate(layoutResId, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        onBindViewHolder(holder, getItem(position))
    }

}

class VH(itemView: View) : RecyclerView.ViewHolder(itemView)