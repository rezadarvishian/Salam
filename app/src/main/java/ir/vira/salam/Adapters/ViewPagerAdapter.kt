package ir.vira.salam.Adapters

import androidx.fragment.app.FragmentStatePagerAdapter
import ir.vira.salam.Fragments.SplashSecurityFragment
import ir.vira.salam.Fragments.SplashStrongFragment
import ir.vira.salam.Fragments.SplashFastFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.util.ArrayList

/**
 * This adapter for set fragments in view pager for intro in splash activity
 *
 * @author Ali Ghasemi
 */
class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    var fragments: MutableList<Fragment> = ArrayList()
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    init {
        fragments.add(SplashSecurityFragment())
        fragments.add(SplashStrongFragment())
        fragments.add(SplashFastFragment())
    }
}