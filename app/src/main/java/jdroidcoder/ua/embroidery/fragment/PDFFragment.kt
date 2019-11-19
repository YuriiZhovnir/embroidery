package jdroidcoder.ua.embroidery.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TableRow
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.OnClick
import io.realm.Realm
import io.realm.RealmList
import jdroidcoder.ua.embroidery.Cell
import jdroidcoder.ua.embroidery.CellColor
import jdroidcoder.ua.embroidery.Pattern
import jdroidcoder.ua.embroidery.R
import jdroidcoder.ua.embroidery.adapter.ColorPdfAdapter
import kotlinx.android.synthetic.main.add_pattern_alert_style.*
import kotlinx.android.synthetic.main.fragment_pgf.*
import java.util.*


class PDFFragment : BaseFragment() {
    companion object {
        const val TAG = "PDFFragment"

        fun newInstance(patternId: Int) = PDFFragment().apply {
            arguments = Bundle(3).apply {
                putInt(PATTERN_ID, patternId)
            }
        }
    }
    private var patternId: Int = 0
    private val realm = Realm.getDefaultInstance()
    private var pattern: Pattern? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pgf, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        patternId = arguments?.getInt(PATTERN_ID, 0) ?: 0
        pattern = realm?.where(Pattern::class.java)?.equalTo("id", patternId)?.findFirst()
        drawTable()
        showColors()

    }

    private fun showColors(){
        val layoutManager = LinearLayoutManager(context)
        colorsList.layoutManager = layoutManager
        colorsList.adapter = ColorPdfAdapter(patternId)
    }

    private fun drawTable() {
        pattern?.height?.let { height ->
            pattern?.width?.let { width ->
                for (i in 0..height) {
                    val row = TableRow(requireContext())
                    val ll = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 60)
                    row.layoutParams = ll
                    for (j in 0..width) {
                        val imageView = context?.let { ImageView(it) }
                        val cell = pattern?.cells?.firstOrNull { p -> p.position[0] == i && p.position[1] == j }
                        val cellTag = if (null == cell) {
                            (Random().nextInt() + i * j).toString()
                        } else {
                            cell.tag
                        }
                        imageView?.tag = cellTag
                        if (null == cell || 0 == cell.color?.colorCode) {
                            imageView?.setBackgroundResource(R.drawable.ic_clear_black_24dp)
                        } else {
                            cell.color?.colorCode?.let { imageView?.setBackgroundColor(it) }
                        }
                        imageView?.layoutParams = TableRow.LayoutParams(24, 24)
                        row.addView(imageView)
                    }
                    tableLayout?.addView(row)
                }
            }
        }
    }
    private fun drawTableВivide(){
            for (i in 0..25){
                val row = TableRow(requireContext())
                val ll = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 30)
                row.layoutParams = ll
                for (j in 0..25){
                    val imageView = context?.let { ImageView(it) }
                    imageView?.layoutParams = TableRow.LayoutParams(30, 30)
                    row.addView(imageView)
                }
                tableLayout?.addView(row)
            }

    }
    @OnClick(R.id.cancel)
    fun cancel(){
        activity?.supportFragmentManager?.popBackStack()
    }

}
