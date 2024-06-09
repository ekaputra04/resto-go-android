package com.example.restogo

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.recyclerview.widget.RecyclerView
import com.example.restogo.model.ExtraMenu

class RadioButtonAdapter(
    private val options: List<ExtraMenu>,
    private val onOptionSelected: (ExtraMenu) -> Unit
) :
    RecyclerView.Adapter<RadioButtonAdapter.ViewHolder>() {

    private var selectedPosition = -1

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val radioButton: RadioButton = view.findViewById(R.id.item_radio_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_radio_button, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = options[position]
        holder.radioButton.text = "${option.name} (Rp.${option.price})"
        holder.radioButton.isChecked = position == selectedPosition

        holder.radioButton.setOnClickListener {
            val previousPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(previousPosition)
            notifyItemChanged(selectedPosition)
            onOptionSelected(option)
        }
    }

    override fun getItemCount(): Int {
        return options.size
    }
}
