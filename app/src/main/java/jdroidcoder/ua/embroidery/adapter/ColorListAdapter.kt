package jdroidcoder.ua.embroidery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.realm.Realm
import jdroidcoder.ua.embroidery.Cell
import jdroidcoder.ua.embroidery.Pattern
import jdroidcoder.ua.embroidery.R
import kotlinx.android.synthetic.main.item_name_color.view.*

class ColorListAdapter(val patternId:Int) : RecyclerView.Adapter<ColorListAdapter.ViewHolder>() {
    private var realm = Realm.getDefaultInstance()
    private val cells = realm?.where(Pattern::class.java)?.equalTo("id", patternId)?.findFirst()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_name_color, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = cells?.cells?.filter { p->p.color?.colorCode != 0 }?.get(position)
        holder.colorName.text = item?.color?.colorName
        holder.counter.text = realm.where(Cell::class.java).equalTo("color.colorName", item?.color?.colorName)?.findAll()?.size?.toString()
                ?: "0"
        item?.color?.colorCode?.let { holder.colorInList.setBackgroundColor(it) }
    }

    override fun getItemCount() = cells?.cells?.filter { p->p.color?.colorCode != 0 }?.size?:0

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var colorName = itemView.colorName
        var counter = itemView.counter
        var colorInList = itemView.colorInList
    }
}