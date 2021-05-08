package com.bmstu.stonksapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.bmstu.stonksapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_main, container, false)
        setBottomMenuNavigation(view)
        return view
    }

    private fun setBottomMenuNavigation(view: View) {
        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.inner_nav_host_fragment) as NavHostFragment?
        if (navHostFragment != null) {
            val navController = navHostFragment.navController
            navController.addOnDestinationChangedListener { controller: NavController?, destination: NavDestination, arguments: Bundle? ->
                if (destination.id == R.id.navigation_predictions) {

                } else if (destination.id == R.id.navigation_stocks) {

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