package com.example.rstaak.fragment

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rstaak.R
import com.example.rstaak.adapter.F321Adapter
import com.example.rstaak.databinding.F321LocationDialogBinding
import com.example.rstaak.model.f321_location.*
import com.example.rstaak.req_res.f321_location.LocationDetails
import com.example.rstaak.viewModel.F321LocationViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class F321LocationDialog: BottomSheetDialogFragment(), F321Adapter.OnItemClickListener
{
    private lateinit var binding: F321LocationDialogBinding
    private val f321LocationViewModel: F321LocationViewModel by viewModels()
    @Inject
    lateinit var f321AdapterFactory: F321Adapter.Factory
    lateinit var f321Adapter: F321Adapter
    private var models = ArrayList<F321ParentModel>()
    @Inject
    lateinit var f321Loading: F321Loading
    @Inject
    lateinit var locationRetry: F321Retry
    @Inject
    lateinit var f321NotFound: F321NotFound
    @Inject
    lateinit var locationDetailsFactory: LocationDetails.Factory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.f321_location_dialog, container, false)
        return binding.root
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        //-----------------------------------------------------------------

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        //-----------------------------------------------------------------

        binding.searchView.visibility = View.VISIBLE
        binding.lnr1.visibility = View.GONE

        //-----------------------------------------------------------------
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        f321Adapter = f321AdapterFactory.create(models, this)
        binding.recyclerView.adapter = f321Adapter

        //-----------------------------------------------------------------

        f321LocationViewModel.isLoading.observe(viewLifecycleOwner, {
            if(it)
            {
                models.clear()
                models.add(f321Loading)
                f321Adapter.notifyDataSetChanged()
            }
            else
            {
                models.clear()
                f321Adapter.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------

        f321LocationViewModel.locationModelsData.observe(viewLifecycleOwner, {
            models.clear()
            models.addAll(it)
            f321Adapter.notifyDataSetChanged()
        })

        //-----------------------------------------------------------------

        f321LocationViewModel.error.observe(viewLifecycleOwner, {
            if(it)
            {
                models.clear()
                models.add(locationRetry)
                f321Adapter.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------

        f321LocationViewModel.notFound.observe(viewLifecycleOwner, {
            if(it)
            {
                models.clear()
                models.add(f321NotFound)
                f321Adapter.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------

        binding.back.setOnClickListener {
            if(models.size > 0)
            {
                if(models[0].currentType == F321ParentModel.Main)
                {
                    val locationModel = models[0] as F321Model

                    if(locationModel.type != "shahrestan")
                        binding.cityDetails.text = binding.cityDetails.text.toString()
                            .substring(0, binding.cityDetails.text.toString().lastIndexOf("،"))

                    when (locationModel.type)
                    {
                        "abadi" -> f321LocationViewModel.getDehshahr(locationModel.bakhshId!!)
                        "dehestan" ->f321LocationViewModel.getBakhsh(locationModel.shahrestanId!!)
                        "shahr" -> f321LocationViewModel.getBakhsh(locationModel.shahrestanId!!)
                        "bakhsh" -> f321LocationViewModel.getShahrestan(locationModel.ostanId)

                        "shahrestan" -> {
                            binding.lnr1.visibility = View.GONE
                            binding.searchView.visibility = View.VISIBLE
                            f321LocationViewModel.getOstan()
                        }
                    }
                }
                else
                {
                    models.clear()
                    models.add(locationRetry)
                    f321Adapter.notifyDataSetChanged()
                }
            }
        }

        //-----------------------------------------------------------------

        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String?): Boolean
            {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean
            {
                if(newText!!.length > 2)
                    f321LocationViewModel.locationRegex(newText)
                else
                    f321LocationViewModel.getOstan()

                return true
            }

        })
    }

    @SuppressLint("SetTextI18n")
    override fun onItemClick(view: View, model: F321Model?)
    {
      when(view.id)
      {
          R.id.lnr -> {

              when(model?.type)
              {
                  "ostan" -> {
                      binding.searchView.visibility = View.GONE
                      binding.lnr1.visibility = View.VISIBLE
                      binding.cityDetails.text = "استان ${model.ostanName}"
                      f321LocationViewModel.getShahrestan(model.ostanId)
                  }
                  "shahrestan" -> {
                      binding.cityDetails.text = "${binding.cityDetails.text}، شهرستان  ${model.shahrestanName}"
                      f321LocationViewModel.getBakhsh(model.shahrestanId!!)
                  }
                  "bakhsh"-> {
                      binding.cityDetails.text = "${binding.cityDetails.text}، بخش  ${model.bakhshName}"
                      f321LocationViewModel.getDehshahr(model.bakhshId!!)
                  }
                  "dehestan" -> {
                      binding.cityDetails.text = "${binding.cityDetails.text}، دهستان  ${model.dehshahrName}"
                      f321LocationViewModel.getAbadi(model.dehshahrId!!)
                  }

                  "shahr" ->{
                      val id = model.dehshahrId!!
                      val name = "${model.ostanName} ، ${model.dehshahrName}"
                      val type = model.type
                      val locationDetails = locationDetailsFactory.create(name, id, type)
                      findNavController().previousBackStackEntry?.savedStateHandle?.set("locationDetails", locationDetails)
                      findNavController().popBackStack()
                  }

                  "abadi" ->{
                      val id = model.abadiId!!
                      val name = "${model.ostanName}، ${model.shahrestanName}، بخش ${model.bakhshName}، ${model.abadiName}"
                      val type = model.type
                      val locationDetails = locationDetailsFactory.create(name, id, type)
                      findNavController().previousBackStackEntry?.savedStateHandle?.set("locationDetails", locationDetails)
                      findNavController().popBackStack()
                  }
              }
          }

          R.id.retry -> f321LocationViewModel.getOstan()
      }
    }

    override fun onDestroy()
    {
        super.onDestroy()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR
    }
}