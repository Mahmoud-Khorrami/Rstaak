package com.example.rstaak.fragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.rstaak.R
import com.example.rstaak.databinding.F0SplashScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class F0SplashScreen : Fragment()
{
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: F0SplashScreenBinding
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = F0SplashScreenBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        //-------------------------------------------

        navController = Navigation.findNavController(view)

        if(sharedPreferences.contains("phoneNumber"))
        {
            findNavController().navigate(R.id.action_fragment0_to_fragment3)
        }
         else
        {
            findNavController().navigate(R.id.action_fragment0_to_fragment1)

        }
    }

}