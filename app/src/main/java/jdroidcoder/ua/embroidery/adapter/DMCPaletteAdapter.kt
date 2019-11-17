package jdroidcoder.ua.embroidery.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import jdroidcoder.ua.embroidery.R
import jdroidcoder.ua.embroidery.helper.ColorsDMC
import jdroidcoder.ua.embroidery.helper.GlobalData
import kotlinx.android.synthetic.main.color_list_style.view.*

class DMCPaletteAdapter(var сolorsCode: Array<ColorsDMC> = arrayOf()) :
        RecyclerView.Adapter<DMCPaletteAdapter.ViewHolder>() {
    private var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.color_list_style, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = this?.сolorsCode?.get(position)
        val color = Color.rgb(item.r, item.g, item.b)
        holder.itemColor.setBackgroundColor(color)
        if (GlobalData.currentColor == color) {
            context?.resources?.getColor(R.color.yellow)?.let { holder.itemColor.borderColor = it }
            holder.itemColor.borderWidth = context?.resources?.getDimension(R.dimen.border_width_5dp)?:5f
        } else {
            holder.itemColor.borderColor = Color.BLACK
            holder.itemColor.borderWidth = context?.resources?.getDimension(R.dimen.border_width_1dp)?:1f
        }

        holder.itemColor.setOnClickListener {
            GlobalData.currentColor = Color.rgb(item.r, item.g, item.b)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = сolorsCode.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemColor = itemView.colorImage
    }
}