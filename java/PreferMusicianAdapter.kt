package com.chocomusic.chocomusicandroid.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import coil.transform.CircleCropTransformation
import com.chocomusic.chocomusicandroid.R
import com.chocomusic.chocomusicandroid.ui.home.HomeViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.item_musician.view.*

class PreferMusicianAdapter(
    private val viewModel: HomeViewModel
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_musician, parent, false)
        )

    override fun getItemCount(): Int = viewModel.getPreferMusicianListSize()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = viewModel.getPreferMusicianListItem(position)

        var positions = ""
        for (str in item.positionList) { positions += "${str.name} " }
        val region = item.region[0].city.name + " " + item.region[0].name

        holder.itemView.apply {
            if (item.bestFeed != null) {
                item_home_musician_thumb.load(item.bestFeed.thumbnail.imgUrl)
                btn_item_home_musician_play.visibility = View.VISIBLE
            } else {
                item_home_musician_thumb.load(item.profileImg.imgUrl)
                btn_item_home_musician_play.visibility = View.GONE
            }
            item_home_musician_profile_img.load(item.profileImg.imgUrl){
                transformations(CircleCropTransformation())
            }
            item_home_musician_profile_name.text = item.name
            item_home_musician_profile_position.text = positions
            item_home_musician_profile_location.text = region

            /* Play Button */
            btn_item_home_musician_play.setOnClickListener {
                Snackbar.make(it, "플레이", Snackbar.LENGTH_SHORT).show()
            }
            setOnClickListener {
                findNavController().navigate(R.id.action_mainServiceFragment_to_profileVisitFragment)
            }
        }
    }
}