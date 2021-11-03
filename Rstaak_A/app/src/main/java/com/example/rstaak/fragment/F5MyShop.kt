package com.example.rstaak.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rstaak.R
import com.example.rstaak.adapter.F5Adapter
import com.example.rstaak.databinding.F5MyShopBinding
import com.example.rstaak.model.f4_shop.*
import com.example.rstaak.viewModel.F5MyShopViewModel
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import javax.inject.Inject

@AndroidEntryPoint
class F5MyShop : Fragment(), F5Adapter.OnItemClickListener
{
    lateinit var binding: F5MyShopBinding
    var models = ArrayList<F4ShopParentModel>()

    lateinit var f5Adapter: F5Adapter
    @Inject
    lateinit var f5AdapterFactory: F5Adapter.Factory
    @Inject
    lateinit var shopsModelFactory: F4ShopModel.Factory
    @Inject
    lateinit var f4Loading: F4ShopLoading
    @Inject
    lateinit var f4Retry: F4ShopRetry
    private val f5ShopViewModel: F5MyShopViewModel by viewModels()
    private lateinit var progressDialog: AlertDialog
    private var b1 = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.f5_my_shop, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        initProgressDialog()
        //-----------------------------------------------------------------

        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        f5Adapter = f5AdapterFactory.create(models, this)
        binding.recyclerView.adapter = f5Adapter

        //-----------------------------------------------------------------

        f5ShopViewModel.isLoading1.observe(viewLifecycleOwner, {
            if(it)
            {
                models.add(f4Loading)
                f5Adapter.notifyDataSetChanged()
            }
            else
            {
                models.clear()
                f5Adapter.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------

        f5ShopViewModel.modelsLiveData.observe(viewLifecycleOwner, {

            if(it.isNotEmpty())
            {
                models.clear()
                for (item in it)
                {
                    val shopsModel = shopsModelFactory.create(item.address, item.createdDatetime, item.description, item.imageList, item.likeFlag,item.likedNumber,item.modified_on,item.owner,item.phoneNumber,item.productsList,item.shopId,item.shopLink,item.title,item.viewedNumber)
                    models.add(shopsModel)

                }
                f5Adapter.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------

        f5ShopViewModel.error1.observe(viewLifecycleOwner, {
            if(it)
            {
                models.clear()
                models.add(f4Retry)
                f5Adapter.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------

        binding.fab.setOnClickListener {
            it.findNavController().navigate(R.id.action_f5MyShop_to_f51NewShop)
        }

        //-----------------------------------------------------------------

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("newShop")?.observe(viewLifecycleOwner, {
            if(it)
                Toast.makeText(context, "ایجاد فروشگاه جدید با موفقیت انجام شد.", Toast.LENGTH_SHORT).show()
        })

        //-----------------------------------------------------------------

        f5ShopViewModel.error2.observe(viewLifecycleOwner, {
            if(it)
            {
                b1 = false
                Toast.makeText(context, "ارتباط با سرور امکان پذیر نمی باشد.", Toast.LENGTH_SHORT)
                    .show()
            }
        })

        //-----------------------------------------------------------------

        f5ShopViewModel.isLoading2.observe(viewLifecycleOwner, {
            if(it)
                progressDialog.show()
            else
                progressDialog.dismiss()
        })

        //-----------------------------------------------------------------

        f5ShopViewModel.f4ModelData.observe(viewLifecycleOwner, {
            if(b1)
            {
                b1 = false
                val action = F5MyShopDirections.actionF5MyShopToF52EditShop(it)
                findNavController().navigate(action)
            }
        })

        //-----------------------------------------------------------------

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Boolean>("editShop")?.observe(viewLifecycleOwner, {
            if(it)
            {
                Toast.makeText(context, "ویرایش فروشگاه با موفقیت انجام شد.", Toast.LENGTH_SHORT)
                    .show()
                //f5ShopViewModel.getShops()
            }
        })
    }

    override fun onItemClick(view: View, shopId: String?)
    {
        when(view.id)
        {
            R.id.cardView ->
            {
            }

            R.id.edit ->
            {
//                b1 = true
//                f5ShopViewModel.getShop(shopId!!)
            }

            R.id.retry ->
            {
                f5ShopViewModel.getShops()
            }
        }
    }

    companion object
    {
        private const val TAG = "F5MyShop"
    }

    private fun initProgressDialog()
    {
        progressDialog = SpotsDialog(context, R.style.ProgressDialog)
        progressDialog.setCancelable(false)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.window?.setGravity(Gravity.BOTTOM)
    }

    override fun onDestroy()
    {
        super.onDestroy()
        Log.i(TAG, "onDestroy: ")
    }

}