package com.example.rstaak.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rstaak.R
import com.example.rstaak.databinding.F52EditShopBinding
import com.example.rstaak.general.ImagePicker
import com.example.rstaak.viewModel.F52EditShopViewModel
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import javax.inject.Inject

@Suppress("DEPRECATION")
@AndroidEntryPoint
class F52EditShop : Fragment()
{
    lateinit var binding: F52EditShopBinding
    private lateinit var progressDialog: AlertDialog
    @Inject
    lateinit var imagePicker: ImagePicker
    private val imageList = ArrayList<String>()
    private val f52ViewModel: F52EditShopViewModel by viewModels()
    private val args: F52EditShopArgs by navArgs()
    @Inject
    lateinit var picasso: Picasso

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = F52EditShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        initProgressDialog()
        //------------------------------------------------

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = f52ViewModel
        }
        //------------------------------------------------

        val f4Model = args.f4Model

        Handler(Looper.getMainLooper()).postDelayed({

            binding.title.setText(f4Model.title)
            binding.description.setText(f4Model.description)
            binding.address.setText(f4Model.address)
            binding.phoneNumber.setText(f4Model.phoneNumber)
            if(f4Model.imageList.isNotEmpty())
            {
                imageList.add(f4Model.imageList[0])
                picasso.load(f4Model.imageList[0]).error(R.drawable.image).fit()
                    .into(binding.shopImage)
            }

        }, 300)

        //------------------------------------------------

        binding.addImage.setOnClickListener{

            FishBun.with(this).setImageAdapter(GlideAdapter())
                .setIsUseDetailView(false).setMaxCount(1).setMinCount(1)
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

        //------------------------------------------------

        binding.approve.setOnClickListener {
            if(imageList.size == 0)
                Toast.makeText(context, "یک عکس برای فروشگاه خود انتخاب کنید.", Toast.LENGTH_SHORT).show()

            else
                f52ViewModel.editShop(imageList, f4Model.shopId)
        }

        //------------------------------------------------

        f52ViewModel.isLoading.observe(viewLifecycleOwner, {
            if(it)
                progressDialog.show()
            else
                progressDialog.dismiss()
        })

        //------------------------------------------------

        f52ViewModel.response.observe(viewLifecycleOwner, {
            if(it == 200)
            {
                findNavController().previousBackStackEntry?.savedStateHandle?.set("editShop", true)
                findNavController().popBackStack()
            }

            else if(it == 404 || it == 500)
                Toast.makeText(context, "ارتباط با سرور امکان پذیر نمی باشد.", Toast.LENGTH_SHORT).show()
        })

        //------------------------------------------------

        f52ViewModel.error.observe(viewLifecycleOwner, {
            if(it)
                Toast.makeText(context, "ارتباط با سرور امکان پذیر نمی باشد..", Toast.LENGTH_SHORT).show()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        when (requestCode)
        {
            FishBun.FISHBUN_REQUEST_CODE -> if(resultCode == Activity.RESULT_OK)
            {
                val imagesPath = data!!.getParcelableArrayListExtra<Uri>("intent_path")
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imagesPath!![0])
                binding.shopImage.setImageBitmap(bitmap)
                binding.shopImage.scaleType = ImageView.ScaleType.FIT_XY
                val s: String = imagePicker.getStringImage(bitmap, 500)
                imageList.clear()
                imageList.add(s)
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