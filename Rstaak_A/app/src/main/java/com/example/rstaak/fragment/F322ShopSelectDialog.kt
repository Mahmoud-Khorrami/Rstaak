package com.example.rstaak.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rstaak.R
import com.example.rstaak.adapter.F322Adapter
import com.example.rstaak.databinding.F322SelectShopDialogBinding
import com.example.rstaak.model.f322_shop_select.F322Loading
import com.example.rstaak.model.f322_shop_select.F322Model
import com.example.rstaak.model.f322_shop_select.F322ParentModel
import com.example.rstaak.model.f322_shop_select.F322Retry
import com.example.rstaak.viewModel.F322ShopSelectViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class F322ShopSelectDialog : BottomSheetDialogFragment(), F322Adapter.OnItemClickListener
{
    private lateinit var binding: F322SelectShopDialogBinding
    private val f322ShopSelectViewModel: F322ShopSelectViewModel by viewModels()
    @Inject
    lateinit var f322AdapterFactory: F322Adapter.Factory
    lateinit var f322Adapter: F322Adapter
    private var models = ArrayList<F322ParentModel>()
    @Inject
    lateinit var f322Loading: F322Loading
    @Inject
    lateinit var f322Retry: F322Retry

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.f322_select_shop_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        //-----------------------------------------------------------------

        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        f322Adapter = f322AdapterFactory.create(models, this)
        binding.recyclerView.adapter = f322Adapter

        //-----------------------------------------------------------------

        f322ShopSelectViewModel.isLoading.observe(viewLifecycleOwner, {
            if(it)
            {
                models.clear()
                models.add(f322Loading)
                f322Adapter.notifyDataSetChanged()
            }
            else
            {
                models.clear()
                f322Adapter.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------

        f322ShopSelectViewModel.shopSelectModelsData.observe(viewLifecycleOwner, {
            models.clear()
            models.addAll(it)
            f322Adapter.notifyDataSetChanged()
        })

        //-----------------------------------------------------------------

        f322ShopSelectViewModel.error.observe(viewLifecycleOwner, {
            if(it)
            {
                models.clear()
                models.add(f322Retry)
                f322Adapter.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------
    }

    override fun onItemClick(view: View, model: F322Model?)
    {
        when(view.id)
        {
            R.id.cardView ->
            {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("shopSelectModel", model)
                findNavController().popBackStack()
            }

            R.id.retry ->
            {
                f322ShopSelectViewModel.getShop()
            }
        }
    }
}