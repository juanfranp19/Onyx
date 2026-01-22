package onyx.movil.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import onyx.movil.fragments.tabs.LoginFragment
import onyx.movil.fragments.tabs.RegisterFragment
import onyx.movil.fragments.tabs.TabLoginRegisterFragment

class TabLoginRegisterViewPagerAdapter(fragmentActivity: TabLoginRegisterFragment) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> LoginFragment()
            1 -> RegisterFragment()
            else -> LoginFragment()
        }
    }
}
