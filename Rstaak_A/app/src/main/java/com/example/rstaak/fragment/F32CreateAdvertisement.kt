package com.example.rstaak.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.rstaak.R
import com.example.rstaak.general.Comma
import com.example.rstaak.general.ImagePicker
import com.example.rstaak.model.f323_category_select.F323Model
import com.example.rstaak.model.F32Model
import com.example.rstaak.model.f322_shop_select.F322Model
import com.example.rstaak.req_res.f321_location.LocationDetails
import com.example.rstaak.adapter.F32Adapter
import com.example.rstaak.databinding.F32CreateAdvertisementBinding
import com.example.rstaak.viewModel.F32ViewModel
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class F32CreateAdvertisement : Fragment()
{
    lateinit var binding: F32CreateAdvertisementBinding
    @Inject
    lateinit var models: ArrayList<F32Model>
    @Inject
    lateinit var f32ModelFactory: F32Model.Factory
    @Inject
    lateinit var f32AdapterFactory: F32Adapter.Factory
    lateinit var f32Adapter: F32Adapter
    private val f32ViewModel: F32ViewModel by viewModels()
    private val imageList = ArrayList<String>()
    @Inject
    lateinit var imagePicker: ImagePicker
    private var shopId = ""
    private var categoryId = ""
    @Inject
    lateinit var locationDetailsFactory: LocationDetails.Factory
    lateinit var locationDetails :LocationDetails
    @Inject
    lateinit var commaFactory: Comma.Factory
    private lateinit var comma: Comma
    private lateinit var progressDialog: AlertDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = F32CreateAdvertisementBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SourceLockedOrientationActivity", "SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initProgressDialog()
        //-----------------------------------------------------------------

        locationDetails = locationDetailsFactory.create("","","")
        comma = commaFactory.create(binding.price)
        binding.price.addTextChangedListener(comma)

        //-----------------------------------------------------------------

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = f32ViewModel
        }

        //-----------------------------------------------------------------
        binding.locationName.setOnClickListener {
            it.findNavController().navigate(R.id.action_fragment9_to_cityDialog)
        }

        //-----------------------------------------------------------------

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<LocationDetails>("locationDetails")?.observe(viewLifecycleOwner, {
            binding.locationName.setText(it.name)
            locationDetails = it
        })

        //-----------------------------------------------------------------

        binding.shopName.setOnClickListener {
            it.findNavController().navigate(R.id.action_fragment9_to_selectShopDialog)
        }

        //-----------------------------------------------------------------

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<F322Model>("shopSelectModel")?.observe(viewLifecycleOwner, {
            it.let {
                binding.shopName.setText(it.title)
                shopId = it.id
            }
        })

        //-----------------------------------------------------------------

        binding.categoryName.setOnClickListener {
            it.findNavController().navigate(R.id.action_fragment9_to_categorySelectDialog)
        }

        //-----------------------------------------------------------------

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<F323Model>("categoryModel")?.observe(viewLifecycleOwner, {
            binding.categoryName.setText("${it.parentName}، ${it.title}")
            categoryId = it.categoryId
        })

        //-----------------------------------------------------------------
        binding.recyclerView.layoutManager = GridLayoutManager(context,3)
        f32Adapter = f32AdapterFactory.create(models)
        binding.recyclerView.adapter = f32Adapter

        //-----------------------------------------------------------------

        binding.addImage.setOnClickListener {

            if(models.size < 9)
            {
                FishBun.with(this).setImageAdapter(GlideAdapter())
                    .setIsUseDetailView(false).setMaxCount(9 - models.size).setMinCount(1)
                    .setPickerSpanCount(3)
                    .setActionBarColor(Color.parseColor("#dcedc8"), Color.parseColor("#aabb97"), false)
                    .setActionBarTitleColor(Color.parseColor("#000000")).setAlbumSpanCount(1, 2)
                    .setButtonInAlbumActivity(true).hasCameraInPickerPage(true)
                    .setReachLimitAutomaticClose(true)
                    .setHomeAsUpIndicatorDrawable(ResourcesCompat.getDrawable(resources,R.drawable.back,null))
                    .setDoneButtonDrawable(ResourcesCompat.getDrawable(resources,R.drawable.done,null))
                    .setAllViewTitle("همه تصاویر").setActionBarTitle("گالری تصاویر")
                    .textOnImagesSelectionLimitReached("Limit Reached!")
                    .textOnNothingSelected("هیچ عکسی انتخاب نشده است.")
                    .setSelectCircleStrokeColor(Color.BLACK).isStartInAllView(false).startAlbum()
            }
            else Toast.makeText(context, "حداکثر می توان 9 تصویر را انتخاب کرد.", Toast.LENGTH_LONG)
                .show()
        }

        //-----------------------------------------------------------------

        binding.approve.setOnClickListener {
            imageList.clear()
            for (item in models)
            {
                val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, item.uri)
                val s: String = imagePicker.getStringImage(bitmap, 500)
                imageList.add(s)
            }

            //-------------------------------------

            var ifUsed = false
            if(binding.used.isChecked) ifUsed = true

            var price = 1
            if(binding.price.text.toString().isNotEmpty())
                price = comma.trimCommaOfString(binding.price.text.toString()).toString().toInt()

            //-------------------------------------

            f32ViewModel.createNewProduct(shopId,categoryId,ifUsed,locationDetails,imageList,price)
        }
        //-----------------------------------------------------------------

        f32ViewModel.response.observe(viewLifecycleOwner, {
            if(it == 200)
            {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("newProduct", "ok")
                findNavController().popBackStack()
            }

            else if(it == 404 || it == 500)
                Toast.makeText(context, "ارتباط با سرور امکان پذیر نمی باشد.", Toast.LENGTH_SHORT).show()
        })

        //-----------------------------------------------------------------

        f32ViewModel.isLoading.observe(viewLifecycleOwner, {
            if(it)
                progressDialog.show()
            else
                progressDialog.dismiss()
        })

        //-----------------------------------------------------------------

        f32ViewModel.error.observe(viewLifecycleOwner, {
            if(it)
                Toast.makeText(context, "ارتباط با سرور امکان پذیر نمی باشد.", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        when (requestCode)
        {
            FishBun.FISHBUN_REQUEST_CODE -> if(resultCode == Activity.RESULT_OK)
            {
                val imagesPath = data!!.getParcelableArrayListExtra<Uri>("intent_path")
                var i = 0
                while (i < imagesPath!!.size)
                {
                    val fragment9Model = f32ModelFactory.create(i, imagesPath[i])
                    models.add(fragment9Model)
                    i++
                }
                f32Adapter.notifyDataSetChanged()
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
}