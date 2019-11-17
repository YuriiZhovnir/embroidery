package jdroidcoder.ua.embroidery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import butterknife.ButterKnife
import jdroidcoder.ua.embroidery.fragment.MyPatternsFragment
import io.realm.Realm

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Realm.init(this)
        ButterKnife.bind(this)
        if (null == supportFragmentManager?.findFragmentByTag(MyPatternsFragment.TAG)) {
            supportFragmentManager?.beginTransaction()
                    ?.replace(android.R.id.content, MyPatternsFragment.newInstance(), MyPatternsFragment.TAG)
                    ?.addToBackStack(MyPatternsFragment.TAG)
                    ?.commit()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (true == supportFragmentManager?.fragments?.isEmpty()) {
            finish()
        }
    }
}
