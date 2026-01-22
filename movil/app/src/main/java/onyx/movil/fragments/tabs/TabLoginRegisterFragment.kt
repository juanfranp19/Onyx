package onyx.movil.fragments.tabs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import onyx.movil.R
import onyx.movil.databinding.FragmentTabLoginRegisterBinding
import onyx.movil.viewpager.TabLoginRegisterViewPagerAdapter

class TabLoginRegisterFragment : Fragment() {
    private lateinit var binding: FragmentTabLoginRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTabLoginRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TabLoginRegisterViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) {
            tab, position -> tab.text = when (position) {
                0 -> getString(R.string.tab_login)
                1 -> getString(R.string.tab_register)
                else -> ""
            }
        }.attach()
    }
}
