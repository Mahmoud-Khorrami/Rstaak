package com.example.rstaak

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.transition.Slide
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import com.example.rstaak.databinding.ActivityMainBinding
import com.example.rstaak.general.BottomNavManager
import com.example.rstaak.service.SocketIOService
import com.example.rstaak.viewModel.F1RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity()
{
    lateinit var binding: ActivityMainBinding
    private lateinit var bottomNavManager: BottomNavManager
    @Inject
    lateinit var bottomNavManagerFactory: BottomNavManager.BottomNavManagerFactory
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //------------------------------------

        val registerViewModel = ViewModelProvider(this).get(F1RegisterViewModel::class.java)
        binding.viewModel = registerViewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this

        //------------------------------------

        bottomNavManager = bottomNavManagerFactory.create(supportFragmentManager, binding.bottomNavigationView)
        setupNavigationManager()
        binding.bottomNavigationView.selectedItemId = R.id.navigation_products


        //------------------------------------

        if(sharedPreferences.getString("userId", null) != null)
        {
            val intent = Intent(this, SocketIOService::class.java)
            intent.putExtra(SocketIOService.EVENT_TYPE, "join")
            startService(intent)
        }
    }

    private fun setupNavigationManager()
    {
        bottomNavManager.setupNavController()
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        bottomNavManager.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle)
    {
        super.onRestoreInstanceState(savedInstanceState)
        bottomNavManager.onRestoreInstanceState(savedInstanceState)
        setupNavigationManager()
    }

    override fun onBackPressed()
    {
        if(!bottomNavManager.onBackPressed()) super.onBackPressed()
    }

    override fun onDestroy()
    {
        super.onDestroy()

        //--------------------------------------

        if(sharedPreferences.getString("userId", null) != null)
        {
            val intent = Intent(this, SocketIOService::class.java)
            intent.putExtra(SocketIOService.EVENT_TYPE, "offline")
            startService(intent)
        }
    }
}