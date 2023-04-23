package com.example.nerdle

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nerdle.databinding.GridNumberBinding
import com.google.android.material.button.MaterialButton

class GameAdapter(private val clickListener: GridListener) :
    ListAdapter<Grid, GameAdapter.GameViewHolder>(DiffCallback) {

    private var selectedViewHolder: GameViewHolder? = null

    class GameViewHolder(private var binding: GridNumberBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: GridListener, grid: Grid) {
            binding.numberMaterialButton.text = grid.text
            binding.numberMaterialButton.isChecked = grid.isSelected
            binding.numberMaterialButton.isEnabled = grid.selectable
            binding.numberMaterialButton.strokeColor =
                ColorStateList.valueOf(Color.parseColor("#E0E000"))
            binding.numberMaterialButton.setOnClickListener {
                clickListener.onClick(grid)
//                if ((it as MaterialButton).isChecked)
//                    binding.numberMaterialButton.strokeWidth = 5
//                else
//                    binding.numberMaterialButton.strokeWidth = 0
            }
            if (grid.isSelected)
                binding.numberMaterialButton.strokeWidth = 5
            else
                binding.numberMaterialButton.strokeWidth = 0
            if (grid.color != null)
                binding.numberMaterialButton.setBackgroundColor(grid.color!!)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameAdapter.GameViewHolder {
        val viewHolder =
            GameAdapter.GameViewHolder(GridNumberBinding.inflate(LayoutInflater.from(parent.context)))
        return viewHolder
    }

    override fun onBindViewHolder(holder: GameAdapter.GameViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(clickListener, item)

        // set buttons in first row checkable
//        val column = items.size / 6
//        if (position >= column) {
//            holder.button.isEnabled = false
//        } else {
//            holder.button.isCheckable = true
//        }
//        holder.button.text = item.text
//        holder.button.isChecked = false
//
//        holder.button.setOnClickListener {
//            if (lastSelectedButton.size == 0) {
//                lastSelectedButton.add(holder.adapterPosition)
//                lastSelectedButton.add(holder.button)
//            } else {
//                if (lastSelectedButton[0] == holder.adapterPosition) {
//                    lastSelectedButton.clear()
//                } else {
//                    (lastSelectedButton[1] as MaterialButton).isChecked = false
//                    lastSelectedButton.clear()
//                    lastSelectedButton.add(holder.adapterPosition)
//                    lastSelectedButton.add(holder.button)
//                }
//            }
//
//        }
//        holder.button.addOnCheckedChangeListener { button, isChecked ->
//            if (isChecked) {
//                Toast.makeText(
//                    button.context,
//                    "Button ${holder.adapterPosition} is checked",
//                    Toast.LENGTH_SHORT
//                ).show()
//                button.strokeWidth = 5
//                button.strokeColor = ColorStateList.valueOf(Color.RED)
//            } else {
//                button.strokeWidth = 0
//            }
//        }
//
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Grid>() {
        override fun areItemsTheSame(oldItem: Grid, newItem: Grid): Boolean {
            return oldItem.index == newItem.index
        }

        override fun areContentsTheSame(oldItem: Grid, newItem: Grid): Boolean {
            return oldItem == newItem
        }

    }
}

class GridListener(val clickListener: (Grid) -> Unit) {
    fun onClick(grid: Grid) = clickListener(grid)
}
