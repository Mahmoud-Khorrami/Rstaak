package com.example.rstaak.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rstaak.R
import com.example.rstaak.adapter.F4Adapter
import com.example.rstaak.databinding.F4ShopsBinding
import com.example.rstaak.model.f3_product.*
import com.example.rstaak.model.f4_shop.*
import com.example.rstaak.viewModel.F4ShopViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class F4Shops : Fragment(), F4Adapter.OnItemClickListener, F4Adapter.OnLoadMoreListener
{
    lateinit var binding: F4ShopsBinding
    var models = ArrayList<F4ShopParentModel>()

    lateinit var f4Adapter: F4Adapter
    @Inject
    lateinit var f4AdapterFactory: F4Adapter.Factory
    @Inject
    lateinit var shopsModelFactory: F4ShopModel.Factory
    @Inject
    lateinit var f4Loading: F4ShopLoading
    @Inject
    lateinit var f4Retry: F4ShopRetry
    @Inject
    lateinit var f4NoMoreData: F4ShopNoMoreData
    private var loadItemIndex = 0
    private val f4ShopViewModel: F4ShopViewModel by viewModels()
    private lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.f4_shops, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        //-------------------------------------------

        navController = Navigation.findNavController(view)

        //-----------------------------------------------------------------

        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        f4Adapter = f4AdapterFactory.create(models,this, binding.recyclerView, this)
        binding.recyclerView.adapter = f4Adapter

        //-----------------------------------------------------------------

        f4ShopViewModel.isLoading.observe(viewLifecycleOwner, {
            if(it)
            {
                models.add(f4Loading)
                f4Adapter.notifyDataSetChanged()
                loadItemIndex = models.size - 1
            }
            else
            {
                if(models.size > loadItemIndex)
                {
                    if(models[loadItemIndex].currentType == F3ProductParentModel.Loading)
                    {
                        models.removeAt(loadItemIndex)
                        f4Adapter.notifyDataSetChanged()
                        f4Adapter.setLoading(false)
                    }
                }
            }
        })

        //-----------------------------------------------------------------

        f4ShopViewModel.modelsLiveData.observe(viewLifecycleOwner, {

            if(it.isNotEmpty())
            {
                models.clear()
                for (item in it)
                {
                    val shopsModel = shopsModelFactory.create(item.address, item.createdDatetime, item.description, item.imageList, item.likeFlag,item.likedNumber,item.modified_on,item.owner,item.phoneNumber,item.productsList,item.shopId,item.shopLink,item.title,item.viewedNumber)
                    models.add(shopsModel)

                }
                f4Adapter.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------

        f4ShopViewModel.foundRecord.observe(viewLifecycleOwner, {
            if(!it)
            {
                f4Adapter.setLoading(true)
                models.add(f4NoMoreData)
                f4Adapter.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------

        f4ShopViewModel.error.observe(viewLifecycleOwner, {
            if(it)
            {
                f4Adapter.setLoading(true)
                models.add(f4Retry)
                f4Adapter.notifyDataSetChanged()
            }
        })
    }

    override fun onLoadMore()
    {
        f4ShopViewModel.getShops()
    }

    override fun onItemClick(view: View, model: F4ShopModel?, position: Int?, likeFlag: Boolean?, clickFlag: String?)
    {
        when(view.id)
        {
            R.id.cardView ->
            {
                if(clickFlag!! == "Long")
                {
                    if(likeFlag!!) f4ShopViewModel.likeShop(model!!.shopId)
                    else f4ShopViewModel.dislikeShop(model!!.shopId)
                }
                else if(clickFlag == "Short")
                {
                    f4ShopViewModel.viewShop(model!!.shopId)
                    val action = F4ShopsDirections.actionF4ShopsToF41ShopDetails(model.shopId)
                    view.findNavController().navigate(action)
                }
            }

            R.id.retry ->
            {
                models.removeAt(position!!)
                f4Adapter.notifyDataSetChanged()
                f4Adapter.setLoading(false)
                f4ShopViewModel.error.value = false
                f4ShopViewModel.getShops()
            }
        }
    }

    companion object
    {
        private const val TAG = "Fragment4Shops"
    }

    override fun onDestroy()
    {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
    }
}