package com.example.rstaak.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.rstaak.R
import com.example.rstaak.adapter.ImageAdapter
import com.example.rstaak.databinding.F31ProductDetailsBinding
import com.example.rstaak.model.f3_product.F3ProductModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DecimalFormat
import javax.inject.Inject


@AndroidEntryPoint
class F31ProductDetails : Fragment()
{
    private lateinit var binding: F31ProductDetailsBinding
    private val args: F31ProductDetailsArgs by navArgs()
    private lateinit var f3ProductModel: F3ProductModel
    @Inject
    lateinit var imageAdapterFactory: ImageAdapter.Factory
    @Inject
    lateinit var df: DecimalFormat

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.f31_product_details, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        //---------------------------------------

        f3ProductModel = args.f3ProductModel

        //---------------------------------------

        setImageAdapter()
        //---------------------------------------

        val city = if(f3ProductModel.rostaakLocation.type == "shahr")
            f3ProductModel.rostaakLocation.ostan + "، شهر " + f3ProductModel.rostaakLocation.name
        else
            f3ProductModel.rostaakLocation.ostan + "، " + f3ProductModel.rostaakLocation.shahrestan + "، بخش " + f3ProductModel.rostaakLocation.bakhsh + "، روستای " + f3ProductModel.rostaakLocation.name

        //---------------------------------------

        val price = "${df.format(f3ProductModel.price)} تومان "

        //---------------------------------------
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = f3ProductModel
            cityName = city
            price1 = price
        }
        //---------------------------------------
    }

    private fun setImageAdapter()
    {
        val adapter = imageAdapterFactory.create(f3ProductModel.imageList)
        binding.pager.adapter = adapter
        binding.springDotsIndicator.setViewPager2(binding.pager)
    }
}