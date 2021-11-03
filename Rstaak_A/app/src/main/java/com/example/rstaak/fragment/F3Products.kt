package com.example.rstaak.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
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
import androidx.navigation.fragment.findNavController //import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rstaak.R
import com.example.rstaak.model.f3_product.*
import com.example.rstaak.adapter.F3Adapter
import com.example.rstaak.databinding.F3ProductsBinding
import com.example.rstaak.general.MyResult
import com.example.rstaak.viewModel.F3ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import javax.inject.Inject

@AndroidEntryPoint
class F3Products : Fragment(), F3Adapter.OnItemClickListener, F3Adapter.OnLoadMoreListener
{
    lateinit var binding: F3ProductsBinding
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
    private lateinit var f3ProductModel: F3ProductModel
    @Inject
    lateinit var f3ChatStatusFactory: F3ChatStatus.Factory
    private var b1 = false
    private lateinit var progressDialog: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.f3_products, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        initProgressDialog() //-------------------------------------------

        navController = Navigation.findNavController(view)

        //-----------------------------------------------------------------

        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
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

        f3ProductViewModel.getProducts1()

        //-----------------------------------------------------------------

        binding.fab.setOnClickListener {
            it.findNavController().navigate(R.id.action_fragment3_to_fragment9CreateAdvertisement)
        }

        //-----------------------------------------------------------------

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("newProduct")
            ?.observe(viewLifecycleOwner, {
                if(it == "ok") Toast.makeText(context, "ثبت آگهی با موفقیت انجام شد.", Toast.LENGTH_SHORT)
                    .show()

            })

        //-----------------------------------------------------------------

        f3ProductViewModel.f3Response3Data.observe(viewLifecycleOwner, {

            if(b1)
            {
                when (it.status)
                {
                    MyResult.Status.SUCCESS ->
                    {
                        b1 = false
                        progressDialog.dismiss()

                        val image = if(f3ProductModel.imageList.isNotEmpty()) f3ProductModel.imageList[0] else ""
                        val chatId = if(it.data?.chatId.isNullOrEmpty()) "" else it.data?.chatId

                        val chatStatus = f3ChatStatusFactory.create(chatId = chatId, productId = f3ProductModel.productId, ownerId = f3ProductModel.owner, productTitle = f3ProductModel.title, productImage = image)
                        val action = F3ProductsDirections.actionFragment3ToF61Chat(chatStatus)
                        findNavController().navigate(action)
                    }

                    MyResult.Status.ERROR ->
                    {
                        b1 = false
                        progressDialog.dismiss()
                        Log.i(TAG, "onViewCreated: ${it.error}")
                        Toast.makeText(context, "ارتباط با سرور امکان پذیر نمی باشد.", Toast.LENGTH_SHORT)
                            .show()
                    }

                    MyResult.Status.LOADING ->
                    {
                        progressDialog.show()
                    }
                }
            }
        })
    }

    override fun onLoadMore()
    {
        f3ProductViewModel.getProducts1()
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
                f3ProductViewModel.getProducts1()
            }

            R.id.chat ->
            {
                b1 = true
                f3ProductModel = model!!
                f3ProductViewModel.chatStatus(model.owner, model.productId)
            }
        }
    }

    private fun initProgressDialog()
    {
        progressDialog = SpotsDialog(context, R.style.ProgressDialog)
        progressDialog.setCancelable(false)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.window?.setGravity(Gravity.BOTTOM)
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