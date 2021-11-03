package com.example.rstaak.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rstaak.R
import com.example.rstaak.adapter.F3Adapter
import com.example.rstaak.databinding.F411ShopProductsBinding
import com.example.rstaak.model.f3_product.*
import com.example.rstaak.viewModel.F3ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class F411ShopProducts : Fragment(), F3Adapter.OnItemClickListener, F3Adapter.OnLoadMoreListener
{
    lateinit var binding: F411ShopProductsBinding
    var models = ArrayList<F3ProductParentModel>()

    private val f3ProductViewModel: F3ProductViewModel by viewModels()
    lateinit var f3Adapter: F3Adapter
    @Inject
    lateinit var f3AdapterFactory: F3Adapter.Factory
    @Inject
    lateinit var f3ModelFactory: F3ProductModel.Factory
    @Inject
    lateinit var f3Loading: F3ProductLoading
    @Inject
    lateinit var productRetry: F3ProductRetry
    @Inject
    lateinit var f3NoMoreData: F3ProductNoMoreData
    private var loadItemIndex = 0
    private lateinit var navController: NavController
    lateinit var shopId: String
    lateinit var categoryId: String
    lateinit var categoryName: String
    private val args: F411ShopProductsArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.f411_shop_products, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        //-------------------------------------------

        navController = Navigation.findNavController(view)

        //-----------------------------------------------------------------

        shopId = args.shopId
        categoryId = args.categoryId
        categoryName = args.categoryName

        binding.title.text = "غرفه $categoryName"

        //-----------------------------------------------------------------

        binding.recyclerView.layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false)
        f3Adapter = f3AdapterFactory.create(models, this, binding.recyclerView, this)
        binding.recyclerView.adapter = f3Adapter

        //-----------------------------------------------------------------

        f3ProductViewModel.isLoading.observe(viewLifecycleOwner, {
            if(it)
            {
                models.add(f3Loading)
                f3Adapter.notifyDataSetChanged()
                loadItemIndex = models.size - 1
            }
            else
            {
                if(models.size > loadItemIndex)
                {
                    if(models[loadItemIndex].currentType == F3ProductParentModel.Loading)
                    {
                        models.removeAt(loadItemIndex)
                        f3Adapter.notifyDataSetChanged()
                        f3Adapter.setLoading(false)
                    }
                }
            }
        })

        //-----------------------------------------------------------------

        f3ProductViewModel.modelsLiveData.observe(viewLifecycleOwner, {

            if(it.isNotEmpty())
            {
                models.clear()
                for (item in it)
                {
                    val productModel = f3ModelFactory.create(item.categoryId, item.categoryName, item.createdDatetime, item.description, item.ifPublished, item.ifUsed, item.imageList, item.likeFlag, item.likedNumber, item.modified_on, item.owner, item.ownerCellNumber, item.price, item.productId, item.rostaakLocation, item.shopId, item.shopName, item.title, item.viewedNumber, item.vitrin)
                    models.add(productModel)

                }
                f3Adapter.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------

        f3ProductViewModel.foundRecord.observe(viewLifecycleOwner, {
            if(!it)
            {
                f3Adapter.setLoading(true)
                models.add(f3NoMoreData)
                f3Adapter.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------

        f3ProductViewModel.error1.observe(viewLifecycleOwner, {
            if(it)
            {
                f3Adapter.setLoading(true)
                models.add(productRetry)
                f3Adapter.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------

        f3ProductViewModel.getProducts2(shopId, categoryId)

        //-----------------------------------------------------------------

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("newProduct")?.observe(viewLifecycleOwner, {
            if(it == "ok")
                Toast.makeText(context, "ثبت آگهی با موفقیت انجام شد.", Toast.LENGTH_SHORT).show()

        })
    }

    override fun onLoadMore()
    {
        f3ProductViewModel.getProducts2(shopId, categoryId)
    }

    override fun onItemClick(view: View, model: F3ProductModel?, position: Int?, likeFlag: Boolean?, clickFlag: String?)
    {
        when(view.id)
        {
            R.id.cardView ->
            {
                if(clickFlag!! == "Long")
                {
                    if(likeFlag!!) f3ProductViewModel.likeProduct(model!!.productId)
                    else f3ProductViewModel.dislikeProduct(model!!.productId)
                }
                else if(clickFlag == "Short")
                {
                    f3ProductViewModel.viewProduct(model!!.productId)
                    val action = F3ProductsDirections.actionFragment3ToFragment8(model)
                    view.findNavController().navigate(action)
                }
            }

            R.id.retry ->
            {
                models.removeAt(position!!)
                f3Adapter.notifyDataSetChanged()
                f3Adapter.setLoading(false)
                f3ProductViewModel.error1.value = false
                f3ProductViewModel.getProducts2(shopId, categoryId)
            }
        }
    }

    companion object
    {
        private const val TAG = "Fragment3Products"
    }

    override fun onDestroy()
    {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
    }
}