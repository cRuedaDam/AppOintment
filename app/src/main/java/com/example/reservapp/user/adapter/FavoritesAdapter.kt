package com.example.reservapp.user.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.reservapp.R
import com.example.reservapp.user.model.CommerceItem

class FavoritesAdapter(private var listCommerce: ArrayList<CommerceItem>, private val onClickItem:(String) -> Unit) : RecyclerView.Adapter<FavoritesAdapter.FavoritesListViewHolder>(){
    private lateinit var commerceNameFav: TextView
    private lateinit var btnNewAppointment: Button
    inner class FavoritesListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        init {
            commerceNameFav = itemView.findViewById(R.id.commerceNameFav)
            btnNewAppointment = itemView.findViewById(R.id.imageButtonDetails)
        }


        fun bind(item: CommerceItem, onClickItem: (String) -> Unit) {
            commerceNameFav.text =item.stringSelected
            btnNewAppointment.setOnClickListener { onClickItem(item.stringSelected) }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesListViewHolder {
        return FavoritesListViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.usr_row_favorites, parent, false))
    }

    override fun getItemCount(): Int {
        return listCommerce.size
    }

    override fun onBindViewHolder(holder: FavoritesListViewHolder, position: Int) {
        val currentItem = listCommerce[position]
        holder.bind(currentItem, onClickItem)
    }

    fun setFavoritesList(favoritesList: ArrayList<CommerceItem>) {
        this.listCommerce = favoritesList
        notifyDataSetChanged()
    }
}
