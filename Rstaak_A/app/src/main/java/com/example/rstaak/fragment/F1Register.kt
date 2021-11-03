package com.example.rstaak.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.rstaak.R
import com.example.rstaak.databinding.F1RegisterBinding
import com.example.rstaak.general.RstaakUtils
import com.example.rstaak.viewModel.F1RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class F1Register : Fragment()
{
    lateinit var binding : F1RegisterBinding
    private lateinit var navController: NavController
    @Inject
    lateinit var rstaakUtils: RstaakUtils

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(layoutInflater,R.layout.f1_register,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        //-------------------------------------------

        val registerViewModel = ViewModelProvider(requireActivity()).get(F1RegisterViewModel::class.java)
        binding.viewModel = registerViewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this

        //-------------------------------------------

        navController = Navigation.findNavController(view)

        //-------------------------------------------

        registerViewModel.response1.observe(viewLifecycleOwner, {
            it?.let {
                if(it == "success")
                {
                    rstaakUtils.hideKeyboard(requireActivity())
                    navController.navigate(R.id.action_fragment1_to_fragment2)
                }

                else if(it != "")
                    Toast.makeText(activity?.applicationContext, "برقراری ارتباط با سرور امکان پذیر نمی باشد.", Toast.LENGTH_LONG).show()
            }
        })
    }

}