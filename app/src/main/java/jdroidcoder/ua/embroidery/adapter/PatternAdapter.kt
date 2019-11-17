package jdroidcoder.ua.embroidery.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.realm.Realm
import jdroidcoder.ua.embroidery.Pattern
import jdroidcoder.ua.embroidery.R
import jdroidcoder.ua.embroidery.helper.Util
import kotlinx.android.synthetic.main.pattern_item_style.view.*

class PatternAdapter(val listener: OnItemClick) :
        RecyclerView.Adapter<PatternAdapter.ViewHolder>() {
    private var context: Context? = null
    private var realm = Realm.getDefaultInstance()
    private var patterns = realm.where(Pattern::class.java).findAll()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.pattern_item_style, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = this?.patterns?.get(position)

        //TODO: get image from database
        holder.preview?.setImageBitmap(item?.preview?.let { Util.stringToBitMap(it) })
        holder.item.setOnClickListener {
            item?.let { it1 -> listener?.onItemClick(item) }
        }
        holder.item?.setOnLongClickListener {
            val dialog = context?.let { it1 -> BottomSheetDialog(it1, R.style.CustomBottomSheetDialogTheme) }
            val view = LayoutInflater.from(context).inflate(R.layout.delete_bottom_sheet, null)
            val bntDelete = view.findViewById<TextView>(R.id.bntDelete)
            val closeBottomSheetDelete = view.findViewById<TextView>(R.id.closeBottomSheetDelete)
            bntDelete.setOnClickListener {
                realm?.beginTransaction()
                realm.where(Pattern::class.java).equalTo("id", item?.id)?.findFirst()?.deleteFromRealm()
                realm.commitTransaction()
                dialog?.dismiss()
                notifyDataSetChanged()
            }
            closeBottomSheetDelete.setOnClickListener {
                dialog?.dismiss()
            }
            dialog?.setCancelable(false)
            dialog?.setContentView(view)
            dialog?.show()
            return@setOnLongClickListener true
        }
    }

    override fun getItemCount() = patterns.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var preview = itemView.image
        var item = itemView
    }
}

interface OnItemClick {
    fun onItemClick(pattern: Pattern)
}