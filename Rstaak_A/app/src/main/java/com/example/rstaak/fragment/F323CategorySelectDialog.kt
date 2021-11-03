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
import com.example.rstaak.adapter.F323Adapter
import com.example.rstaak.databinding.F323CategorySelectDialogBinding
import com.example.rstaak.model.f323_category_select.F323Loading
import com.example.rstaak.model.f323_category_select.F323Model
import com.example.rstaak.model.f323_category_select.F323ParentModel
import com.example.rstaak.model.f323_category_select.F323Retry
import com.example.rstaak.viewModel.F323CategorySelectViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class F323CategorySelectDialog : BottomSheetDialogFragment(), F323Adapter.OnItemClickListener,
    F323Adapter.SetViewListener
{
    lateinit var binding: F323CategorySelectDialogBinding
    private val f323CategorySelectViewModel: F323CategorySelectViewModel by viewModels()
    @Inject
    lateinit var f323AdapterFactory: F323Adapter.Factory
    lateinit var f323Adapter: F323Adapter
    private var models = ArrayList<F323ParentModel>()
    @Inject
    lateinit var f323Loading: F323Loading
    @Inject
    lateinit var f323Retry: F323Retry
    @Inject
    lateinit var f323ModelFactory: F323Model.Factory
    lateinit var f323Model: F323Model

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.f323_category_select_dialog, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        //-----------------------------------------------------------------

        binding.lnr1.visibility = View.GONE

        //-----------------------------------------------------------------
        binding.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
        f323Adapter = f323AdapterFactory.create(models, this, this)
        binding.recyclerView.adapter = f323Adapter

        //-----------------------------------------------------------------

        f323CategorySelectViewModel.isLoading.observe(viewLifecycleOwner, {
            if(it)
            {
                models.clear()
                models.add(f323Loading)
                f323Adapter.notifyDataSetChanged()
            }
            else
            {
                models.clear()
                f323Adapter.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------

        f323CategorySelectViewModel.categoryModelData.observe(viewLifecycleOwner, {
            models.clear()
            models.addAll(it)
            f323Adapter.notifyDataSetChanged()
        })

        //-----------------------------------------------------------------

        f323CategorySelectViewModel.error.observe(viewLifecycleOwner, {
            if(it)
            {
                models.clear()
                models.add(f323Retry)
                f323Adapter.notifyDataSetChanged()
            }
        })

        //-----------------------------------------------------------------

        binding.back.setOnClickListener {
            binding.text1.visibility = View.VISIBLE
            binding.lnr1.visibility = View.GONE
            f323CategorySelectViewModel.categoryModelData.value = f323CategorySelectViewModel.categoryModel
        }
    }

    override fun onItemClick(view: View, model: F323Model?)
    {
        when(view.id)
        {
            R.id.lnr ->{
                if(model!!.child)
                {
                    findNavController().previousBackStackEntry?.savedStateHandle?.set("categoryModel", model)
                    findNavController().popBackStack()
                }

                else
                {
                    models.clear()
                    for (item in model.childCategories!!)
                    {
                        f323Model = f323ModelFactory.create(title = item.title, categoryId = item.categoryId, child = true, parentName = model.title)
                        models.add(f323Model)
                    }
                    f323Adapter.notifyDataSetChanged()
                }
            }

            R.id.retry -> {
                f323CategorySelectViewModel.getCategory()
            }
        }

    }

    override fun set(text: String)
    {
        binding.lnr1.visibility = View.VISIBLE
        binding.text1.visibility = View.GONE
        binding.text2.text = text
    }

}