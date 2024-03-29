package com.rayo.digitaldiary.ui.login

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.rayo.digitaldiary.R
import com.rayo.digitaldiary.baseClasses.BaseActivity
import com.rayo.digitaldiary.baseClasses.BaseViewModel
import com.rayo.digitaldiary.databinding.ActivityAuthenticationBinding
import com.rayo.digitaldiary.helper.Constants
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Mittal Varsani on 14/04/22.
 *
 * @author Mittal Varsani
 */

@AndroidEntryPoint
class AuthenticationActivity : BaseActivity<BaseViewModel?>(),
    NavController.OnDestinationChangedListener {
    lateinit var binding: ActivityAuthenticationBinding

    override val viewModel: BaseViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setDataBindingView(R.layout.activity_authentication)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val graph = inflater.inflate(R.navigation.nav_authentication)
        val navController = navHostFragment.navController
        navController.setGraph(graph, intent.extras)
        navController.addOnDestinationChangedListener(this)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
    }
}