package com.cristiangonzalez.assistcontrol.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cristiangonzalez.assistcontrol.R
import com.cristiangonzalez.assistcontrol.databinding.FragmentStatisticItemBinding
import com.cristiangonzalez.assistcontrol.models.Statistic

class StatisticAdapter(private val statistics: List<Statistic>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val binding = FragmentStatisticItemBinding.bind(itemView)

        //Cargar atributos de api
        fun bind(statistic: Statistic) = with(itemView){
            binding.textViewCourse.text = statistic.course
            binding.textViewPercentage.text = statistic.percentage
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(layoutInflater.inflate(R.layout.fragment_statistic_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(statistics[position])
    }

    override fun getItemCount() = statistics.size

}