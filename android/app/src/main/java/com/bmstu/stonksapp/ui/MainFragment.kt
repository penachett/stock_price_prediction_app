package com.bmstu.stonksapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bmstu.stonksapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {

    private lateinit var fragName: TextView
    private lateinit var logoutIcon: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity() as? MainActivity)?.setStatusBarColor(R.color.primary_dark)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_main, container, false)
        fragName = view.findViewById(R.id.main_fragment_name)
        logoutIcon = view.findViewById(R.id.logout_icon)
        logoutIcon.setOnClickListener {
            parentFragment?.findNavController()?.navigate(R.id.action_to_auth_fragment)
        }
        setBottomMenuNavigation(view)
        return view
    }

    private fun setBottomMenuNavigation(view: View) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.inner_nav_host_fragment) as NavHostFragment?
        if (navHostFragment != null) {
            val navController = navHostFragment.navController
            navController.addOnDestinationChangedListener { _: NavController?, destination: NavDestination, _: Bundle? ->
                if (destination.id == R.id.navigation_predictions) {
                    logoutIcon.visibility = View.VISIBLE
                    fragName.text = resources.getString(R.string.my_predictions)
                } else if (destination.id == R.id.navigation_stocks) {
                    logoutIcon.visibility = View.GONE
                    fragName.text = resources.getString(R.string.stocks_list)
                }
            }
            val bottomNav: BottomNavigationView = view.findViewById(R.id.bottom_nav_view)
            NavigationUI.setupWithNavController(bottomNav, navController)
        }
    }

    companion object {
        const val TAG = "Main Fragment"
    }
}