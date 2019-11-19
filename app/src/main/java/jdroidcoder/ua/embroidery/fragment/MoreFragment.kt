package jdroidcoder.ua.embroidery.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import butterknife.OnClick
import com.google.android.material.bottomsheet.BottomSheetDialog
import jdroidcoder.ua.embroidery.BuildConfig
import jdroidcoder.ua.embroidery.R
import kotlinx.android.synthetic.main.fragment_my_patterns.*
import kotlinx.android.synthetic.main.more_fragment.*
import kotlinx.android.synthetic.main.privacy_policy_alert.view.*


class MoreFragment : BaseFragment(){
    companion object {
        const val TAG = "MoreFragment"

        fun newInstance() = MoreFragment()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.more_fragment, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        informationPP.setOnClickListener {
            alertPP()
        }

//        btnMoreDone.setOnClickListener {
//            toolbar.visibility = View.VISIBLE
//            activity?.supportFragmentManager?.popBackStack()
//        }
        version.text = "${getString(R.string.version)} ${BuildConfig.VERSION_NAME}"
    }
    fun alertPP() {
        val dialogBulder = AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.privacy_policy_alert, null)
        dialogBulder.setView(view)
        dialogBulder.setCancelable(false)
        val alertDialog = dialogBulder.create()
        alertDialog.show()
        view.closePP.setOnClickListener {
            alertDialog.dismiss()
        }
    }
    @OnClick(R.id.feedBackMore)
    fun feedBackMore(){
        val dialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialogTheme)
        val view = layoutInflater.inflate(R.layout.feedback_bottom_sheet, null)
        val closeFeedbackBS = view.findViewById<TextView>(R.id.closeBottomSheetFeedback)
        closeFeedbackBS.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }
    @OnClick(R.id.btnMoreDone)
    fun btnMoreDone(){
        activity?.supportFragmentManager?.popBackStack()
//        activity?.onBackPressed()
//        fragmentManager?.popBackStack()
//        supportfragmentmanager.popbackstack

    }
}