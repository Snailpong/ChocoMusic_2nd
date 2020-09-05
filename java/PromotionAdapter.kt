package com.chocomusic.chocomusicandroid.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.chocomusic.chocomusicandroid.R
import com.chocomusic.chocomusicandroid.ui.home.HomeViewModel
import kotlinx.android.synthetic.main.item_home_promotion.view.*

class PromotionAdapter(
    private val viewModel: HomeViewModel
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_promotion, parent, false)
        )
    }

    override fun getItemCount(): Int = viewModel.getBannerListSize()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = viewModel.getBannerListItem(position)

        holder.itemView.apply {
            banner_img.load(item)
        }
    }

}