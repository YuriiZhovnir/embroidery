package jdroidcoder.ua.embroidery.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import butterknife.OnClick
import io.realm.Realm
import jdroidcoder.ua.embroidery.Pattern
import jdroidcoder.ua.embroidery.R
import jdroidcoder.ua.embroidery.adapter.OnItemClick
import jdroidcoder.ua.embroidery.adapter.PatternAdapter
import kotlinx.android.synthetic.main.fragment_my_patterns.*


class MyPatternsFragment : BaseFragment(), OnItemClick {

    companion object {
        const val TAG = "MyPatternsFragment"
        fun newInstance() = MyPatternsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_my_patterns, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (false == Realm.getDefaultInstance()?.where(Pattern::class.java)?.findAll()?.isEmpty()) {
            patternsList?.visibility = View.VISIBLE
            patternsList?.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            patternsList?.adapter = PatternAdapter(this)
        }
    }


    @OnClick(R.id.addPattern)
    fun addPattern() {
        val view = layoutInflater.inflate(R.layout.add_pattern_alert_style, null)

        val dialog = context?.let {
            AlertDialog.Builder(it)
                    .setView(view)
                    .setCancelable(false)
                    .create()
        }

        view?.findViewById<Button>(R.id.cancel)?.apply {
            this?.setOnClickListener {
                dialog?.dismiss()
            }
        }

        view?.findViewById<Button>(R.id.done)?.apply {
            this?.setOnClickListener {
                try {
                    val height = view?.findViewById<EditText>(R.id.height)?.text?.toString()?.toInt()
                    val width = view?.findViewById<EditText>(R.id.width)?.text?.toString()?.toInt()
                    if (height ?: 0 in 151 downTo 9 && width ?: 0 in 151 downTo 9) {
                        val pattern = Pattern(width, height)
                        val realm = Realm.getDefaultInstance()
                        realm.beginTransaction()
                        val temp = realm.copyToRealm(pattern)
                        realm.commitTransaction()
                        if (null == activity?.supportFragmentManager?.findFragmentByTag(PatternBuilderFragment.TAG)) {
                            width?.let { it1 ->
                                height?.let { it2 ->
                                    PatternBuilderFragment.newInstance(it1, it2, temp.id)
                                }
                            }?.let { it2 ->
                                activity?.supportFragmentManager?.beginTransaction()
                                        ?.replace(android.R.id.content, it2, PatternBuilderFragment.TAG)
                                        ?.addToBackStack(PatternBuilderFragment.TAG)
                                        ?.commit()
                            }
                        }
                        dialog?.dismiss()
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }

        dialog?.show()
    }

    override fun onItemClick(pattern: Pattern) {
        if (null == activity?.supportFragmentManager?.findFragmentByTag(PatternBuilderFragment.TAG)) {
            pattern?.width?.let { it1 ->
                pattern?.height?.let { it2 ->
                    PatternBuilderFragment.newInstance(it1, it2, pattern.id)
                }
            }?.let { it2 ->
                activity?.supportFragmentManager?.beginTransaction()
                        ?.replace(android.R.id.content, it2, PatternBuilderFragment.TAG)
                        ?.addToBackStack(PatternBuilderFragment.TAG)
                        ?.commit()
            }
        }
    }

    @OnClick(R.id.more)
    fun more() {
        toolbar.visibility = View.GONE
        val fragment = MoreFragment.newInstance()
        activity?.supportFragmentManager?.beginTransaction()
                ?.replace(android.R.id.content, fragment,MoreFragment.TAG)
                ?.addToBackStack(MoreFragment.TAG)
                ?.commit()
    }
}