package com.chocomusic.chocomusicandroid.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.chocomusic.chocomusicandroid.R
import com.chocomusic.chocomusicandroid.ui.home.HomeViewModel
import com.chocomusic.chocomusicandroid.ui.main.MainServiceFragmentDirections
import kotlinx.android.synthetic.main.item_home_posting.view.*

class PostingAdapter(
    private val viewModel: HomeViewModel,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_home_posting, parent, false)
        )

    override fun getItemCount(): Int = viewModel.getRecentPostListSize()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = viewModel.getRecentPostListItem(position)

        val date = item.deadLine.split("-").let { "${it[1]}/${it[2]}" }
        val region = item.region.city.name + " " + item.region.name
        val requirementList = item.requirementList.map {
            "${it.position.name} ${it.number}명"
        }
        var requirementStr = ""
        for (str in requirementList) { requirementStr += "$str " }

        holder.itemView.setOnClickListener {
            val action = MainServiceFragmentDirections.actionMainServiceFragmentToPostingSpecFragment(item)
            it.findNavController().navigate(action)
        }

        when(item.category) {
            /* 버스킹 */
            "busking" -> {
                holder.itemView.item_home_posting_card_date.visibility = View.VISIBLE
                holder.itemView.item_home_posting_card_location.visibility = View.GONE
                holder.itemView.item_home_posting_card_date.text = date
                holder.itemView.item_home_posting_category.text = "버스킹"
                holder.itemView.item_home_posting_title.text = item.title
                holder.itemView.item_home_posting_location.text = region
                holder.itemView.item_home_posting_positions.text = requirementStr
            }
            /* 공연 */
            "performance" -> {
                holder.itemView.item_home_posting_card_date.visibility = View.VISIBLE
                holder.itemView.item_home_posting_card_location.visibility = View.GONE
                holder.itemView.item_home_posting_card_date.text = date
                holder.itemView.item_home_posting_category.text = "공연"
                holder.itemView.item_home_posting_title.text = item.title
                holder.itemView.item_home_posting_location.text = region
                holder.itemView.item_home_posting_positions.text = requirementStr
            }
            /* 팀 모집 */
            "recruitment" -> {
                holder.itemView.item_home_posting_card_date.visibility = View.GONE
                holder.itemView.item_home_posting_location.visibility = View.GONE
                holder.itemView.item_home_posting_location_container.visibility = View.GONE
                holder.itemView.item_home_posting_card_location.visibility = View.VISIBLE
                holder.itemView.item_home_posting_card_location.text = item.region.city.name
                holder.itemView.item_home_posting_category.text = "팀 모집"
                holder.itemView.item_home_posting_title.text = item.title
                holder.itemView.item_home_posting_positions.text = requirementStr
            }
        }

    }


}