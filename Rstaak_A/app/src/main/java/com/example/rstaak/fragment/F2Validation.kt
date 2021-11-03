package com.example.rstaak.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.rstaak.R
import com.example.rstaak.databinding.F2ValidationBinding
import com.example.rstaak.general.RstaakUtils
import com.example.rstaak.service.SocketIOService
import com.example.rstaak.viewModel.F1RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class F2Validation : Fragment()
{
    lateinit var binding: F2ValidationBinding
    private lateinit var navController: NavController
    @Inject
    lateinit var rstaakUtils: RstaakUtils

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.f2_validation, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        //-------------------------------------------------

        navController = Navigation.findNavController(view)

        //-------------------------------------------------

        val registerViewModel = ViewModelProvider(requireActivity()).get(F1RegisterViewModel::class.java)
        binding.viewModel = registerViewModel
        binding.executePendingBindings()
        binding.lifecycleOwner = this

        //-------------------------------------------------

        registerViewModel.response2.observe(viewLifecycleOwner, {
            it?.let {

                if(it == "success")
                {
                    val intent = Intent(context, SocketIOService::class.java)
                    intent.putExtra(SocketIOService.EVENT_TYPE, "join")
                    requireActivity().startService(intent)

                    rstaakUtils.hideKeyboard(requireActivity())
                    navController.navigate(R.id.action_fragment2_to_fragment3)
                }

                else Toast.makeText(activity?.applicationContext, it, Toast.LENGTH_LONG).show()
            }
        })

    }

}