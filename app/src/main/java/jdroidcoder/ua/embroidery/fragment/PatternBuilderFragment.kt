package jdroidcoder.ua.embroidery.fragment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import butterknife.OnClick
import com.google.android.material.bottomsheet.BottomSheetDialog
import io.realm.Realm
import io.realm.RealmList
import jdroidcoder.ua.embroidery.Cell
import jdroidcoder.ua.embroidery.CellColor
import jdroidcoder.ua.embroidery.Pattern
import jdroidcoder.ua.embroidery.R
import jdroidcoder.ua.embroidery.adapter.AnchorPaletteAdapter
import jdroidcoder.ua.embroidery.adapter.DMCPaletteAdapter
import jdroidcoder.ua.embroidery.helper.ColorsAnchor
import jdroidcoder.ua.embroidery.helper.ColorsDMC
import jdroidcoder.ua.embroidery.helper.GlobalData
import kotlinx.android.synthetic.main.fragment_pattern_builder.*
import java.util.*
import jdroidcoder.ua.embroidery.adapter.ColorListAdapter
import jdroidcoder.ua.embroidery.helper.Util




class PatternBuilderFragment : BaseFragment() {

    companion object {
        const val TAG = "PatternBuilderFragment"

        fun newInstance(width: Int, height: Int, patternId: Int) = PatternBuilderFragment().apply {
            arguments = Bundle(3).apply {
                putInt(WIDTH, width)
                putInt(HEIGHT, height)
                putInt(PATTERN_ID, patternId)
            }
        }
    }

    private val changes: ArrayList<LastChange> = ArrayList()
    private val cells: ArrayList<ImageView> = ArrayList()
    private var patternId: Int = 0
    private val realm = Realm.getDefaultInstance()
    private var pattern: Pattern? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater?.inflate(R.layout.fragment_pattern_builder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val item = ColorsAnchor.values()?.get(0)
        patternId = arguments?.getInt(PATTERN_ID, 0) ?: 0
        pattern = realm?.where(Pattern::class.java)?.equalTo("id", patternId)?.findFirst()
        GlobalData.currentColor = Color.rgb(item.r, item.g, item.b)
        val layoutManager = GridLayoutManager(context, 5, GridLayoutManager.VERTICAL, false)
        colorsList.layoutManager = layoutManager
        colorsList.adapter = AnchorPaletteAdapter(ColorsAnchor.values())
        drawTable()
    }

    @OnClick(R.id.btnOnTouchListener)
    fun btnOnTouchListener() {
        GlobalData.blockedMode = !GlobalData.blockedMode
        if (GlobalData.blockedMode) {
            zoomLayout?.setZoomEnabled(false)
            zoomLayout?.setOneFingerScrollEnabled(false)
            zoomLayout?.setOverScrollHorizontal(false)
            zoomLayout?.setOverScrollVertical(false)
            zoomLayout?.setScrollEnabled(false)
            btnOnTouchListener.setBackgroundColor(resources.getColor(R.color.yellow))
            cells?.forEach {
                it.isClickable = false
                it.isFocusable = false
            }
            tableLayout?.setOnTouchListener { view, motionEvent ->
                cells?.forEach { view1 ->
                    val offsetViewBounds = Rect()
                    view1.getDrawingRect(offsetViewBounds)
                    tableLayout.offsetDescendantRectToMyCoords(view1, offsetViewBounds)
                    if (true == offsetViewBounds.contains(
                            motionEvent.x?.toInt(),
                            motionEvent.y?.toInt()
                        )
                    ) {
                        view1?.performClick()
                    }
                }
                return@setOnTouchListener true
            }
        } else {
            zoomLayout?.setZoomEnabled(true)
            zoomLayout?.setOneFingerScrollEnabled(true)
            zoomLayout?.setOverScrollHorizontal(true)
            zoomLayout?.setOverScrollVertical(true)
            zoomLayout?.setScrollEnabled(true)
            btnOnTouchListener.setBackgroundColor(resources.getColor(R.color.white))
            tableLayout?.setOnTouchListener(null)
            cells?.forEach {
                it.isClickable = true
                it.isFocusable = true
            }
        }
    }

    @OnClick(R.id.colorPicker)
    fun colorPicker() {
        GlobalData.colorPickerMode = !GlobalData.colorPickerMode
        if (GlobalData.colorPickerMode) {
            colorPicker.setBackgroundColor(resources.getColor(R.color.yellow))
        } else {
            colorPicker.setBackgroundColor(resources.getColor(R.color.white))
        }
    }

    @OnClick(R.id.btnDeleteColor)
    fun btnDeleteColor() {
        GlobalData.clearMode = !GlobalData.clearMode
        if (GlobalData.clearMode) {
            btnDeleteColor.setBackgroundColor(resources.getColor(R.color.yellow))
        } else {
            btnDeleteColor.setBackgroundColor(resources.getColor(R.color.white))
        }
    }

    @OnClick(R.id.cancelColor)
    fun cancelColor() {
        if (false == changes?.isEmpty()) {
            changes?.lastOrNull()?.let { it1 ->
                val x = tableLayout?.findViewWithTag<ImageView>(it1.tag)
                if (it1.color != 0) {
                    it1.color?.let { x?.setBackgroundColor(it) }
                } else {
                    x?.setBackgroundResource(R.drawable.ic_clear_black_24dp)
                }
                changes?.remove(it1)
            }
        }
    }

    private fun drawTable() {
        pattern?.height?.let { height ->
            pattern?.width?.let { width ->
                for (i in 0..height) {
                    val row = TableRow(requireContext())
                    val ll = FrameLayout.LayoutParams(MATCH_PARENT, 60)
                    row.layoutParams = ll
                    for (j in 0..width) {
                        val imageView = context?.let { ImageView(it) }
                        val cell =
                            pattern?.cells?.firstOrNull { p -> p.position[0] == i && p.position[1] == j }
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
                        imageView?.setOnClickListener {
                            if (GlobalData.clearMode) {
                                if (it.background is ColorDrawable) {
                                    changes.add(
                                        LastChange(
                                            it.tag.toString(),
                                            (it.background as ColorDrawable).color
                                        )
                                    )
                                }
                                realm.beginTransaction()
                                cell?.color?.colorName = ""
                                cell?.color?.colorCode = 0
                                realm.commitTransaction()
                                it?.setBackgroundColor(Color.TRANSPARENT)
                                it.setBackgroundResource(R.drawable.ic_clear_black_24dp)
                            } else {
                                if (it.background is ColorDrawable) {
                                    changes.add(
                                        LastChange(
                                            it.tag.toString(),
                                            (it.background as ColorDrawable).color
                                        )
                                    )
                                } else {
                                    changes.add(
                                        LastChange(
                                            it.tag.toString(),
                                            Color.TRANSPARENT
                                        )
                                    )
                                }
                                if (true == GlobalData.colorPickerMode) {
                                    if (it.background is ColorDrawable) {
                                        GlobalData.currentColor =
                                            (it.background as ColorDrawable).color
                                    }
                                    colorPicker()
                                } else {
                                    GlobalData.currentColor
                                        ?.let { it1 ->
                                            it?.setBackgroundColor(it1)
                                        }
                                }
                            }
                            realm.beginTransaction()
                            if (it.background is ColorDrawable) {
                                val backgroundColor = (it.background as ColorDrawable).color
                                val tempColor = ColorsAnchor.values()?.firstOrNull { p ->
                                    Color.rgb(
                                        p.r,
                                        p.g,
                                        p.b
                                    ) == backgroundColor
                                }
                                var cell = realm.where(Cell::class.java)
                                    .equalTo("tag", it.tag?.toString())
                                    .findFirst()
                                if (cell == null) {
                                    val pettern =
                                        realm.where(Pattern::class.java).equalTo("id", patternId)
                                            .findFirst()
//                                    pattern?.deleteFromRealm()

                                    cell = Cell()
                                    val position = RealmList<Int>()
                                    position.add(i)
                                    position.add(j)
                                    pettern?.cells?.add(Cell(cellTag, position, CellColor(null, 0)))
                                }
                                cell?.color?.colorCode = backgroundColor
                                cell?.color?.colorName = tempColor?.toString()
                                realm?.insertOrUpdate(cell)
                            }
                            realm.commitTransaction()
                        }

                        imageView?.let { cells?.add(it) }
                        row.addView(imageView)
                    }
                    tableLayout?.addView(row)
                }
            }
        }
    }

    @OnClick(R.id.choosePalette)
    fun choosePalette() {
        val dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.palette_bottom_sheet, null)
        val openPaletteDMC = view.findViewById<TextView>(R.id.openPaletteDMC)
        val openPaletteAnchor = view.findViewById<TextView>(R.id.openPaletteAnchor)
        val closePaletteBS = view.findViewById<TextView>(R.id.closeBottomSheetPalette)
        openPaletteDMC.setOnClickListener {
            if (colorsList.adapter != null) {
                colorsList.adapter = DMCPaletteAdapter(ColorsDMC.values())
                dialog.dismiss()
            }
        }
        openPaletteAnchor.setOnClickListener {
            if (colorsList.adapter != null) {
                colorsList.adapter = AnchorPaletteAdapter(ColorsAnchor.values())
                dialog.dismiss()
            }
        }
        closePaletteBS.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    @OnClick(R.id.goToNextFragmentDone)
    fun goToNextFragmentDone() {
        pdfAndImage.visibility = View.VISIBLE
        callBack?.visibility = View.VISIBLE
        openBtnSheetDelete?.visibility = View.GONE
        goToNextFragmentDone?.visibility = View.GONE
        panelInstrument?.visibility = View.GONE
        zoomLayout?.apply {
            val bitmap = Bitmap.createBitmap(this?.width, this?.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val bgDrawable = this?.background
            if (bgDrawable != null) {
                bgDrawable.draw(canvas)
            } else {
                canvas.drawColor(Color.WHITE)
            }
            this?.draw(canvas)
            realm.beginTransaction()
            val pattern = realm.where(Pattern::class.java).equalTo("id", patternId).findFirst()
            pattern?.preview = Util.bitmapToString(bitmap)
            realm.commitTransaction()
        }
        val layoutManager = LinearLayoutManager(context)
        colorsList.layoutManager = layoutManager
        colorsList.adapter = ColorListAdapter(patternId)
    }

    @OnClick(R.id.openBtnSheetDelete)
    fun openBtnSheetDelete() {
        val dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.delete_bottom_sheet, null)
        val bntDelete = view.findViewById<TextView>(R.id.bntDelete)
        val closeBottomSheetDelete = view.findViewById<TextView>(R.id.closeBottomSheetDelete)
        bntDelete.setOnClickListener {
            realm?.beginTransaction()
            realm.where(Pattern::class.java).equalTo("id", patternId)?.findFirst()
                ?.deleteFromRealm()
            realm.commitTransaction()
            dialog.dismiss()
            activity?.supportFragmentManager?.popBackStack()
        }
        closeBottomSheetDelete.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    @OnClick(R.id.callBack)
    fun callBack() {
        pdfAndImage.visibility = View.GONE
        callBack?.visibility = View.GONE
        openBtnSheetDelete?.visibility = View.VISIBLE
        goToNextFragmentDone?.visibility = View.VISIBLE
        panelInstrument?.visibility = View.VISIBLE
        val layoutManager = GridLayoutManager(context, 5, GridLayoutManager.VERTICAL, false)
        colorsList.layoutManager = layoutManager
        colorsList.adapter = AnchorPaletteAdapter(ColorsAnchor.values())
    }

    @OnClick(R.id.pdfAndImage)
    fun pdfAndImage() {
        val dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.pdf_image_btn_sheet, null)
        val openPDF = view.findViewById<TextView>(R.id.showListPDF)
        val hideListImage = view.findViewById<TextView>(R.id.hideListImage)
        val closePaletteBS = view.findViewById<TextView>(R.id.closeBottomSheetExport)
        openPDF.setOnClickListener {
            val fragment = PDFFragment.newInstance(patternId)
            fragmentManager
                ?.beginTransaction()
                ?.replace(android.R.id.content, fragment, PDFFragment.TAG)
                ?.addToBackStack(PDFFragment.TAG)
                ?.commit()
            dialog.dismiss()

        }
        hideListImage.setOnClickListener {
            image.setImageBitmap(pattern?.preview.let { it?.let { it1 -> Util.stringToBitMap(it1) } })
            image.visibility = View.VISIBLE
            tableLayout.visibility = View.GONE
            panelInstrument?.visibility = View.GONE
            colorsList.visibility = View.GONE
            zoomLayout.visibility = View.GONE
            callBack.visibility = View.GONE
            done.visibility = View.GONE
            pdfAndImage.visibility = View.GONE
            cancelImage.visibility = View.VISIBLE
            exportImage.visibility = View.VISIBLE
            imageTitle.visibility = View.VISIBLE
//            zoomImageView?.visibility = View.VISIBLE
//            zoomImage.visibility = View.VISIBLE

            dialog.dismiss()
        }
        closePaletteBS.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

    @OnClick(R.id.cancelImage)
    fun cancelImage() {
        tableLayout.visibility = View.VISIBLE
        panelInstrument?.visibility = View.VISIBLE
        colorsList.visibility = View.VISIBLE
        zoomLayout.visibility = View.VISIBLE
        callBack.visibility = View.VISIBLE
        done.visibility = View.VISIBLE
        pdfAndImage.visibility = View.VISIBLE
        cancelImage.visibility = View.GONE
        exportImage.visibility = View.GONE
        imageTitle.visibility = View.GONE
        image.visibility = View.GONE
//        zoomImageView.visibility = View.GONE
//        zoomImage.visibility = View.GONE
    }

}


class LastChange(var tag: String? = null, var color: Int? = null)
