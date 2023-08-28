package com.example.currency.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.currency.data.model.pojo.HistoricalRates
import com.example.currency.databinding.ItemHistoricalRatesBinding

class HistoricalRatesAdapter(private val clickListener: OnClickListener) :
    ListAdapter<HistoricalRates, HistoricalRatesAdapter.HistoricalRatesViewHolder>(
        HistoricalRatesDiffCallback()
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricalRatesViewHolder {
        return HistoricalRatesViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: HistoricalRatesViewHolder, position: Int) {
        holder.bind(clickListener, getItem(position))
    }

    class HistoricalRatesViewHolder private constructor(private val binding: ItemHistoricalRatesBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: OnClickListener, historicalRates: HistoricalRates) {
            binding.root.setOnClickListener {
                listener.onClick(historicalRates)
            }

            binding.historicalRates = historicalRates
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): HistoricalRatesViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemHistoricalRatesBinding.inflate(layoutInflater, parent, false)

                return HistoricalRatesViewHolder(binding)
            }
        }
    }

    class OnClickListener(
        val measurementClickListener: (historicalRates: HistoricalRates) -> Unit,
    ) {
        fun onClick(historicalRates: HistoricalRates) = measurementClickListener(historicalRates)
    }
}

class HistoricalRatesDiffCallback : DiffUtil.ItemCallback<HistoricalRates>() {
    override fun areItemsTheSame(
        oldItem: HistoricalRates,
        newItem: HistoricalRates
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: HistoricalRates,
        newItem: HistoricalRates
    ): Boolean {
        return oldItem == newItem
    }
}