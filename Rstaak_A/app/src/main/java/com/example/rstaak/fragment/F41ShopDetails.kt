package com.example.rstaak.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rstaak.R
import com.example.rstaak.adapter.F41Adapter1
import com.example.rstaak.adapter.F41Adapter2
import com.example.rstaak.databinding.F41ShopDetailsBinding
import com.example.rstaak.model.f41_shop_details.*
import com.example.rstaak.viewModel.F41ShopDetailsViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class F41ShopDetails : Fragment(), F41Adapter1.OnItemClickListener1, F41Adapter2.OnItemClickListener2
{
    private lateinit var binding: F41ShopDetailsBinding
    private val f41ShopDetailsViewModel: F41ShopDetailsViewModel by viewModels()
    @Inject
    lateinit var f41Adapter1Factory: F41Adapter1.Factory
    private lateinit var f41Adapter1: F41Adapter1
    private var models = ArrayList<F41ParentModel>()
    @Inject
    lateinit var f41Loading: F41Loading
    @Inject
    lateinit var f41Retry: F41Retry
    @Inject
    lateinit var f41ShowCase: F41ShowCase
    private val args: F41ShopDetailsArgs by navArgs()
    @Inject
    lateinit var f41Model1Factory: F41Model1.Factory
    private lateinit var shopId: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.f41_shop_details, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        //-----------------------------------------------------------------

        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        f41Adapter1 = f41Adapter1Factory.create(models, this, this)
        binding.recyclerView.adapter = f41Adapter1

        //-----------------------------------------------------------------

        f41ShopDetailsViewModel.isLoading.observe(viewLifecycleOwner, {
            if(it)
            {
                models.add(f41Loading)
                f41Adapter1.notifyDataSetChanged()
            }
            else
            {
                models.clear()
                f41Adapter1.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------

        f41ShopDetailsViewModel.f41Models1Data.observe(viewLifecycleOwner, {

            if(it.isNotEmpty())
            {
               // models.add(f41ShowCase)

                for (item in it)
                {
                    val f41Model1 = f41Model1Factory.create(item.childs, item.parentCategoryId, item.parentCategoryName, shopId)
                    models.add(f41Model1)
                }
                f41Adapter1.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------

        f41ShopDetailsViewModel.error.observe(viewLifecycleOwner, {
            if(it)
            {
                models.clear()
                models.add(f41Retry)
                f41Adapter1.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------

        shopId = args.shopId
        f41ShopDetailsViewModel.getShopsSpecial(shopId)

        //-----------------------------------------------------------------
    }

    override fun onItemClick1()
    {
        f41ShopDetailsViewModel.getShopsSpecial(shopId)
    }

    override fun onItemClick2(categoryId: String, categoryName: String)
    {
        val action = F41ShopDetailsDirections.actionF41ShopDetailsToF411ShopProducts(shopId, categoryId, categoryName)
        findNavController().navigate(action)
    }

}